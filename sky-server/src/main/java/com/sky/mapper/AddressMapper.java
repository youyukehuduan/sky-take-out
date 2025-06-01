package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.ss.formula.functions.Address;

import java.util.List;

@Mapper
public interface AddressMapper {

    //添加地址
    void add(AddressBook addressBook);

    //其他地址赋0
    void updateAnother(AddressBook address);

    void updateMine(AddressBook address);

    @Select("select * from address_book where user_id = #{id}")
    List<AddressBook> getAddress(Long id);

    @Select("select * from address_book where user_id = #{id} and is_default = 1")
    AddressBook findDefault(Long id);

    void update(AddressBook add);

    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);
}
