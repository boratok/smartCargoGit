package com.boratok.Security;

import com.boratok.entity.Users;
import com.boratok.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(10);

    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public String verify(Users users) {
        Authentication authentication=
                authManager.authenticate(new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword()));

        if(authentication.isAuthenticated()){
            return jwtService.generateToken(users.getUsername());
        }
        return "fail";
    }
}
