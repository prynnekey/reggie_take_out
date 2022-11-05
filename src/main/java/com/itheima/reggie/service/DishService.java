package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

/**
 * @author prynn
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品,同时插入菜品对应的口味数据,需要操作两张表,dish和dish_flavor
     * @param dishDto 菜品数据+口味数据
     */
    void saveWithFlavor(DishDto dishDto);


    /**
     * 获取菜品详情,同时获取菜品对应的口味数据
     * @param id 要获取的id
     * @return  菜品详情
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品,同时修改菜品对应的口味数据
     * @param dishDto 要修改的菜品数据+口味数据
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 删除菜品,同时删除菜品对应的口味数据
     * @param ids 要删除的菜品id
     */
    void deleteWithDishFlavor(List<Long> ids);
}
