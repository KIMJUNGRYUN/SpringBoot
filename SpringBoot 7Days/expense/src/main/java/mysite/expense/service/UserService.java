package mysite.expense.service;

import lombok.RequiredArgsConstructor;
import mysite.expense.dto.UserDTO;
import mysite.expense.entity.User;
import mysite.expense.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository uRepo;
    private final ModelMapper modelMapper; // DTO <-> 엔티티(entity) 변환

    public void save(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        user.setUserId(UUID.randomUUID().toString()); // 랜덤 ID
        uRepo.save(user);
    }

    private User mapToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }


}
