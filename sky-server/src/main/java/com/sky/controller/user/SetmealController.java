package com.sky.controller.user;


import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端-套餐浏览接口 ")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("{id}")
    @ApiOperation("根据ID查套餐")
    @Cacheable("")
    public Result getByListId(@PathVariable Long id){
        log.info("根据ID查套餐所含的菜品");
        SetmealVO vo = setmealService.getByListId(id);
        return Result.success(vo);
    }

    //4 根据分类ID查询套餐包含的菜品
    @GetMapping("/dish/{setmeal_id}")
    @ApiOperation("根据ID查询套餐包含的菜品")
    public Result getDishByCategory(@PathVariable Long setmeal_id){

        List<SetmealDish> vos = setmealService.getDishByCategoryId(setmeal_id);
        return Result.success(vos);

    }

    //3 根据分类id查询套餐
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId") // key:setmealCache::1
    public Result getList( Long categoryId){
        log.info("根据分类ID查套餐");
        List<SetmealVO> vos = setmealService.getInfoById(categoryId);
        return Result.success(vos);
    }


}
