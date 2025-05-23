package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    //新增菜品和对应的口味
    void addDish(DishDTO dto);

    PageResult page(DishPageQueryDTO dto);

    void delete(List<Long> ids);

    DishVO getById(Long id);

    void update(DishDTO dto);

    List<DishVO> getByList(Long id);
}
