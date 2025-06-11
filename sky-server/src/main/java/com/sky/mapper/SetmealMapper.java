package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(value= OperationType.INSERT)
    void add(Setmeal setmeal);


    Page<SetmealVO> page(SetmealPageQueryDTO dto);

    @AutoFill(value= OperationType.UPDATE)
    @Update("update setmeal set status = #{status} where id = #{id}")
    void setStatus(Integer status,Long id);


    @Select("select * from setmeal where id =#{id}")
    Setmeal getById(Long id);

    @AutoFill(value= OperationType.UPDATE)
    void update(Setmeal sto);


    void delete(List<Long> ids);

    List<Setmeal> getBymealId(Long categoryId);

    SetmealOverViewVO getTodaySetMeal();
}
