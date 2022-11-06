package com.itheima.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最终会封装成此对象
 * @author prynn
 */
@Data
public class R<T> implements Serializable {

    /**
     * 编码：1成功 0和其他失败
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 动态数据
     */
    private Map<String, Object> map = new HashMap<>();


    public static <T> R<T> success(T object){
        R<T> r = new R<>();
        r.setCode(1);
        r.setData(object);
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }


    public R<T> add(String key, Object value){
        this.map.put(key, value);
        return this;
    }


}
