package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
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
//
//    @GetMapping("/workspace/overviewSetmeals")
//    public Result<?> overviewSetmeals() {
//        return Result.success("此接口暂未实现");
//    }
//
//    @GetMapping("/workspace/overviewDishes")
//    public Result<?> overviewDishes() {
//        return Result.success("此接口暂未实现");
//    }
//
//    @GetMapping("/order/conditionSearch")
//    public Result<?> conditionSearch() {return Result.success("此接口暂未实现");}
}