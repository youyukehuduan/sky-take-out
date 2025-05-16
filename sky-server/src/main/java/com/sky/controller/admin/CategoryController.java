package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.wechat.pay.contrib.apache.httpclient.notification.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.sky.result.Result.success;


//分类管理
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController{

    @Autowired
    private CategoryService categoryService;


    //菜品新增分类
    @PostMapping
    @ApiOperation("新增分类")
    public Result add(@RequestBody CategoryDTO cto){
        log.info("新增分类：{}",cto);
        categoryService.add(cto);
        return Result.success();
    }

    //菜品分页查询
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO cto){
        log.info("菜品分类分页查询：页码={}, 每页大小={}, 名称={}, 类型={}",
                cto.getPage(), cto.getPageSize(), cto.getName(), cto.getType());

        PageResult pageResult = categoryService.pageQuery(cto);
        return Result.success(pageResult);
    }

    //删除菜品分类
    @DeleteMapping
    @ApiOperation("菜品分类删除")
    public Result deleteById(Long id){
        log.info("根据id删除分类:{}",id);
         categoryService.deletById(id);
        return Result.success();
    }

    //修改菜品
    @PutMapping
    @ApiOperation("修改菜品分类")
    public Result updateById(@RequestBody CategoryDTO cto){
        log.info("修改菜品分类:{}",cto);
        categoryService.updateById(cto);
        return Result.success();
    }

    //启用禁用
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    public Result updateStatus(@PathVariable Integer status,Long id){
        log.info("id:{},菜品状态：{}",id,status);
        categoryService.setStatus(id,status);
        return Result.success();
    }

    //菜品分类  由于文档错误，此接口仍然被page查询接口占据，目前失效
    @GetMapping("/list")
    @ApiOperation("类型查询分类")
    public Result findByList(Integer type){
        log.info("正在查询菜品分类：{}",type);
        List<Category> lists = categoryService.findByList(type);
        return Result.success(lists);
    }

}
