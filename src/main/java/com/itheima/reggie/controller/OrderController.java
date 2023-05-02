package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    UserService userService;
    @Autowired
    AddressBookService addressBookService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        User user = userService.getById(userId);
        //设置user_id
        orders.setUserId(userId);
        //设置status
        orders.setStatus(2);
        //设置number
        long orderId = IdWorker.getId();
        orders.setNumber(String.valueOf(orderId));
        //设置amount
        AtomicInteger amount = new AtomicInteger(0);
        //设置order_time check_time
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        //设置phone
        orders.setPhone(addressBook.getPhone());
        //设置address
        orders.setAddress(addressBook.getDetail());
        //设置user_name
        orders.setUserName(user.getName());
        //设置consignee  收货人
        orders.setConsignee(addressBook.getConsignee());

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        List<OrderDetail> orderDetailList =  list.stream().map(item->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            orderDetail.setAmount(item.getAmount());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orderDetailService.saveBatch(orderDetailList);
        orderService.save(orders);
        shoppingCartService.remove(queryWrapper);
        return null;
    }
}
