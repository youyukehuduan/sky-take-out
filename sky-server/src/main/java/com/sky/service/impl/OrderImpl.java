package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.sky.entity.Orders;

@Service
@Transactional
@Slf4j
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

    @Autowired
    private WebSocketServer webSocketServer;


    private Orders orders;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {

        //处理异常字段
        AddressBook addressBook = addressMapper.getById(dto.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //构造订单字段
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));// 用户ID
        orders.setStatus(Orders.PENDING_PAYMENT); // 1，待付款
        orders.setUserId(BaseContext.getCurrentId()); //用户id
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//未支付
        //地址簿
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        this.orders = orders; //此处不删除，订单支付需要用到
        //主订单表插入数据
        orderMapper.insert(orders);

        //订单明细数据
        //查询购物车明细
        List<OrderDetail> lists = new ArrayList<>();
        List<ShoppingCart> shopCartLists = shopCartMapper.find(orders.getUserId());//购物车跟用户id绑定

        for(ShoppingCart list :shopCartLists){
            OrderDetail tail = new OrderDetail();

            BeanUtils.copyProperties(list, tail);
            tail.setOrderId(orders.getId());

            //订单明细表插入数据
            orderMapper.insertDetails(tail);
        }

        //3,购物车删除数据
        shopCartMapper.delete(orders.getUserId());

        // 构造返回结果
        OrderSubmitVO vo = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return vo;
    }

    /**
     * 订单支付
     *
     * @param
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO dto) throws Exception {
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

        //通过WebSocket实现来单提醒，向客户端浏览器推送消息
        // paySuccess 被跳过，不会生效
            log.info("支付了12345");
            Map map = new HashMap();
            map.put("type",1);//消息类型，1表示来单提醒
            map.put("orderId",orders.getId());
            map.put("content","订单号：" + this.orders.getNumber());
            log.info("websocket向前端返回数据:{}",map);
            webSocketServer.sendToAllClient(JSON.toJSONString(map));
        //

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        //paySuccess 该方法被跳过，不会生效！
        // socket连接,前端响应
        // 根据订单号查询订单
        Orders  ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过WebSocket实现来单提醒，向客户端浏览器推送消息
        Map map = new HashMap();
        map.put("type",1);//消息类型，1表示来单提醒
        map.put("orderId",orders.getId());
        map.put("content","订单号：" + outTradeNo);
        log.info("websocket向前端返回数据");
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
        //


    }

    @Override
    public PageResult historyOrders(Integer page,Integer pageSize,Integer status){

        // 1,开始分页查询
        PageHelper.startPage(page,pageSize);

        // 构建查询条件
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();
        dto.setStatus(status);
        dto.setUserId(BaseContext.getCurrentId());

        // 查询订单主表（orders）
        Page<Orders> pages =  orderMapper.historyOrders(dto);

        List<OrderVO> lists = new ArrayList<OrderVO>();

        if(pages != null && pages.size() > 0){
            for(Orders order : pages){
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(order,vo); //拷贝基础属性

                //查询订单明细
                List<OrderDetail> orders = orderMapper.getByOrderId(order.getId());
                vo.setOrderDetailList(orders);

                lists.add(vo);
            }
        }
        return new PageResult(pages.getTotal(),lists);

    }

    @Override
    public OrderVO orderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders,vo);
        //去菜品详细表中查询
        Long user_id = BaseContext.getCurrentId();
        List<OrderDetail> details = orderMapper.getByOrderId(user_id);

        vo.setOrderDetailList(details);
//        for(OrderDetail detail :details){
//
//        }

        return vo;
    }

    @Override
    public void cancel(Integer id) {
        orderMapper.cancelOrders(id);

    }

    @Override
    public void repetion(Integer id) {
        //再来一单
    }


    //用户催单
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        // paySuccess 被跳过，不会生效
        Map map = new HashMap();
        map.put("type",2);//消息类型，1表示来单提醒
        map.put("orderId",id);
        map.put("content","订单号：" + orders.getNumber());
//        log.info("websock 用户催单:{}",map);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
        //
    }

    @Override
    public PageResult page(OrdersPageQueryDTO dto) {
        //原神启动！
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        // 查询订单列表（Orders）
        Page<Orders> page = orderMapper.find(dto);
        // 将 Orders 转换为 OrderVO（假设你有工具类或者手动转换）
        List<OrderVO> lists = new ArrayList<>();

        for(Orders order : page){
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order,vo);


            List<OrderDetail> alists = orderMapper.getByOrderId( order.getId());
            vo.setOrderDetailList(alists);
//            List<OrderDetail> olists =  vo.getOrderDetailList(orderMapper.getByOrderId(order.getId()));
//            vo.setOrderDetailList(olists);
            lists.add(vo);

        }

        return new PageResult(page.getTotal(),lists);

    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO osvo = new OrderStatisticsVO();
        return orderMapper.statistics();
    }

    @Override
    public void confirm(OrdersConfirmDTO dto) {

        dto.setStatus(orders.CONFIRMED);
        orderMapper.transfer(dto);
    }
    //派送
    @Override
    public void delivery(Long id) {
        OrdersConfirmDTO dto = new OrdersConfirmDTO();
        dto.setStatus(orders.DELIVERY_IN_PROGRESS);
        dto.setId(id);

        orderMapper.transfer(dto);
    }

    @Override
    public void complete(Long id) {
        OrdersConfirmDTO dto = new OrdersConfirmDTO();
        dto.setStatus(orders.COMPLETED);
        dto.setId(id);

        orderMapper.transfer(dto);
    }

    //取消订单
    @Override
    public void cancelOrder(OrdersCancelDTO dto) {
         Orders orders =  orderMapper.getById(dto.getId());
        // 如果当前用户已经支付，执行退款 操作，目前略过
        //         if(orders.getPayStatus() == Orders.PAID){
        //
        //         }
         //根据id设置订单
         orders.setStatus(Orders.CANCELLED);
         orders.setCancelReason(dto.getCancelReason());
         orders.setCancelTime(LocalDateTime.now());
         orders.setPayStatus(Orders.REFUND);

        orderMapper.adminCancel(orders);
    }

        //拒单
    @Override
    public void rejection(OrdersRejectionDTO dto){
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto,orders);
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(dto.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setPayStatus(Orders.REFUND);
        orderMapper.update(orders);





        //执行退款操作
        //xxxx

    }


}
