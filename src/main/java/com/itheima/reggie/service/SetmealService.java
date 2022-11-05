package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author prynn
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐和套餐菜品
     * @param setmealDto 套餐数据+套餐菜品数据
     */
    void saveWithSetmealDish(SetmealDto setmealDto);

    /**
     * 删除套餐和套餐对应的菜品
     * @param ids 要删除套餐的id
     */
    void deleteWithSetmealDish(List<Long> ids);
}
