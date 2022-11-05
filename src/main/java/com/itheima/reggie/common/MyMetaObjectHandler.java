package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时自动填充
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert] ....");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //获取当前线程用户id
        Long userId = BaseContext.getUserId();
        log.info("获取用户的id为:{}", userId);
        metaObject.setValue("createUser", userId);
        metaObject.setValue("updateUser", userId);

    }

    /**
     * 更新时自动填充
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update] ....");


        long id = Thread.currentThread().getId();
        log.info("当前线程id:{}", id);

        //获取当前线程用户id
        Long userId = BaseContext.getUserId();

        log.info("获取用户的id为:{}", userId);

        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", userId);

    }

}
