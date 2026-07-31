package com.example.recover.utils;

import com.example.recover.dto.UserDto;
import com.example.recover.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // 单个转换
    User toEntity(UserDto userDto);

    //List 转换 — 自动基于上面的单个方法生成
    List<User> toEntityList(List<UserDto> list);

    // 反向转换
    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> user);
}
