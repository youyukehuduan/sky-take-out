package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealdishMapper {


    List<Long> getById(List<Long> ids);

    void insert(List<SetmealDish> setmealdishs);


    @Select("select * from setmeal_dish where setmeal_id = #{setmeal_id}")
    List<SetmealDish> getBymealId(Long setmeal_id);
}
