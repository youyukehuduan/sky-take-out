package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService {

    void add(SetmealDTO sto);

    PageResult page(SetmealPageQueryDTO dto);

    void setMeal(Integer status,Long id);

    SetmealVO getByListId(Long id);

    void update(SetmealDTO sto);

    void delete(List<Long> ids);
}
