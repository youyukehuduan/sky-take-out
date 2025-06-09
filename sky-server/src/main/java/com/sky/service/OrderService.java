package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {


    OrderSubmitVO submit(OrdersSubmitDTO dto);


    OrderPaymentVO payment(OrdersPaymentDTO dto) throws Exception;


    void paySuccess(String outTradeNo);


    PageResult historyOrders(Integer page,Integer pageSize,Integer status);

    OrderVO orderDetail(Long id);

    void cancel(Integer id);

    void repetion(Integer id);

    void reminder(Integer id);

    //分页查询
    PageResult page(OrdersPageQueryDTO dto);


    OrderStatisticsVO statistics( );

    void confirm(OrdersConfirmDTO dto);

    //派送
    void delivery(Long id);
    // & 完成
    void complete(Long id);

    //取消订单
    void cancelOrder(OrdersCancelDTO dto);

    //拒单
    void rejection(OrdersRejectionDTO dto);
}
