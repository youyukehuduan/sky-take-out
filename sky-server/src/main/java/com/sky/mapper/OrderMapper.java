package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    void insertDetails(OrderDetail orderDetail);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);

    Page<Orders> historyOrders(OrdersPageQueryDTO dto);


    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long id);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    void cancelOrders(Integer id);


    Page<Orders> find(OrdersPageQueryDTO dto);

    OrderStatisticsVO statistics();


    //订单状态改变
    @Update("Update orders set status = #{status} where id = #{id} ")
    void transfer(OrdersConfirmDTO dto);

    @Update("Update orders set cancel_reason = #{cancelReason},status = #{status}," +
            "cancel_time =#{cancelTime},pay_status = #{payStatus} where id = #{id} ")
    void adminCancel(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);
}
