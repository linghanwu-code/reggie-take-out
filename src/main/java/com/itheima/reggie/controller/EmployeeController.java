package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录功能
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null) {
            return R.error("登陆失败");
        }
        if (!emp.getPassword().equals((password))) {
            return R.error("登陆失败");
        }
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工.员工信息{}" ,employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获得当前登录用户的id
        Long  empID= (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empID);
        employee.setUpdateUser(empID);

            employeeService.save(employee);

        return R.success("新增员工成功");

    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page ={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper= new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name );
        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
//自动给我们的page封装好了

        return R.success(pageInfo);

    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee ){
        Long employId=(Long) request.getSession().getAttribute("employee");
        log.info(employee.toString());
        employeeService.updateById(employee);
        return R.success("员工信息更新成功");


    }
    @GetMapping("/{id}")
    public R<Employee> getByid(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);

        }
        return R.error("没有查询到对应员工信息");
    }

}
