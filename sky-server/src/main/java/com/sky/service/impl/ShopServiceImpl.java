package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.mapper.ShopMapper;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public void add(ShoppingCartDTO dto) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(dto,cart);

        // 设置当前用户ID
        cart.setUserId(BaseContext.getCurrentId());

        // 查询当前商品是否已存在
        List<ShoppingCart> existingItems = shopCartMapper.list(cart);

        if (existingItems != null && !existingItems.isEmpty()) {
            // 已存在，数量+1
            ShoppingCart existingCart = existingItems.get(0);
            existingCart.setNumber(existingCart.getNumber() + 1);
            shopCartMapper.updateNumberById(existingCart);
        } else {
            // 不存在，插入新数据
            Long dishId = dto.getDishId();

            if (dishId != null) {
                // 添加的是菜品
                Dish dish = dishMapper.getById(dishId);
                cart.setName(dish.getName());
                cart.setImage(dish.getImage());
                cart.setAmount(dish.getPrice());
            } else {
                // 添加的是套餐
                Long setmealId = dto.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                cart.setName(setmeal.getName());
                cart.setImage(setmeal.getImage());
                cart.setAmount(setmeal.getPrice());
            }

            // 设置默认字段
            cart.setId(null); // 确保插入新记录
            cart.setNumber(1);
            cart.setCreateTime(LocalDateTime.now());



            // 插入数据库
            shopCartMapper.insert(cart);
        }
    }

    @Override
    //清空购物车
    public void delete() {
        Long id = BaseContext.getCurrentId();
        shopCartMapper.delete(id);
    }

    @Override
    //显示购物车
    public List<ShoppingCart> list() {
        Long id = BaseContext.getCurrentId(); //获取当前账号的ID
        List<ShoppingCart> carts = shopCartMapper.find(id);
        return carts;
    }

    @Override
    public void sub(ShoppingCartDTO dto) {
        ShoppingCart sc = new ShoppingCart();
        BeanUtils.copyProperties(dto,sc);
        sc.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> lists = shopCartMapper.list(sc);


            ShoppingCart cart = lists.get(0);
            cart.setNumber(cart.getNumber() - 1);
            //更新新的数量
            shopCartMapper.updateNumberById(cart); // Java值改变，未改变数据库
            if(cart.getNumber() == 0) {
                log.info("减到零了，原神，启动");
                shopCartMapper.delete(cart.getId());
            }

        cart.setCreateTime(LocalDateTime.now());
    }


}
