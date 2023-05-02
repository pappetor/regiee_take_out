package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/addressBook")
@Slf4j
@RestController
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }
    @PutMapping
    public R<AddressBook> edit(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(AddressBook::getUserId,userId);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        Long id = addressBook.getId();
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook addresssBook = addressBookService.getById(id);
        return R.success(addresssBook);
    }
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ids != null,AddressBook::getId,ids);
        addressBookService.remove(queryWrapper);
        return R.success("删除成功");
    }
    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

}
