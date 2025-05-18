package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
