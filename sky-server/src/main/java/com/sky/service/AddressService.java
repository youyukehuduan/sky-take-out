package com.sky.service;


import com.sky.entity.AddressBook;
import java.util.List;

public interface AddressService {

    void add(AddressBook address);

    void setDefault(AddressBook address);

    List<AddressBook> getAddress();

    AddressBook findDefault();

    void update(AddressBook add);

    AddressBook getAddressById(Long id);

    void deleteAddressById(Long id);
}
