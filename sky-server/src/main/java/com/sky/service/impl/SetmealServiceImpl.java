package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.SetmealdishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;

import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;


    @Autowired
    private SetmealdishMapper setmealdishMapper;

    @Override
    public void add(SetmealDTO sto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(sto, setmeal);

        //向套餐表插入数据
        setmealMapper.add(setmeal);

        //向套餐表插入数据
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealdishs = sto.getSetmealDishes();

        for(SetmealDish setmealDish : setmealdishs){
            setmealDish.setSetmealId(setmealId);
        }
        setmealdishMapper.insert(setmealdishs);


    }

    @Override
    public PageResult page(SetmealPageQueryDTO dto) {
        //开始分页查询
        PageHelper.startPage(dto.getPage(),dto.getPageSize());

        Page<SetmealVO> page = setmealMapper.page(dto);
        //结果封装
        return new PageResult(page.getTotal(),page.getResult());


    }

    @Override
    public void setMeal(Integer status,Long id) {
        setmealMapper.setStatus(status,id);
    }

    @Override
    public SetmealVO  getByListId(Long id) {

        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> lists = setmealdishMapper.getBymealId(id);

        SetmealVO vo = new SetmealVO();
        BeanUtils.copyProperties(setmeal,vo);
        vo.setSetmealDishes(lists);
        return vo;

    }

    @Override
    public void update(SetmealDTO sto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(sto, setmeal);

        setmealMapper.update(setmeal);
    }

    @Override
    public void delete(List<Long> ids) {

        setmealMapper.delete(ids);
    }
}
