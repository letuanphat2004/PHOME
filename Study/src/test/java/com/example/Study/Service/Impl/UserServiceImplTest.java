package com.example.Study.Service.Impl;

import com.example.Study.Respository.UserRepository;
import com.example.Study.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private UserRepository users;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        service = new UserServiceImpl();
        ReflectionTestUtils.setField(service, "userRepository", users);
    }

    @Test
    void adminCanDisableAndEnableTenantAccount() {
        User tenant = user(7L, "tenant", 1L);
        when(users.findById(7L)).thenReturn(Optional.of(tenant));

        service.setAccountEnabled(7L, false, "admin");
        assertTrue(tenant.isDeleted());
        verify(users).save(tenant);

        service.setAccountEnabled(7L, true, "admin");
        assertFalse(tenant.isDeleted());
    }

    @Test
    void adminAccountCannotBeDisabled() {
        User admin = user(3L, "admin_two", 3L);
        when(users.findById(3L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class,
                () -> service.setAccountEnabled(3L, false, "admin"));

        verify(users, never()).save(admin);
    }

    private User user(long id, String username, long roleId) {
        User user = User.builder().username(username).password("encoded")
                .fullname(username).role_id(roleId).build();
        user.setId(id);
        return user;
    }
}
