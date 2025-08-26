package com.boratok.Security;

import com.boratok.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegisterController {

    @Autowired
    private UserRegisterService userRegisterService;

    @PostMapping("/register")
    public Users register(@RequestBody Users user){
        return userRegisterService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users users){
        return userRegisterService.verify(users);
    }
}
