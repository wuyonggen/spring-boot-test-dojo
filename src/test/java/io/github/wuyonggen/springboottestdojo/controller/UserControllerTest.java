package io.github.wuyonggen.springboottestdojo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wuyonggen.springboottestdojo.entity.User;
import io.github.wuyonggen.springboottestdojo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // 自动配置的JSON处理器

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("GET /api/users - 成功获取用户列表")
    void getAllUsers_shouldReturn200WithAllUsers() throws Exception {
        // Arrange: 准备模拟数据
        List<User> mockUsers = List.of(
                new User(1L, "Alice", "alice@example.com"),
                new User(2L, "Bob", "bob@example.com")
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Act & Assert: 模拟HTTP请求并验证错误
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        User mockUser = new User(userId, "Alice", "alice@example.com");

        // 模拟行为
        when(userService.getUserById(userId)).thenReturn(mockUser);

        // 执行和验证HTTP请求
        mockMvc.perform(get("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 验证状态码
                .andExpect(jsonPath("$.id").value(userId)) // 验证JSON字段
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("创建用户-成功场景")
    void createUser_shouldReturnCreated() throws Exception {
        // 准备测试数据
        User newUser = new User(null, "Bob", "bob@example.com");
        User savedUser = new User(1L, "Bob", "bob@example.com");

        // 模拟行为
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // 执行和验证HTTP请求
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    void updateUser() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        User updatedDetails = new User(null, "Alice Smith", "alice.smith@example.com");
        User updatedUser = new User(userId, "Alice Smith", "alice.smith@example.com");

        // 模拟行为
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // 执行和验证HTTP请求
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Smith"));
    }

    @Test
    void deleteUser() throws Exception {
        // 准备测试数据
        Long userId = 1L;

        // 模拟行为（void方法不需要thenReturn）
        doNothing().when(userService).deleteUser(userId);

        // 执行和验证HTTP请求
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent()); // 验证204状态码
    }
}