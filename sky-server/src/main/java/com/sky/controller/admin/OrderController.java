package com.sky.controller.admin;

import com.github.pagehelper.PageHelper;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单查询")
    public Result page(OrdersPageQueryDTO dto){
        PageResult pageResult = orderService.page(dto);
        return Result.success(pageResult);
    }

    //各个状态的订单数量统计
    @GetMapping("/statistics")
    @ApiOperation("订单数量统计")
    public Result statistics(){
        OrderStatisticsVO vo = orderService.statistics();
        return Result.success(vo);
    }

    // 接单
    @PutMapping("confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO dto){
        log.info("传入参数为：{}",dto);
        orderService.confirm(dto);
        return Result.success();
    }

    //派送
    @PutMapping("delivery/{id}")
    @ApiOperation("派送")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    //完成
    @PutMapping("complete/{id}")
    @ApiOperation("完成")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }

    //查询订单详情
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result orderDetail(@PathVariable Long id) {
        log.info("查询订单详情");
        OrderVO vo = orderService.orderDetail(id);
        return Result.success(vo);
    }

    //取消订单
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO dto) {

       orderService.cancelOrder(dto);
        return Result.success();
    }

    //拒单
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO dto){
        log.info("拒单:{}",dto);
        orderService.rejection(dto);
        return Result.success();
    }

}
