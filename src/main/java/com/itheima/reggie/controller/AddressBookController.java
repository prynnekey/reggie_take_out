package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author prynn
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook 地址的数据
     * @return 新增结果
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}", addressBook);
        //添加数据前先设置userId
        addressBook.setUserId(BaseContext.getUserId());
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }


    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        //update from address_book set is_default = 0 where user_id = ?
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault, 0)
                .eq(AddressBook::getUserId, BaseContext.getUserId());
        addressBookService.update(addressBookLambdaUpdateWrapper);

        //update from address_book set is_default = 1 where id = ?
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }


    /**
     * 获取当前用户所有的地址信息
     * @return 地址信息
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getList(){
        //条件构造器
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(BaseContext.getUserId() != null, AddressBook::getUserId, BaseContext.getUserId());
        //添加排序
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getCreateTime);

        List<AddressBook> addressBookList = addressBookService.list(addressBookLambdaQueryWrapper);
        return R.success(addressBookList);
    }

    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        //判断是否为空
        if(addressBook != null){
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }

    /**
     * 跟新收货地址
     * @param addressBook 地址信息
     * @return 更新结果
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}", addressBook);
        addressBookService.updateById(addressBook);
        return R.success("保存成功");
    }

    @DeleteMapping
    public R<String> delete(long ids){
        log.info("ids:{}",ids);
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }


    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.getUserId())
                .eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);

        if(null == addressBook){
            return R.error("没有默认地址");
        }
        return R.success(addressBook);
    }
}
