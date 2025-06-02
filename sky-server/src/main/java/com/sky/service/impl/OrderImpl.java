package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Override
    public OrderSubmitVO submit(OrdersDTO ordersDTO) {

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);

        //补全字段
        orders.setNumber(UUID.randomUUID().toString());
        orders.setStatus(Orders.PENDING_PAYMENT); // 1，待付款
        orders.setUserId(BaseContext.getCurrentId()); //用户id
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//未支付
//        orders.setPayStatus(Orders.PAID);


        //获取地址簿的内容
        AddressBook add =  addressMapper.findDefault(orders.getUserId());
        if(add == null){
            throw new RuntimeException("请先设置默认地址");
        }
        orders.setPhone(add.getPhone());
        orders.setAddress(add.getDetail());//获取地址
        orders.setConsignee(add.getConsignee());

        orders.setRemark("不吃香菜(考研版)");//备注
        orders.setEstimatedDeliveryTime(LocalDateTime.now());//预计送达时间
        orders.setDeliveryStatus(1);//配送状态 1立即送出
        orders.setTablewareNumber(1);// 餐具数量
        orders.setTablewareStatus(1);// 1按餐量提供 0选择具体数量
        //主订单表插入数据
        orderMapper.insert(orders);
        Long id = orders.getId();
        orders.setId(id);

         // 2，从购物车中获取信息
        // 2.1 套餐中获取菜品
        List<ShoppingCart> lists = shopCartMapper.find(orders.getUserId());
        for(ShoppingCart list :lists){
            OrderDetail tail = new OrderDetail();
            tail.setOrderId(list.getUserId());
            tail.setName(list.getName());
            tail.setImage(list.getImage());
            tail.setDishFlavor(list.getDishFlavor());
            tail.setSetmealId(list.getSetmealId());
            tail.setDishFlavor(list.getDishFlavor());
            tail.setNumber(list.getNumber());
            tail.setAmount(list.getAmount());
            //订单明细表插入数据
            orderMapper.insertDetails(tail);
        }

        //3,购物车删除数据
        shopCartMapper.delete(orders.getUserId());

        // 构造返回结果
        OrderSubmitVO vo = new OrderSubmitVO();
        vo.setId(id);
        vo.setOrderNumber(orders.getNumber());
        vo.setOrderAmount(orders.getAmount());
        vo.setOrderTime(orders.getOrderTime());
        return vo;
    }
}
