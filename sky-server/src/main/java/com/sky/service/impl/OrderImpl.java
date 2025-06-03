package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.sky.entity.Orders;

@Service
@Transactional
public class OrderImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;


    private Orders orders;

    @Override
    public OrderSubmitVO submit(OrdersDTO ordersDTO) {

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);

        //补全字段
        orders.setNumber(UUID.randomUUID().toString());
        orders.setStatus(Orders.PENDING_PAYMENT); // 1，待付款
        orders.setUserId(BaseContext.getCurrentId()); //用户id
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//未支付
//        orders.setPayStatus(Orders.PAID);


        //获取地址簿的内容
        AddressBook add =  addressMapper.findDefault(orders.getUserId());
        if(add == null){
            throw new RuntimeException("请先设置默认地址");
        }
        orders.setPhone(add.getPhone());
        orders.setAddress(add.getDetail());//获取地址
        orders.setConsignee(add.getConsignee());

        orders.setRemark("不吃香菜(考研版)");//备注
        orders.setEstimatedDeliveryTime(LocalDateTime.now());//预计送达时间
        orders.setDeliveryStatus(1);//配送状态 1立即送出
        orders.setTablewareNumber(1);// 餐具数量
        orders.setTablewareStatus(1);// 1按餐量提供 0选择具体数量

        //主订单表插入数据
        orderMapper.insert(orders);
        Long id = orders.getId();
        orders.setId(id);

         // 2，从购物车中获取信息
        // 2.1 套餐中获取菜品
        List<ShoppingCart> lists = shopCartMapper.find(orders.getUserId());
        for(ShoppingCart list :lists){
            OrderDetail tail = new OrderDetail();
            tail.setOrderId(list.getUserId());
            tail.setName(list.getName());
            tail.setImage(list.getImage());
            tail.setDishFlavor(list.getDishFlavor());
            tail.setSetmealId(list.getSetmealId());
            tail.setDishFlavor(list.getDishFlavor());
            tail.setNumber(list.getNumber());
            tail.setAmount(list.getAmount());
            this.orders = orders;
            //订单明细表插入数据
            orderMapper.insertDetails(tail);
        }

        //3,购物车删除数据
        shopCartMapper.delete(orders.getUserId());

        // 构造返回结果
        OrderSubmitVO vo = new OrderSubmitVO();
        vo.setId(id);
        vo.setOrderNumber(orders.getNumber());
        vo.setOrderAmount(orders.getAmount());
        vo.setOrderTime(orders.getOrderTime());
        return vo;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        // 由于个人版并没有支付权限，所以绕开
        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));

        //个人版直接使用以下方式
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;// 订单状态，待接单
        Integer OrderPaidStatus = Orders.PAID; //支付状态：已支付
        LocalDateTime check_out_time = LocalDateTime.now(); //更新支付时间
        orderMapper.updateStatus(OrderStatus,OrderPaidStatus,check_out_time,this.orders.getId());



        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

}
