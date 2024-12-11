package mysite.expense.service;

import lombok.RequiredArgsConstructor;
import mysite.expense.entity.User;
import mysite.expense.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository uRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //유저를 찾고 + 권한 시큐리리티 유저 객체로 리턴한다.
        User user = uRepo.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException(email+" 해당 이메일 유저가 없습니다."));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()  //권한인데 없음
        );
    }
}
