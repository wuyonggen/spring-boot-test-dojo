package io.github.wuyonggen.springboottestdojo.repository;

import io.github.wuyonggen.springboottestdojo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // 只加载JPA相关配置
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User testUser = new User(null, "Alice", "alice@example.com");

    @BeforeEach
    void setUp() {
        // 测试前清空数据
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("保存用户-成功")
    void save_shouldPersistUser() {
        // Act
        var savedUser = userRepository.save(testUser);

        // Assert
        assertNotNull(savedUser.getId(), "ID应该被自动生成");
        assertEquals("Alice", savedUser.getName(), "用户名不匹配");

        // 验证数据库状态
        User dbUser = testEntityManager.find(User.class, savedUser.getId());
        assertEquals("alice@example.com", dbUser.getEmail(), "邮箱未正确保存");
    }

    @Test
    @DisplayName("查找用户-存在")
    void findById_shouldReturnUserWhenUserExists() {
        // Arrange
        User savedUser = testEntityManager.persistAndFlush(testUser);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertTrue(foundUser.isPresent(), "用户应该存在");
        assertEquals(savedUser.getId(), foundUser.get().getId(), "ID不匹配");

    }

    @Test
    @DisplayName("查找用户-不存在")
    void findById_shouldReturnEmptyWhenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findById(99L);

        // Assert
        assertTrue(foundUser.isEmpty(), "用户不应该存在");
    }

    @Test
    @DisplayName("更新用户-成功")
    void update_shouldUpdateUserWhenUserExists() {
        // Arrange
        User savedUser = testEntityManager.persistAndFlush(testUser);
        String newEmail = "alice11@example.com";
        savedUser.setEmail(newEmail);

        // Act
        userRepository.save(savedUser);

        // Assert
        assertEquals(testEntityManager.find(User.class, savedUser.getId()).getEmail(), newEmail);
    }

    @Test
    @DisplayName("删除用户-成功")
    void delete_shouldRemoveUser() {
        // Arrange
        User savedUser = testEntityManager.persistAndFlush(testUser);
        testEntityManager.persistAndFlush(testUser);

        // Act
        userRepository.delete(savedUser);

        // Assert
        assertNull(testEntityManager.find(User.class, savedUser.getId()), "用户应该被删除");
    }
}