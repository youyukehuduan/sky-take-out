package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品")
    public Result getByList(@RequestParam Long categoryId){
        log.info("根据分类Id查询菜品:{}",categoryId);

        //构造redis中的key,dish_分类Id
        String key = "dish" + categoryId;

        //查询redis中是否存在菜品数据
        List<DishVO> lists = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if(lists != null && lists.size() > 0){
            //如果存在，直接返回，无须查询数据库
            log.info("根据分类Id查询菜品,已从redis中获取:{}",lists);
            return Result.success(lists);
        }

        List<DishVO> vos = dishService.getByList(categoryId);

        //如果不存在，将查询到的数据放入redis中 加上 categoryId 是因为菜品是按照分类来展示的，不同分类下的菜品不一样
        redisTemplate.opsForValue().set(key, vos);

        return Result.success(vos);
    }
}
