package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

public interface DishService {

    //新增菜品和对应的口味
    void addDish(DishDTO dto);

    PageResult page(DishPageQueryDTO dto);
}
