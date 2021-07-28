/**
 * FileName: UserController
 * Author:   嘉平十七
 * Date:     2021/1/25 18:07
 * Description: 处理所有的用户操作请求
 */
package com.chen.blog.controller;

import com.chen.blog.domain.User;
import com.chen.blog.service.UserService;
import com.chen.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/chenet")
    public String chenet(){
        return "chenet";
    }

    /**
     * 登录跳转按钮
     * @return
     */
    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 注册跳转按钮
     * @return
     */
    @GetMapping("/toRegistered")
    public String toRegistered(){
        return "registered";
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param session
     * @param model
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model){
        User user1 = userService.checkUserByUsername(username, MD5Utils.code(password));
        User user2 = userService.checkUserByEmail(username, MD5Utils.code(password));
        if (user1 != null){
            user1.setPassword(null);
            session.setAttribute("user",user1);
            return "redirect:/index";
        }else if (user2 != null){
            user2.setPassword(null);
            session.setAttribute("user",user2);
            return "redirect:/index";
        }else {
            //此处使用重定向，model拿不到
            model.addAttribute("message","用户名或密码错误");
            return "login";
        }
    }

    /**
     * 验证码
     * @param email 邮箱，用于发送验证码
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("/user/verCode")
    public String verCode(String email,Model model,HttpSession session){
        String ver = VerifyCodeUtils.getVerCode(6);
        //存入redis，并设置过期时间
        redisUtils.set("verCode",ver,120);
        try {
            MailUtils.sendMail(email,"竹风博客注册",ver);
        } catch (Exception e) {
            model.addAttribute("message","系统繁忙");
            e.printStackTrace();
        }
        return "registered";
    }

    /**
     * 用户注册
     * @param username
     * @param password
     * @param email
     * @param verCode
     * @param session
     * @param model
     * @return
     */
    @PostMapping("/registered")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam String verCode,
                          HttpSession session,Model model){
        if (!MailUtils.checkMail(email)){
            model.addAttribute("message","请输入正确的邮箱");
        }
        User user1 = userService.checkUserByUsername(username, MD5Utils.code(password));
        User user2 = userService.checkUserByUsername(email, MD5Utils.code(password));
        String ver = (String) redisUtils.get("verCode");
        if (user1 != null){
            model.addAttribute("message","用户名已注册");
        }if (user2 != null){
            model.addAttribute("message","邮箱已经注册");
        }if(!verCode.equals(ver)){
            model.addAttribute("message","验证码错误");
        }else {
            userService.addUser(new User(username, MD5Utils.code(password),0,0,email,new Date()));
            model.addAttribute("message","注册成功");
            return "login";
        }
        return "registered";
    }

    /**
     * 前台退出登录
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/index";
    }

    /**
     * 修改用户信息
     * @param username 用户名
     * @param email 邮箱
     * @param avatar 头像，使用可选模式，为每个头像赋值
     * @param description 个人简介
     * @param session
     * @param model
     * @return
     */
    @PostMapping("/user/update")
    public String update(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam Integer avatar,
                         @RequestParam String description,
                         HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        if (userService.findUserByUsername(username) == null){
            user.setUsername(username);
            model.addAttribute("message","更改成功");
        }else if (user.getUsername().equals(username)){ }else {
            model.addAttribute("message","用户名更改失败");
        }
        if (userService.findUserByEmail(email) == null){
            user.setEmail(email);
            model.addAttribute("message","更改成功");
        }else if (user.getEmail().equals(email)){}else {
            model.addAttribute("message","邮箱更改失败");
        }
        user.setAvatar(avatar);
        user.setDescription(description);
        userService.updateUser(user);
        model.addAttribute("user",user);    //session中存的是登录用户信息，model中存的和session中一样，用于前台页面展示
        session.setAttribute("user",user);
        return "userInfo";
    }

    /**
     * 用户个人中心
     * @param userId 用户id，通过前台传递参数，控制是当前登录账号还是他人账号
     * @param model
     * @return
     */
    @GetMapping("/user/userInfo")
    public String userInfo(Long userId, Model model, HttpServletRequest request,FilterConfig config){
        model.addAttribute("user",userService.findUserById(userId));
        System.err.println("IP地址："+IPUtils.getIpAddr(request));
        System.err.println("IPMap"+config.getServletContext().getAttribute("ipMap"));
        System.err.println("limitedMap"+config.getServletContext().getAttribute("limitedIpMap"));
        return "userInfo";
    }
}