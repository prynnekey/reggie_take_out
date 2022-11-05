package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author prynn
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 提交订单并支付
     *
     * @param orders 提交的订单数据
     * @return 提交结果
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("order:{}", orders);

        ordersService.submit(orders);
        return R.success("提交成功");
    }


    /**
     * 用户界面获取最新订单数据
     *
     * @param page     页码
     * @param pageSize 每页多少条数据
     * @return 查询到的数据
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(Long page, Long pageSize) {
        log.info("page:{},pageSize:{}", page, pageSize);
        //分页构造器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据用户id查询订单
        ordersLambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getUserId());
        //根据下单事件排序
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(ordersPage, ordersLambdaQueryWrapper);
        return R.success(ordersPage);
    }


    /**
     * 后台管理中查看订单明细
     *
     * @param page     页码
     * @param pageSize 每页多少数据
     * @param number   查询的订单号
     * @return 查询到的数据
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> page(Long page, Long pageSize, String number, LocalDateTime beginTime, LocalDateTime endTime) {
        log.info("page:{}, pageSize:{}, number:{}, beginTime:{}, endTime:{}", page, pageSize, number, beginTime, endTime);

        //分页构造器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);

        //条件按构造器
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(StringUtils.hasText(number), Orders::getNumber, number);

        //查询
        ordersService.page(ordersPage, ordersLambdaQueryWrapper);

        //将查询到的数据拷贝到ordersDtoPage中
        BeanUtils.copyProperties(ordersPage, ordersDtoPage,"records");

        List<Orders> ordersList = ordersPage.getRecords();

        List<OrdersDto> ordersDtoList = ordersList.stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);

            ordersDto.setUserName(orders.getUserName());

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            ordersDto.setOrderDetails(orderDetailService.list(orderDetailLambdaQueryWrapper));

            return ordersDto;
        }).collect(Collectors.toList());


        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }
}
