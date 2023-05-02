package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import sun.reflect.generics.visitor.Reifier;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的URI
        String uri = request.getRequestURI();
        //2.判断本次请求是否需要处理
        //本次不需要处理的地址
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        log.info("拦截到的请求："+ uri);
        boolean check = check(urls, uri);
        //3.如果不需要处理则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        //4-1.需要处理判断当前管理端用户是否登录
        Long empId = (Long) request.getSession().getAttribute("employee");
        BaseContext.setCurrentId(empId);
        if (empId != null) {
            filterChain.doFilter(request, response);
            return;
        }
        //4-2.需要处理判断当前移动端用户是否登录
        Long userId = (Long) request.getSession().getAttribute("user");
        BaseContext.setCurrentId(userId);
        if(userId != null){
            filterChain.doFilter(request,response);
            return;
        }
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 判断当前uri是否需要过滤
     *
     * @param urls
     * @param uri
     * @return
     */
    public boolean check(String[] urls, String uri) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, uri)) {
                return true;
            }
        }
        return false;
    }
}
