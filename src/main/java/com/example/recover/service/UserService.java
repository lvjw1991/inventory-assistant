package com.example.recover.service;

import com.example.recover.dto.LoginRequest;
import com.example.recover.dto.RegisterRequest;
import com.example.recover.dto.UserDto;
import com.example.recover.entity.User;
import com.example.recover.repository.UserRepository;
import com.example.recover.exception.ResourceNotFoundException;
import com.example.recover.dto.Result;
import com.example.recover.utils.JwtUtil;
import com.example.recover.utils.UserMapper;
import com.example.recover.utils.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * 检查用户名
     * 是否重复
     * 保存用户
     * @param registerRequest
     * @return
     */
    @Transactional
    public Result<UserDto> register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return Result.fail(500,"用户名称和密码不能为空");
        }
        if(userRepository.existsByUsername(username)){
            return Result.fail(500,"用户名已存在");
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(registerRequest.getPhone());
        user.setRealName(registerRequest.getRealName());
        user.setRole(UserRole.CASHIER);
        user.setStatus(true);
        user.setUsername(username);
        user.setEnabled(true);
        return Result.success(userMapper.toDto(userRepository.save(user)));
    }

    /**
     * 查询用户
     * 判断密码
     * 返回用户
     *
     * @param loginRequest
     * @return
     */
    public Result<UserDto> login(LoginRequest loginRequest) {
        String password = loginRequest.getPassword();
        String username = loginRequest.getUsername();
        User user = userRepository.findByUsername(username);
        if(user == null){
            return Result.fail(500,"用户名不存在");
        }
        // Spring Security 验证用户名密码
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password));
        UserDto dto = userMapper.toDto(user);
        String token = jwtUtil.generateToken(user);
        dto.setToken(token);
        return Result.success(dto);
    }

    public List<UserDto> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(404, "ResourceNotFoundException"));
        return userMapper.toDto(user);
    }

    public Result<UserDto> getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            return Result.fail(500,"用户名不存在");
        }
        UserDto dto = userMapper.toDto(user);
        String token = jwtUtil.generateToken(user);
        dto.setToken(token);
        return Result.success(dto);
    }
}
