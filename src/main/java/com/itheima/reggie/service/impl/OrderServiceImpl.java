package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Orders> implements OrderService {
}
