package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkSpaceSerViceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public BusinessDataVO business() {
    //获得当天的时间
        LocalDateTime time = LocalDateTime.now();
        BusinessDataVO vo = new BusinessDataVO();

        //营业额
        Double money = orderMapper.getTodayMoney(time);
        vo.setTurnover(money == null ? 0 : money);

        //有效订单数
        Integer takeOrder = orderMapper.getTodayOrder(time);
        takeOrder = takeOrder == null ? 0 : takeOrder;
        vo.setValidOrderCount(takeOrder );

        //总订单数
        Integer allOrder = orderMapper.getTodayAllOrder(time) == null ? 0 : orderMapper.getTodayAllOrder(time);

        //订单完成率
        double rate = 0.0;
        if(allOrder != 0){
            rate = (double) takeOrder / allOrder;
        }
        vo.setOrderCompletionRate(rate);

        //平均客单价  营业额/有效订单数
        double avg = 0.0;
        if(takeOrder == 0){
            avg = 0;
        }else{
            avg = money / takeOrder;
        }
        vo.setUnitPrice(avg);

        //newUsers 新增用户数
        Integer user = userMapper.getNewUser(time);
        vo.setNewUsers(user == null ? 0 : user);

        return vo;
    }

    @Override
    public OrderOverViewVO overOrder() {
        return orderMapper.getTodayBusiness();
    }
}
