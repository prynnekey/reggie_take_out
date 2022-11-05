package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 * @author prynn
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询菜品
     * @param page 当前页
     * @param pageSize 每页显示条数
     * @param name 搜索时菜品的名称
     * @return 分页数据
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name){
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoInfo = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加name条件
        dishLambdaQueryWrapper.like(StringUtils.hasText(name),Dish::getName, name);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishLambdaQueryWrapper.orderByDesc(Dish::getCreateTime);

        //执行sql语句
        dishService.page(pageInfo, dishLambdaQueryWrapper);

        //对象拷贝 将pageInfo中的数据拷贝到dishDtoInfo中,不需要拷贝records,因为records的泛型不一致,需要去处理数据
        BeanUtils.copyProperties(pageInfo, dishDtoInfo, "records");

        List<Dish> pageInfoRecords = pageInfo.getRecords();

        //处理数据,使得list中包含pageInfoRecords + categoryName
        List<DishDto> list = pageInfoRecords.stream().map((item) -> {
            //创建DishDto对象
            DishDto dishDto = new DishDto();

            //对象拷贝,使得dishDto中包含dish的所有属性
            BeanUtils.copyProperties(item, dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类
            Category category = categoryService.getById(categoryId);

            if(category != null){
                //获取分类名称
                String categoryName = category.getName();
                //将分类名称设置到dishDto中
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoInfo.setRecords(list);

        return R.success(dishDtoInfo);
    }

    /**
     * 添加菜品
     * @param dishDto 菜品数据
     * @return 添加结果
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }


    /**
     * 通过id获取菜品数据和对应菜品口味数据,并回显到用户界面
     * @param id 要查询的id
     * @return  菜品数据 + 菜品口味数据
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishDto(@PathVariable long id){
        //根据id查询菜品数据
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品数据
     * @param dishDto 要修改的菜品数据
     * @return 修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto);
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }


    /**
     * 修改菜品的状态：启售、停售
     * @param ids 要修改的菜品id数组
     * @param status 要修改的状态 0：停售 1：启售
     * @return 修改结果
     */
    @PostMapping("/status/{status}")
    public R<String> status(@RequestParam List<Long> ids,@PathVariable int status){
        log.info("ids:{},status:{}",ids,status);

        //update dish set status = ? where id in (?,?,?)
        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.set(Dish::getStatus, status).in(Dish::getId, ids);
        dishService.update(dishLambdaUpdateWrapper);

        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        dishService.deleteWithDishFlavor(ids);

        return R.success("菜品数据删除成功");
    }



    /**
     * 根据条件查询对应的数据
     * @param dish 查询条件
     * @return 查询结果
     */
    @GetMapping("/list")
    public R<List<DishDto>> getList(Dish dish){
        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件,查询状态为1（启售状态）的菜品
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime).orderByAsc(Dish::getSort);

        //查询数据
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);

        List<DishDto> dishDtoList = dishList.stream().map( item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Category category = categoryService.getById(item.getCategoryId());

            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }
}
