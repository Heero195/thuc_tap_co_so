package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Member;
import com.example.demo.exception.DuplicateRecordException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.mapper.UserMapper;
import com.example.demo.model.request.CreateUserReq;
import com.example.demo.model.request.UpdateUserReq;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public List<UserDto> getListUser() {
        List<User> users = userRepository.findAll();
        ArrayList<UserDto> result = new ArrayList<>();
        for (User user : users) {
             Member member = memberRepository.findByUserId(user.getId()).orElse(null);
             result.add(UserMapper.toUserDto(user, member));
        }
        return result;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No user found with email " + email));
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user found"));
        Member member = memberRepository.findByUserId(user.getId()).orElse(null);
        return UserMapper.toUserDto(user, member);
    }

    @Override
    public UserDto createUser(CreateUserReq req) {
        Optional<User> exist = userRepository.findByEmail(req.getEmail());
        if (exist.isPresent()) {
            throw new DuplicateRecordException("Email already exists in the system");
        }

        User user = UserMapper.toUser(req);
        user = userRepository.save(user);

        Member member = UserMapper.toMember(req, user.getId());
        memberRepository.save(member);

        return UserMapper.toUserDto(user, member);
    }

    @Override
    public UserDto updateUser(UpdateUserReq req, Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user found"));

        if (!user.getEmail().equals(req.getEmail())) {
            Optional<User> exist = userRepository.findByEmail(req.getEmail());
            if (exist.isPresent()) {
                throw new DuplicateRecordException("New email already exists in the system");
            }
            user.setEmail(req.getEmail());
        }
        user.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(req.getPassword(), org.mindrot.jbcrypt.BCrypt.gensalt(12)));
        userRepository.save(user);

        Member member = memberRepository.findByUserId(user.getId()).orElse(new Member());
        member.setUserId(user.getId());
        member.setFullName(req.getName());
        member.setPhone(req.getPhone());
        member.setAvatar(req.getAvatar());
        memberRepository.save(member);

        return UserMapper.toUserDto(user, member);
    }

    @Override
    public boolean deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No user found"));
        userRepository.delete(user);
        return true;
    }
}