package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {



    Page<DishVO> pageQuery(DishPageQueryDTO dto);

    @AutoFill(value= OperationType.INSERT)
    void insert(Dish dish);


    void deleteById(Long id);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    @AutoFill(value= OperationType.INSERT)
    void update(Dish dish);


    //根据分类Id查询菜品
    @Select("select * from dish where category_id = #{id}")
    List<Dish> getByCategoryId(Long id);


    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
//    @Select("select count(id) from dish where category_id = #{categoryId}")
//    Integer countByCategoryId(Long categoryId);

}
