package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    //处理超时订单
    @Scheduled(cron = " 0 * * * * ?") //每分钟执行一次
    public void processTimeoutOrder(){
        log.info("处理超时订单:{}", new Date());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> lists = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);
        if(lists != null && lists.size() > 0){
            lists.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("支付超时，自动取消");
                orderMapper.update(order);
            });
        }
    }

    //处理“派送中”状态的订单  1个小时后字节完成
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单：{}", new Date());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> lists = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        lists.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        });
    }
}
