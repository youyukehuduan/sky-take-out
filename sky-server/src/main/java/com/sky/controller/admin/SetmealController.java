package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐设置")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#sto.categoryId")// setmealCache::100
    public Result addSetMeal(@RequestBody SetmealDTO sto) {
        log.info("新增套餐");
        setmealService.add(sto);
        return Result.success();
    }

    @GetMapping("page")
    @ApiOperation("分页查询套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true) //清除该名称下所有缓存
    public Result Pagequery(SetmealPageQueryDTO dto){
        log.info("套餐查询");
        PageResult pageResult = setmealService.page(dto);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、禁售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateSetMeal(@PathVariable Integer status,Long id){
        log.info("套餐起售、禁售");
        setmealService.setMeal(status,id);
        return Result.success();
    }

    @GetMapping("{id}")
    @ApiOperation("根据ID查套餐")
    public Result getByListId(@PathVariable Long id){
        log.info("根据ID查套餐");
        SetmealVO vo = setmealService.getByListId(id);
        return Result.success(vo);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO sto){
        log.info("修改套餐，{}",sto);
        setmealService.update(sto);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return Result.success();
    }

}
