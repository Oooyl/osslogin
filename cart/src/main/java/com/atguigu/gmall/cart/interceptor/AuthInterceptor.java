package com.atguigu.gmall.cart.interceptor;

import com.atguigu.gmall.cart.annotations.LoginRequired;
import com.atguigu.gmall.cart.util.CookieUtil;
import com.atguigu.gmall.cart.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

       //拦截器方法
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

//        System.out.println(methodAnnotation);
            // 通过添加注解的方式区分需要和不需要登录的页面
        //如果没有注解，则表示不需要登录，用户可以直接访问请求地址
        if (methodAnnotation == null ){
            return true;
        }else{
            String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
            String newToken = request.getParameter("newToken");
            String token = "";
            if (StringUtils.isNotBlank(oldToken)){
                token=oldToken;
            }
            if (StringUtils.isNotBlank(newToken)){
                token=newToken;
            }
            if (StringUtils.isNotBlank(token)) {
                //调用认证中心，认证token是否正确
                String url = "http://passport.gmall.com:8085/verify?token="+token+"&currentIp="+request.getRemoteAddr();
                String verifyResult = HttpclientUtil.doGet(url);
                //验证成功
                if (StringUtils.isNotBlank(verifyResult)&&"success".equals(verifyResult)){
                    //如果验证成功需要生成一个新的cookie
                    CookieUtil.setCookie(request,response,"oldToken",token,60*60,true);
                    return true;
                }
            }

        }

//        System.out.println("调用拦截器方法");
        //验证失败或者未登录，跳转到登录页面
        String returnUrl = request.getRequestURL().toString();
        response.sendRedirect("http://passport.gmall.com:8085/index?returnUrl="+returnUrl);
        return false;

    }
}
