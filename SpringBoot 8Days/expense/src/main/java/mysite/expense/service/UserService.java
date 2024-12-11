package mysite.expense.service;

import lombok.RequiredArgsConstructor;
import mysite.expense.dto.UserDTO;
import mysite.expense.entity.User;
import mysite.expense.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository uRepo;
    private final ModelMapper modelMapper; //DTO <-> entity 변환
    private final PasswordEncoder passwordEncoder;

    public void save(UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = mapToEntity(userDTO);
        user.setUserId(UUID.randomUUID().toString()); //랜덤 ID
        uRepo.save(user);
    }

    private User mapToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    //인증된 유저객체 리턴
    public User getLoggedUser() {
        //시큐리티를 이용해 현재 인증된 유저의 이메일을 가져와 DB 찾아서 유저객체 리턴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginUserEmail = auth.getName();
        User user = uRepo.findByEmail(loginUserEmail).orElseThrow(()
                -> new RuntimeException("이메일로 유저를 찾을수 없음"));
        return user;
    }

}
