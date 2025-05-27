package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public void add(CategoryDTO cto) {
        Category category = new Category();
        BeanUtils.copyProperties(cto, category);

        category.setStatus(StatusConstant.DISABLE); //状态先禁用

  //      category.setCreateTime(LocalDateTime.now());
 //       category.setUpdateTime(LocalDateTime.now());

  //      category.setCreateUser(BaseContext.getCurrentId());
  //      category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.add(category);

    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO cto) {
        //开始分页查询
        PageHelper.startPage(cto.getPage(),cto.getPageSize());

        Page<Category> page = categoryMapper.pageQuery(cto);

        //3，解析结果，并封装
        long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void deletById(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public void updateById(CategoryDTO cto) {
        Category category = new Category();
        BeanUtils.copyProperties(cto, category);

    //    category.setUpdateTime(LocalDateTime.now());
    //    category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    @Override
    public void setStatus( Long id,Integer status ) {

        Category category = new Category();

        //使用实体类，给她id和状态
        category.setId(id);
        category.setStatus(status);

    //    category.setUpdateTime(LocalDateTime.now());
    //    category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.setStatus(category);
    }

    //判断单品或套餐
    @Override
    public List<Category> findByList(Integer type) {
        return categoryMapper.lists(type);

    }




}
