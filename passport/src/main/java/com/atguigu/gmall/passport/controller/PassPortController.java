package com.atguigu.gmall.passport.controller;

import com.atguigu.gmall.bean.User;
import com.atguigu.gmall.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String currentIp){
        //判断传回来的token是否正确==》解码的操作
        String key = "com.atguigu.gmall";
        String salt = currentIp;
        Map<String, Object> map = JwtUtil.decode(token, key, salt);

        if (map == null){
            return "failed";
        }

        return "success";
    }

    @RequestMapping("index")
    public String index(String returnUrl, ModelMap modelMap){
        modelMap.addAttribute("returnUrl",returnUrl);
        return "index";
    }

    //判断用户登录信息，并且生成token，存放在redis，并且返回给请求页面
    @RequestMapping("login")
    @ResponseBody
    public String login(User user, HttpServletRequest request){
    //调用用户服务判断用户登录是否正确

        if (user == null){
            return null;
        }else {

            String key = "com.atguigu.gmall";
            String salt = request.getRemoteAddr();

            Map<String, Object> map = new HashMap<>();
            map.put("name", user.getUsername());
            map.put("id", "1");
            String token = JwtUtil.encode(key, map, salt);
            //存入redis缓存中

            return token;
        }
    }
}
