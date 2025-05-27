package com.sky.controller.admin;

import com.sky.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class TempWorkspaceController {

    @GetMapping("/workspace/businessData")
    public Result<?> businessData() {
        return Result.success("此接口暂未实现");
    }

    @GetMapping("/workspace/overviewOrders")
    public Result<?> overviewOrders() {
        return Result.success("此接口暂未实现");
    }

    @GetMapping("/workspace/overviewSetmeals")
    public Result<?> overviewSetmeals() {
        return Result.success("此接口暂未实现");
    }

    @GetMapping("/workspace/overviewDishes")
    public Result<?> overviewDishes() {
        return Result.success("此接口暂未实现");
    }

    @GetMapping("/order/conditionSearch")
    public Result<?> conditionSearch() {return Result.success("此接口暂未实现");}
}