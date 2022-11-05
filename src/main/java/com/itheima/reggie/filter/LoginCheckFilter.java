package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author prynn
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    /**
     * 路径匹配器,支持通配符
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //1.获取请求的url
        String requestURI = request.getRequestURI();

        //要拦截的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
//                "/common/**",
                "/user/login",

        };
        //2.判断本次请求是否需要拦截
        boolean check = check(urls, requestURI);
        //3.如果不需要处理，直接放行
        if(check){
            log.info("拦截到请求:{},本次请求不需要处理", request.getRequestURI());
            filterChain.doFilter(request, servletResponse);
            return;
        }
        //4.1 判断后台是否登录，如果已登录，直接放行
        if(request.getSession().getAttribute("employee") != null){

            long id = Thread.currentThread().getId();
            log.info("当前线程id:{}", id);

            Long empId = (Long) request.getSession().getAttribute("employee");

            log.info("用户已登录,登录id:{}", empId);
            //设置当前线程用户id
            BaseContext.setUserId(empId);

            filterChain.doFilter(request, servletResponse);
            return;
        }

        //4.2 判断用户是否登录，如果已登录，直接放行
        if(request.getSession().getAttribute("user") != null){

            long id = Thread.currentThread().getId();
            log.info("当前线程id:{}", id);

            Long userId = (Long) request.getSession().getAttribute("user");

            log.info("用户已登录,登录id:{}", userId);
            //设置当前线程用户id
            BaseContext.setUserId(userId);

            filterChain.doFilter(request, servletResponse);
            return;
        }

        //5.如果未登录，返回登录结果
        log.info("用户未登录,拦截到请求:{}",request.getRequestURI());
        servletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配,检查本次请求是否需要拦截
     * @param urls 要拦截的url
     * @param requestURI 实际访问的url
     * @return true:不需要拦截，false:需要拦截
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }

}
