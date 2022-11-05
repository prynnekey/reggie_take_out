package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author prynn
 */
public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除分类 删除前需要进行判断
     * @param id 要删除的id
     */
    void remove(Long id);
}
