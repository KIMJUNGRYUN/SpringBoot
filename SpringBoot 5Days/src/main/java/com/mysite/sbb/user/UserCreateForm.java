package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

//회원 가입시 폼에 매핑하는 객체
@Getter
@Setter
public class UserCreateForm {
    @Size(min=3, max=25 ,message = "유저네임은 3~25 가능합니다..")
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @Email //이메일 형식이 맞는지 검사
    private String email;
}
