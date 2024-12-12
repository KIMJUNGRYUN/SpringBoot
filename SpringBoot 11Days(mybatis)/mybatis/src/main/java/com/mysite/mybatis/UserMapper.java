package com.mysite.mybatis;

import org.apache.ibatis.annotations.*;

import java.util.List;

//JPA 의 리파지토리와 같음
@Mapper
public interface UserMapper {
    //#{} 는 변수인데 메소드의 입력값을 넣음
    @Select("select id,name,phone,address from user where id=#{id}")
    public User getUserById(int id);

    @Select("select id,name,phone,address from user")
    public List<User> getUserList();

    @Insert("insert into user values (#{id},#{name},#{phone},#{address})")
    int insertUser(String id, String name, String phone, String address);

    @Update("UPDATE user SET name=#{name}, phone=#{phone}, address=#{address} WHERE id=#{id}")
    int updateUser(String id, String name, String phone, String address);

    @Delete("DELETE FROM user WHERE id=#{id}")
    int deleteUser(String id);


}
