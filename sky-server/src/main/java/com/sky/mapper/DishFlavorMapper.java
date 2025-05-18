package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {

  //  @AutoFill(value= OperationType.INSERT)
    void insert(List<DishFlavor> flavors);
}