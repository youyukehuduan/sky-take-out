package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sky.service.OrderService;

@RestController
@RequestMapping("user/order")
@Api(tags = "订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/submit")
    @ApiOperation("用户下单")
    public Result orderSubmit(@RequestBody OrdersDTO ordersDTO){
        log.info("用户下单");
        OrderSubmitVO vo = orderService.submit(ordersDTO);
        return Result.success(vo);
    }
}
