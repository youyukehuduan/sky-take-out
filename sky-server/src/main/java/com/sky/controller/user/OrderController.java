package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sky.service.OrderService;

@RestController("userOrderController")
@RequestMapping("user/order")
@Api(tags = "订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/submit")
    @ApiOperation("用户下单")
    public Result orderSubmit(@RequestBody OrdersSubmitDTO dto){
        log.info("用户下单了,相关的信息为：{}",dto.toString());

        OrderSubmitVO vo = orderService.submit(dto);
        return Result.success(vo);
    }
    /**
     * 订单支付
     *
     * @param
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO dto) throws Exception {
        log.info("订单支付：{}", dto);
        OrderPaymentVO orderPaymentVO = orderService.payment(dto);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }


    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result historyOrders(Integer page,Integer pageSize,Integer Status) {
        log.info("历史订单查询");
         PageResult pageResult = orderService.historyOrders(page,pageSize,Status);
         return Result.success(pageResult);
    }

    //查询订单详情
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result orderDetail(@PathVariable Long id) {
    log.info("查询订单详情");
        OrderVO vo = orderService.orderDetail(id);
        return Result.success(vo);
    }

    //取消订单
    @PutMapping("/cancel/{id}")
    @ApiOperation("订单取消")
    public Result cancel(@PathVariable Integer id) {
        log.info("取消订单");
        orderService.cancel(id);
        return Result.success();
    }

    //再来一单
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Integer id) {
        orderService.repetion(id);
        return Result.success();
    }
    //催单
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }


}
