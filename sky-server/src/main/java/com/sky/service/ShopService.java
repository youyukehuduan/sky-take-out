package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShopService {

    //添加购物车
    void add(ShoppingCartDTO dto);

    void delete();

    List<ShoppingCart> list();


    //删除一个购物车
    void sub(ShoppingCartDTO dto);
}
