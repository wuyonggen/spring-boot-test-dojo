package io.github.wuyonggen.springboottestdojo.service;

import io.github.wuyonggen.springboottestdojo.entity.User;
import io.github.wuyonggen.springboottestdojo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 这个注解启用mockito
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * @Mock 创建一个模拟对象（如 userRepository），替代真实依赖。
     */
    @Mock
    private UserRepository userRepository;

    /**
     * @InjectMocks 创建待测对象（如 userService），并自动注入其依赖的 @Mock 对象。
     */
    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Arrange
        List<User> mockUsers = List.of(
                new User(1L, "Alice", "alice@example.com"),
                new User(2L, "Bob", "bob@example.com")
        );
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act 模拟 userRepository.findAll() 返回预定义的用户列表
        List<User> result = userService.getAllUsers();

        // Assert
        // Assert: 验证结果
        assertEquals(2, result.size(), "返回的用户列表长度不正确");
        assertEquals("Alice", result.get(0).getName(), "第一个用户名称不匹配");
        assertEquals("Bob", result.get(1).getName(), "第二个用户名称不匹配");

        // 验证交互：确保 findAll() 被调用且仅调用一次
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository); // 确保没有其他意外交互
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User(userId, "Alice", "alice@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        // Arrange
        User newUser = new User(null, "Charlie", "charlie@example.com");
        User savedUser = new User(1L, "Charlie", "charlie@example.com");
        when(userRepository.save(newUser)).thenReturn(savedUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertEquals("Charlie", result.getName());
        assertEquals("charlie@example.com", result.getEmail());
        assertEquals(1L, result.getId());
        verify(userRepository).save(newUser);
    }

    @Test
    void updateUser_shouldUpdateUser() {
        // 准备数据
        Long userId = 1L;
        User existingUser = new User(userId, "Alice", "alice@example.com");
        User updatedDetails = new User(null, "Alice Smith", "alice.smith@example.com");

        // 模拟行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser); // 关键点：返回已更新的对象

        // 执行测试
        User result = userService.updateUser(userId, updatedDetails);

        // 验证结果
        assertEquals("Alice Smith", result.getName());
        assertEquals("alice.smith@example.com", result.getEmail());
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser); // 验证保存的是原有对象（更新而非新建）

    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        // 准备数据
        Long nonExistentId = 99L;
        User updatedDetails = new User(null, "Alice Smith", "alice.smith@example.com");

        // 模拟行为
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 执行 & 验证异常
        assertThrows(RuntimeException.class, () -> userService.updateUser(nonExistentId, updatedDetails));

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any()); // 确保未调用 save

    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        // 准备数据
        Long userId = 1L;
        User existingUser = new User(userId, "Alice", "alice@example.com");

        // 模拟行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        // 执行测试（无返回值）
        userService.deleteUser(userId);

        // 验证交互
        verify(userRepository).findById(userId);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        // 准备数据
        Long nonExistentId = 99L;

        // 模拟行为
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 执行 & 验证异常
        assertThrows(RuntimeException.class, () -> userService.deleteUser(nonExistentId));

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).delete(any()); // 确保未调用 delete
    }
}