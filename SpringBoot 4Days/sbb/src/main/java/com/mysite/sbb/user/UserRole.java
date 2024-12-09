package com.mysite.sbb.user;

import lombok.Getter;

@Getter
public enum UserRole {
    //유저의 권한들을 적음
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private String role;

    UserRole(String role){
        this.role = role;
    }


}
