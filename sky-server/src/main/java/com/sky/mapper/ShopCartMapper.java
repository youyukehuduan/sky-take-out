package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShopCartMapper {


    public List<ShoppingCart> list(ShoppingCart shoppingCart);


    public ShoppingCart add(ShoppingCart shoppingCart);

    //更新商品数据
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{id}")
    void delete(Long id);

    @Select("select * from shopping_cart where user_id = #{id} ")
    List<ShoppingCart> find(Long id);
}
