package com.sky.controller.user;


import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("user/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService  categoryService;

    @GetMapping("/list")
    @ApiOperation("C端-分类接口")
    public Result list( Integer type){




        log.info("查询分类:要查询喽~");
        List<Category> lists = categoryService.findByList(type);
        return Result.success(lists);
    }


}
