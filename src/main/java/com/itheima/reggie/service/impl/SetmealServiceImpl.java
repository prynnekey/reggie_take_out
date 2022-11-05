package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BusinessException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author prynn
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithSetmealDish(SetmealDto setmealDto) {
        //保存套餐
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();

        //保存套餐菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes()
                .stream().peek(setmealDish -> setmealDish.setSetmealId(setmealId)).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithSetmealDish(List<Long> ids) {
        //select count(*) from setmeal where category_id in (1,2,3) and status = 1
        //根据状态判断是否可以删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        long count = this.count(setmealLambdaQueryWrapper);
        //如果不能删除抛出异常
        if(count > 0){
            throw new BusinessException("该套餐正在售卖，不能删除");
        }
        //如果可以删除，正常删除
        this.removeByIds(ids);
        //删除套餐对应的菜品
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
