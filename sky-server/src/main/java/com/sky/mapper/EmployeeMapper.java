package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @AutoFill(value= OperationType.INSERT)
    @Insert("Insert INTO employee (name,username,password,phone,sex,id_number,status,create_time,update_time,create_user,update_user) " +
            "values(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser}) ")
    void add(Employee employee);



    Page<Employee> page(EmployeePageQueryDTO empDTO);

    //无法使用AOP，建议在service中直接设置
    //接口参数未包含实体类或未在 SQL 中更新时间字段，导致自动填充的updateTime和updateUser未生效，
    // 需在 Service 层手动设置时间字段或修改 SQL 直接更新。
    void startOrStop(Employee employee);


    Employee findById(Long id);


    @AutoFill(value= OperationType.UPDATE)
    void updateById(Employee employee);
}
