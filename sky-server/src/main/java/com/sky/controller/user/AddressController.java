package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "地址簿接口")
@Slf4j
public class AddressController {


    @Autowired
    private AddressService addressService;


    @PostMapping
    @ApiOperation("新增地址")
    public Result address(@RequestBody AddressBook add){
        log.info("新增地址");
        addressService.add(add);
        return Result.success();
    }

    @PutMapping("/default")
    @ApiOperation("设置为默认地址")
    public Result setDefault(@RequestBody AddressBook add){

        addressService.setDefault(add);
        return Result.success();
    }


    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result setDefault(){
        AddressBook add = addressService.findDefault();
        return Result.success(add);
    }

    @GetMapping("/list")
    @ApiOperation("地址列表")
    public Result getAddress(){
        log.info("地址列表查询");
        List<AddressBook> lists = addressService.getAddress();
        return Result.success(lists);
    }
    @PutMapping
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook add){
        log.info("修改地址");
        addressService.update(add);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result getAddressById(@PathVariable("id") Long id){
        log.info("根据id查地址");
        AddressBook add = addressService.getAddressById(id);
        return Result.success(add);
    }

    @DeleteMapping()
    @ApiOperation("根据id删除地址")
    public Result deleteAddressById(Long id){
        log.info("根据id删除地址");
        addressService.deleteAddressById(id);
        return Result.success();
    }

}
