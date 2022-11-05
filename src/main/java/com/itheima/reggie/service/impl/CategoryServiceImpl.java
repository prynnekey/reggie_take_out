package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BusinessException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author prynn
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //如果Dish中有这个分类，就不能删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件 根据id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        long count1 = dishService.count(dishLambdaQueryWrapper);

        if(count1 > 0){
            //已经关联菜品，抛出异常
            throw new BusinessException("该分类下有菜品，不能删除");
        }

        //如果Setmeal中有这个分类，就不能删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件 根据id查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        long count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            //已经关联套餐，抛出异常
            throw new BusinessException("该分类下有套餐，不能删除");
        }

        //删除分类
        super.removeById(id);
    }
}


