package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface CategoryService {



    void add(CategoryDTO cto);

    PageResult pageQuery(CategoryPageQueryDTO cto);

    void deletById(Long id);

    void updateById(CategoryDTO cto);

    void setStatus(Long id,Integer status );

    List<Category> findByList(Integer type);
}





