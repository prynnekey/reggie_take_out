package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author prynn
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 移动端用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody User user, HttpServletRequest request) {
        log.info("user:{}",user);

        //获取手机号
        String phone = user.getPhone();
        //获取验证码

        //判断验证码是否正确

        //如果正确，说明登录成功
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone, phone);
        user = userService.getOne(userLambdaQueryWrapper);

        //判断是否为新用户
        if(user != null){
            //登录成功后将id保存到session中
            request.getSession().setAttribute("user", user.getId());
            //老用户，直接登录
            return R.success(user);
        }

        //新用户，将数据保存到数据库后，再登录
        user = new User();
        user.setPhone(phone);
        userService.save(user);
        //登录成功后将id保存到session中
        request.getSession().setAttribute("user", user.getId());

        return R.success(user);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
