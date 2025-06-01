package com.sky.service.impl;

import com.sky.dto.OrdersDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public OrderSubmitVO submit(OrdersDTO ordersDTO) {

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);

        //订单表插入数据
        orderMapper.insert(orders);





    }
}
