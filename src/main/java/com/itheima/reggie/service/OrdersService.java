package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * @author prynn
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders 提交订单的数据
     */
    void submit(Orders orders);
}
