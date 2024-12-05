package com.mysite.sbb.user;

import com.mysite.sbb.question.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    //유저네임(id)으로 유저 객체를 조회
    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepo.findByUsername(username);
        if (siteUser.isPresent()){
            return siteUser.get();
        }else{
            throw new DataNotFoundException("SiteUser not found");
        }
    }

}
