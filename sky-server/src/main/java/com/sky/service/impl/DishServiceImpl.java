package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealdishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;

import java.util.ArrayList;
import java.util.List;

import com.sky.vo.DishVO;
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

    @Autowired
    private SetmealdishMapper setmealdishMapper;

    @Transactional
    @Override
    public void addDish(DishDTO DTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(DTO, dish);

        //向菜品表插入1条数据
        dishMapper.insert(dish);//后绪步骤实现

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        //新增口味
       List<DishFlavor> lists = DTO.getFlavors();
        if(lists != null && lists.size() > 0){
           for(DishFlavor df : lists){
               df.setDishId(dishId);
               log.info("设置口味的dishId为: {}", df.getDishId()); // 添加日志确认
           }
       }

        dishFlavorMapper.insert(lists);
    }

    @Override
    public PageResult page(DishPageQueryDTO dto) {
        //开始分页查询
        PageHelper.startPage(dto.getPage(),dto.getPageSize());

        //Page 是 PageHelper 提供的扩展类，继承自 ArrayList，包含分页元数据（总记录数、总页数等）。
        Page<DishVO> page = dishMapper.pageQuery(dto);

        //解析结果
//        long total = page.getTotal();
//        List<DishVO> lists = page.getResult();
//        return new PageResult(total,lists);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public void delete(List<Long> ids) {
        //判断当前菜品是否在售中，在手中无法删除
        for(Long id : ids){
           Dish dish = dishMapper.getById(id);
           //在售中，无法删除
           if(dish.getStatus() == StatusConstant.ENABLE){
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
        }
        //判断是否关联
        List<Long> lists =  setmealdishMapper.getById(ids);
        if(lists != null && lists.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        for(Long id : ids){
            //删除菜品
            dishMapper.deleteById(id);
            //删除口味
            dishFlavorMapper.deleteById(id);
        }
    }

    @Override
    public DishVO getById(Long id) {

        //根据id查询菜品数据
        Dish DTO = dishMapper.getById(id);
        //获取口味
        List<DishFlavor> lists = dishFlavorMapper.getById(id);

        //将查询到的数据封装到VO
        DishVO vo = new DishVO();
        BeanUtils.copyProperties(DTO, vo);
        vo.setFlavors(lists);

        return vo;
    }

    @Override
    public void update(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        // 修改菜品基本信息
        dishMapper.update(dish);

        //删除原有的口味数据
        dishFlavorMapper.deleteById(dto.getId());

        //插入新口味数据
        List<DishFlavor> lists =  dto.getFlavors();
        if(lists != null && lists.size() > 0){
            for(DishFlavor df :lists){
                df.setDishId(dish.getId());
            }
            //口味表插入n条数据
            dishFlavorMapper.insert(lists);
        }



    }


    //根据分类Id查询菜品
    @Override
    public List<DishVO> getByList(Long id) {
        List<Dish> dishs = dishMapper.getByCategoryId(id);

        List<DishVO> vos = new ArrayList<>();

        for(Dish d : dishs){
            DishVO vo = new DishVO();
            BeanUtils.copyProperties(d, vo);
            List<DishFlavor> dfs = dishFlavorMapper.getById(d.getId());
            vo.setFlavors(dfs); //设置口味
            vos.add(vo);
        }
        return vos;


    }


}
