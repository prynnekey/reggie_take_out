package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author prynn
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     *
     * @param category 分类对象
     * @return 新增结果
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页多少条数据
     * @return 成功or失败
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        log.info("page:{},pageSize:{}", page, pageSize);

        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件,根据sort进行排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(pageInfo, lambdaQueryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 根据id删除分类
     * @param ids 要删除的id
     * @return  删除是否成功
     */
    @DeleteMapping()
    public R<String> delete(Long ids) {
        log.info("ids:{}", ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category  查询条件
     * @return 查询结果
     */
    @GetMapping("/list")
    public R<List<Category>> getList(Category category){
        log.info("category:{}",category);

        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType,category.getType());
        //添加顺序条件:根据sort或者最后更新时间进行排序
        lambdaQueryWrapper.orderByAsc(Category::getSort).or().orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        return R.success(categoryList);
    }
}
