package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class WorkSpaceSerViceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;




    /**
     * 获取当日营业数据（调用通用方法）
     *
     * @return BusinessDataVO
     */
    public BusinessDataVO business() {
        // 获取当天的开始时间和结束时间
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);

        return getBusinessData(todayStart, todayEnd);
    }
    /**
     * 获取营业数据（支持任意时间段）
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return BusinessDataVO
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        // 总订单数
        Integer totalOrderCount = orderMapper.countByMap(map);

        // 设置状态为已完成，查询营业额和有效订单数
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map); // 营业额
        Integer validOrderCount = orderMapper.countByMap(map); // 有效订单数

        turnover = turnover == null ? 0.0 : turnover;
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;

        // 订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != null && totalOrderCount > 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        // 平均客单价
        Double unitPrice = 0.0;
        if (validOrderCount > 0) {
            unitPrice = turnover / validOrderCount;
        }

        // 新增用户数
        Integer newUsers = userMapper.countByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers == null ? 0 : newUsers)
                .build();
    }





    @Override
    public OrderOverViewVO overOrder() {
        return orderMapper.getTodayBusiness();
    }

    @Override
    public DishOverViewVO dishes() {
        return dishMapper.getTodayDish();
    }

    @Override
    public SetmealOverViewVO setmeal() {
        return setmealMapper.getTodaySetMeal();
    }
}
