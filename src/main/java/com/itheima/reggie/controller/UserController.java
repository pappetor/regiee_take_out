package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.service.impl.UserServiceImpl;
import com.itheima.reggie.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        String code = MailUtils.achieveCode();
        session.setAttribute("code", code);
        try {
            MailUtils.sendTestMail(phone, code);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return R.success("验证码发送成功");
    }

    //用验证码登录的代码
//    @PostMapping("/login")
//    public R<User> login(@RequestBody Map map, HttpSession session) {
//        String phone = (String) map.get("phone");
//        String code = (String) map.get("code");
//        Object codeInfo = session.getAttribute("code");
//        if (code != null && code.equals(codeInfo)) {
//            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(phone != null, User::getPhone, phone);
//            User user = userService.getOne(queryWrapper);
//            if (user == null) {
//                user = new User();
//                user.setPhone(phone);
//                userService.save(user);
//            }
//            session.setAttribute("user", user.getId());
//            return R.success(user);
//        } else {
//            return R.error("验证码输入有误，请重新输入");
//        }
//
//    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        Object codeInfo = session.getAttribute("code");

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(phone != null, User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        session.setAttribute("user", user.getId());
        return R.success(user);
    }

}
