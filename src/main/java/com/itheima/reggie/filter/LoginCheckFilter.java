package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
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
        String[] urls= new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "front/**"
        };
        if(check(urls,uri)){
            filterChain.doFilter(request,response);
            return;
        }
        if (request.getSession().getAttribute("employee")!=null){
            filterChain.doFilter(request,response);
            return;
        }
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
       log.info("拦截到请求",uri);

    }
    public boolean check(String[] urls,String requesturl){
        for (String url : urls) {
            boolean match = PATH_MATHER.match(url,requesturl);
            if (match)
                return true;
            
        }
        return false;

    }
}
