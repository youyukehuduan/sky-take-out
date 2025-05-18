package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
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
}
