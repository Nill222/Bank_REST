package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateEditDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.dto.mapper.UserCreateEditMapper;
import com.example.bankcards.dto.mapper.UserReadMapper;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCreateEditMapper userCreateEditMapper;

    @Mock
    private UserReadMapper userReadMapper;

    @InjectMocks
    private UserService userService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_returnsUserReadDto_whenUserExists() {
        User user = buildUser();
        UserReadDto expectedDto = buildUserReadDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userReadMapper.map(user)).thenReturn(expectedDto);

        Optional<UserReadDto> result = userService.findById(userId);

        assertThat(result).isPresent().contains(expectedDto);
        verify(userRepository).findById(userId);
        verify(userReadMapper).map(user);
    }

    @Test
    void create_returnsUserReadDto_whenValid() {
        UserCreateEditDto createDto = buildUserCreateDto();
        User user = buildUser();
        UserReadDto expectedDto = buildUserReadDto();

        when(userCreateEditMapper.map(createDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userReadMapper.map(user)).thenReturn(expectedDto);

        UserReadDto result = userService.create(createDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(userCreateEditMapper).map(createDto);
        verify(userRepository).save(user);
        verify(userReadMapper).map(user);
    }

    @Test
    void update_returnsUpdatedUserReadDto_whenUserExists() {
        UserCreateEditDto editDto = buildUserCreateDto();
        User user = buildUser();
        User updatedUser = buildUser();
        UserReadDto expectedDto = buildUserReadDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userCreateEditMapper.map(editDto, user)).thenReturn(updatedUser);
        when(userRepository.saveAndFlush(updatedUser)).thenReturn(updatedUser);
        when(userReadMapper.map(updatedUser)).thenReturn(expectedDto);

        Optional<UserReadDto> result = userService.update(userId, editDto);

        assertThat(result).isPresent().contains(expectedDto);
        verify(userRepository).findById(userId);
        verify(userCreateEditMapper).map(editDto, user);
        verify(userRepository).saveAndFlush(updatedUser);
        verify(userReadMapper).map(updatedUser);
    }

    @Test
    void update_returnsEmpty_whenUserNotFound() {
        UserCreateEditDto editDto = buildUserCreateDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<UserReadDto> result = userService.update(userId, editDto);

        assertThat(result).isEmpty();
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userCreateEditMapper, userRepository, userReadMapper);
    }

    @Test
    void delete_returnsTrue_whenUserExists() {
        User user = buildUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.delete(userId);

        assertThat(result).isTrue();
        verify(userRepository).delete(user);
        verify(userRepository).flush();
    }

    @Test
    void delete_returnsFalse_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.delete(userId);

        assertThat(result).isFalse();
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    // Вспомогательные методы
    private User buildUser() {
        return User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedpass")
                .role(Role.USER)
                .build();
    }

    private UserCreateEditDto buildUserCreateDto() {
        return new UserCreateEditDto(
                "testuser",
                "test@example.com",
                "rawpassword",
                Role.USER
        );
    }

    private UserReadDto buildUserReadDto() {
        return new UserReadDto(
                userId,
                "testuser",
                "test@example.com",
                Role.USER
        );
    }
}
