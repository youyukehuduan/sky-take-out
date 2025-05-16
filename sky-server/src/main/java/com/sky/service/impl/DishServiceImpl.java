package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Transactional
    @Override
    public void addDish(DishDTO DTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(DTO, dish);

        //向菜品表插入1条数据
        dishMapper.insert(dish);//后绪步骤实现


        //新增菜品


    }
}
