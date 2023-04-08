package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 检查用户是否完成登录
 */
@Slf4j
@WebFilter( filterName="LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher  PATH_MATHER  = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String  uri = request.getRequestURI();
        log.info("拦截到请求{}" ,uri);
        //定义不需要处理的请求路径
        String[] urls= new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        if(check(urls,uri)){
            log.info("本次的请求不需要处理{}",uri);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态,如果已经登录则直接放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("session正常");
            Long empId=(Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
//controller里面直接返回R对象是因为框架自动帮我们转换成Json了,这里是filter需要我们手动转成Json数据传回前端
        //

        //判断客户端登录状态,如果已经登录则直接放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("session正常");
            Long userId=(Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }
    public boolean check(String[] urls,String requesturl){
        for (String url : urls) {
            boolean match = PATH_MATHER.match(url,requesturl);
            if (match) {
                return true;
            }
        }
        return false;

    }
}
