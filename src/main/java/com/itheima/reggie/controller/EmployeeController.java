package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author prynn
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工用户登录
     * @param request 从request中获取session 并将数据存放到session中
     * @param employee 将接收到的数据封装到employee中
     * @return 登录是否成功
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将前端传过来的密码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.查询数据库中的数据
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        //3.判断用户名是否存在
        if(emp == null){
            return R.error("用户不存在");
        }
        //4.判断密码是否正确
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5.判断账号是否被禁用
        if(emp.getStatus() != 1){
            return R.error("账号已禁用");
        }
        //6.将用户id存入session中 并返回用户对象
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 推出登录
     * @param request  从request中获取session 并将数据存放到session中
     * @return 退出是否成功
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清空session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request 获取员工id
     * @param employee 员工对象
     * @return 新增是否成功
     */
    @PostMapping
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息{}", employee);
        //设置初始密码123456，并用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置 创建时间 更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置 创建人 修改人 通过session获取
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);


        employeeService.save(employee);
        return R.success("新增成功");
    }


    /**
     * 分页查询
     * @param page 要查询的页数
     * @param pageSize 每页显示的条数
     * @param name 要查询的员工姓名
     * @return 分页查询的结果
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, @RequestParam(required = false) String name){
        log.info("分页查询员工，当前页：{}，每页显示条数：{}，员工姓名：{}", page, pageSize, name);

        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);
        log.info("分页查询员工，结果：{}", pageInfo.getRecords());
        log.info("一共多少页：{}", pageInfo.getPages());
        log.info("一页多少条数据：{}", pageInfo.getSize());
        log.info("一共多少条{}", pageInfo.getTotal());
        log.info("当前页数{}", pageInfo.getCurrent());
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        log.info(employee.toString());

        //设置最后更新时间
//        employee.setUpdateTime(LocalDateTime.now());

        //设置本次操作的修改人
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);


        long id = Thread.currentThread().getId();
        log.info("当前线程id:{}", id);


        //执行更新
        employeeService.updateById(employee);
        return R.success("修改成功");
    }


    /**
     * 根据id查询员工信息
     * @param id id
     * @return 查询到的Employee类型的对象
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id){
        log.info("根据id查询员工，员工id：{}", id);
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

}
