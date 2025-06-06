package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateEditDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.dto.mapper.UserCreateEditMapper;
import com.example.bankcards.dto.mapper.UserReadMapper;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UserOperationException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;

    public Optional<UserReadDto> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        return userRepository.findById(id)
                .map(userReadMapper::map);
    }

    @Transactional
    public UserReadDto create(UserCreateEditDto user) {
        if (user == null) {
            throw new IllegalArgumentException("Данные пользователя не могут быть null");
        }
        return Optional.of(user)
                .map(userCreateEditMapper::map)
                .map(userRepository::save)
                .map(userReadMapper::map)
                .orElseThrow(() -> new UserOperationException("Ошибка при создании пользователя"));
    }

    @Transactional
    public Optional<UserReadDto> update(UUID id, UserCreateEditDto user) {
        if (id == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        if (user == null) {
            throw new IllegalArgumentException("Данные пользователя не могут быть null");
        }
        return userRepository.findById(id)
                .map(entity -> userCreateEditMapper.map(user, entity))
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map)
                .or(() -> {
                    throw new UserNotFoundException(id);
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.delete(entity);
                    userRepository.flush();
                    return true;
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
