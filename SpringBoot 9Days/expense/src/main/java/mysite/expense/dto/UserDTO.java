package mysite.expense.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "이름을 작성해주세요")
    private String name;
    @NotBlank(message = "이메일 작성해주세요")
    @Email(message = "이메일 형식이 아닙니다")
    private String email;
    @NotBlank(message = "패스워드를 입력해주세요")
    @Size(min = 4, message = "패스워드는 4자이상")
    private String password;
    private String confirmPassword; //패스워드 확인 DTO에만
}
