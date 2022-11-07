package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author prynn
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 分页查询套餐
     *
     * @param page     页码
     * @param pageSize 每页显示的条数
     * @param name     套餐名称
     * @return 分页数据
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);

        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        setmealLambdaQueryWrapper.like(StringUtils.hasText(name), Setmeal::getName, name);
        //添加排序条件
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //分页查询
        setmealService.page(pageInfo, setmealLambdaQueryWrapper);

        //将查询到的数据封装到setmealDtoPage中,records不需要拷贝，因为泛型不同，需要手动拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtoList = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            //将item中的属性全部拷贝到SetmealDto中
            BeanUtils.copyProperties(item, setmealDto);

            //获取id
            Long categoryId = item.getCategoryId();
            //通过id获取category
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                //获取categoryName
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }


    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto:{}", setmealDto);
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("保存成功");
    }


    /**
     * 修改套餐状态
     *
     * @param ids    要修改的id数组
     * @param status 要修改的状态 0：下架 1：上架
     * @return 修改结果
     */
    @PostMapping("/status/{status}")
    public R<String> status(@RequestParam List<Long> ids, @PathVariable int status) {
        log.info("ids:{},status:{}", ids, status);

        //update setmeal set status = ? where id in (?,?,?)
        LambdaUpdateWrapper<Setmeal> setmealLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealLambdaUpdateWrapper.set(Setmeal::getStatus, status);
        setmealLambdaUpdateWrapper.in(Setmeal::getId, ids);
        setmealService.update(setmealLambdaUpdateWrapper);

        return R.success("修改成功");
    }


    /**
     * 删除套餐，同时删除套餐和菜品的关系，改套餐必须停售，然后才能删除
     *
     * @param ids 套餐id
     * @return 删除结果
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);

        setmealService.deleteWithSetmealDish(ids);
        return R.success("套餐数据删除成功");
    }


    @Cacheable(value = "setmealCache", key = "'setmeal' + '_' + 'list' + '_' + #setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<SetmealDto>> getList(Setmeal setmeal) {
        log.info("setmeal:{}", setmeal);

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);

        List<SetmealDto> setmealDtoList = setmealList.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if(category != null){
                //对每一个setmealDto设置categoryName
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            Long setmealId = item.getId();
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);

            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);
            return setmealDto;
        }).collect(Collectors.toList());

        return R.success(setmealDtoList);
    }

}
