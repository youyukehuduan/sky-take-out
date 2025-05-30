package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车相关接口")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShopService shopService;



    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO dto) {
        log.info("添加一个商品到购物车：{}",dto);

         shopService.add(dto);
         return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("清空物品")
    public Result sub(@RequestBody ShoppingCartDTO dto){
        log.info("清空一个商品到购物车：{}",dto);
        shopService.sub(dto);
        return Result.success();
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result delete(){
        log.info("清空购物车");

        shopService.delete();
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result list(){
        log.info("查看购物车");
        List<ShoppingCart> sc = shopService.list();
        return Result.success(sc);
    }

}