package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//菜品管理
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result AddDish(@RequestBody DishDTO DTO){
        log.info("新增菜品:{}",DTO);
        dishService.addDish(DTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result page(DishPageQueryDTO dto){
        log.info("菜品查询:{}",dto);
        PageResult pageResult =  dishService.page(dto);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result Delete(@RequestParam List<Long> ids){
        log.info("删除菜品:{}",ids);
         dishService.delete(ids);
         return Result.success();
    }

    @GetMapping("{id}")
    public Result getById(@PathVariable Long id){
        log.info("根据ID查询菜品");
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO DTO){
        log.info("更新菜品");
        dishService.update(DTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result getByList(@RequestParam Long categoryId){
        log.info("根据分类Id查询菜品:{}",categoryId);
        List<DishVO>  vos = dishService.getByList(categoryId);
        return Result.success(vos);
    }




}
