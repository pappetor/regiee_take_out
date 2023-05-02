package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        if(dishId != null){
            //传过来的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //传过来的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one == null){
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(userId);
            shoppingCartService.save(shoppingCart);
        }else{
            Integer number = one.getNumber();
            one.setNumber(number +1);
            shoppingCartService.updateById(one);
        }
        return R.success(shoppingCart);
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        if(dishId != null){
            //说明操作的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            Long setmealId = shoppingCart.getSetmealId();
            //说明操作的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        Integer number = one.getNumber();
        if(number > 1){
            one.setNumber(number-1);
            shoppingCartService.updateById(one);
        }else{
            shoppingCartService.removeById(one);
        }
        return R.success("减少菜品或套餐成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");

    }

}
