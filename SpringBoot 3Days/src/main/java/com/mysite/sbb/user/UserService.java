package com.mysite.sbb.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passEncoder;

    public  SiteUser create(String username, String email, String password){
        SiteUser siteuser = new SiteUser();

        siteuser.setUsername(username);
        siteuser.setEmail(email);
        //비밀번호 암호화 객체 (빈으로 등록하여 주입받자)
        siteuser.setPassword(passEncoder.encode(password));

        this.userRepo.save(siteuser);
        return siteuser;
    }

}
