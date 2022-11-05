package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工信息
 * @author prynn
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    /**
     * 身份证号
     */
    private String idNumber;

    private Integer status;

    /**
     * 创建时间
     * insert时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * insert或update时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * insert时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * insert或update时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
