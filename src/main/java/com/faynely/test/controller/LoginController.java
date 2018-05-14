package com.faynely.test.controller;

import com.faynely.annotation.Autowired;
import com.faynely.annotation.Controller;
import com.faynely.annotation.RequestMapping;
import com.faynely.test.service.LoginService;

/**
 * @author NickFayne 2018-05-14 23:41
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping("/login")
    public void login(){
        loginService.login();
    }
}
