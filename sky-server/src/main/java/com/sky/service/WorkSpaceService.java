package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    BusinessDataVO business();

    OrderOverViewVO overOrder();

    DishOverViewVO dishes();

    SetmealOverViewVO setmeal();

    //根据时间段统计营业数据
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);
}
