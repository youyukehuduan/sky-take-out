package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车相关接口")
@Slf4j
public class ShoppingCartController {

    @GetMapping("/list")
    @ApiOperation("获取购物车商品列表")
    public Result list() {
        log.info("获取购物车商品列表");
        // 返回空列表作为占位，后续可以替换为真实数据
        return Result.success(Collections.emptyList());
    }
}