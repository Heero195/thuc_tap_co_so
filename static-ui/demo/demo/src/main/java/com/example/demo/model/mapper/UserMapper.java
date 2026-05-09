package com.example.demo.model.mapper;

import com.example.demo.entity.User;
import com.example.demo.entity.Member;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.request.CreateUserReq;
import org.mindrot.jbcrypt.BCrypt;

public class UserMapper {

    public static UserDto toUserDto(User user, Member member) {
        UserDto tmp = new UserDto();
        tmp.setId(user.getId());
        tmp.setEmail(user.getEmail());
        if (member != null) {
            tmp.setName(member.getFullName());
            tmp.setPhone(member.getPhone());
            tmp.setAvatar(member.getAvatar());
            tmp.setRole(member.getRole());
        }
        return tmp;
    }

    public static User toUser(CreateUserReq req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt(12)));
        return user;
    }

    public static Member toMember(CreateUserReq req, Integer userId) {
        Member member = new Member();
        member.setUserId(userId);
        member.setFullName(req.getName());
        member.setPhone(req.getPhone());
        member.setRole(req.getRole() != null ? req.getRole() : "Developer");
        return member;
    }
}