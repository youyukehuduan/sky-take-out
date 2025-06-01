package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressMapper;
import com.sky.service.AddressService;
import org.apache.poi.ss.formula.functions.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;


    @Override
    public void add(AddressBook address) {

        //获取当前用户的用户ID
        address.setUserId(BaseContext.getCurrentId());
        address.setIsDefault(0);
        addressMapper.add(address);
    }

    @Override
    public void setDefault(AddressBook address) {
        //获取当前用户的用户ID
        address.setUserId(BaseContext.getCurrentId());
        address.setIsDefault(0);
        //1,把本用户其他地址设置为0；
        addressMapper.updateAnother(address);

        //2，本用户本地址设置为1；
        address.setIsDefault(1);
        addressMapper.updateMine(address);
    }

    @Override
    public List<AddressBook> getAddress() {
        Long id = BaseContext.getCurrentId();
        return addressMapper.getAddress(id);
    }

    @Override
    public AddressBook findDefault() {
        Long id = BaseContext.getCurrentId();
        AddressBook add =  addressMapper.findDefault(id);
        return add;
    }

    @Override
    public void update(AddressBook add) {
//        Long id = BaseContext.getCurrentId();

        addressMapper.update(add);
    }

    @Override
    public AddressBook getAddressById(Long id) {
        AddressBook add = addressMapper.getById(id);
        return add;
    }

    @Override
    public void deleteAddressById(Long id) {
        addressMapper.deleteById(id);
    }
}
