package com.mysite.sbb.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SiteUser {

    @Id //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id +1 증가
    private Long id;

    @Column(unique = true)  //중복안됨
    private String username;

    private String password;

    @Column(unique = true) //중복안됨
    private String email;
}
