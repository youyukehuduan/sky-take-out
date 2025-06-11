package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台")
public class TempWorkspaceController {

    @Autowired
    private WorkSpaceService workspaceService;

    @GetMapping("businessData")
    @ApiOperation("今日一览")
    public Result businessData() {

        BusinessDataVO vo = workspaceService.business();
        return Result.success(vo);
    }


    @GetMapping("/overviewOrders")
    @ApiOperation("订单管理")
    public Result overviewOrders() {
        OrderOverViewVO vo = workspaceService.overOrder();
        return Result.success(vo);
    }


    @GetMapping("/overviewSetmeals")
    @ApiOperation("套餐总览")
    public Result overviewSetmeals() {
        SetmealOverViewVO vo = workspaceService.setmeal();
        return Result.success(vo);
    }

    @GetMapping("/overviewDishes")
    @ApiOperation("菜品总览")
    public Result overviewDishes() {
        DishOverViewVO vo = workspaceService.dishes();
        return Result.success(vo);
    }
//
//    @GetMapping("/order/conditionSearch")
//    public Result<?> conditionSearch() {return Result.success("此接口暂未实现");}
}