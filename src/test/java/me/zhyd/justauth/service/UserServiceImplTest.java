package me.zhyd.justauth.service;

import me.zhyd.oauth.model.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Enables Mockito annotations for JUnit 5
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    // Mock RedisTemplate instance to simulate Redis operations
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    // Mock BoundHashOperations to simulate Redis hash operations
    @Mock
    private BoundHashOperations<String, String, AuthUser> mockOps;

    // Test data: Pre-configured AuthUser instance for test cases
    private final AuthUser testUser = AuthUser.builder().uuid("test-uuid").username("test-user").build();

    // Injects mocks into the service being tested (UserServiceImpl)
    @InjectMocks
    private UserServiceImpl userService;

    // Setup method executed before each test
    @BeforeEach
    void setUp() {
        // Initializes Mockito annotations
        MockitoAnnotations.openMocks(this);

        // NOTE: This commented configuration would link redisTemplate to mockOps
        // when(redisTemplate.boundHashOps(anyString())).thenReturn(mockOps);
    }

    // Tests successful user retrieval by UUID
    @Test
    void getByUuidShouldReturnUserWhenExists() {
        // Configure mock to return testUser when specific UUID is requested
        when(mockOps.get("test-uuid")).thenReturn(testUser);

        // Call service method
        AuthUser result = userService.getByUuid("test-uuid");

        // Verify returned user has expected username
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    // Tests user retrieval when UUID doesn't exist
    @Test
    void getByUuidShouldReturnNullWhenNotExists() {
        // Configure mock to return null for invalid UUID
        when(mockOps.get("invalid-uuid")).thenReturn(null);

        // Call service method
        AuthUser result = userService.getByUuid("invalid-uuid");

        // Verify result is null
        assertNull(result);
    }

    // Tests retrieval of all users
    @Test
    void listAllShouldReturnUsersList() {
        // Create expected result list
        List<AuthUser> expected = Collections.singletonList(testUser);
        // Configure mock to return test user list
        when(mockOps.values()).thenReturn(expected);

        // Call service method
        List<AuthUser> result = userService.listAll();

        // Verify returned list matches expected
        assertEquals(expected, result);
    }

    // Tests user removal functionality
    @Test
    void removeShouldDeleteUserFromRedis() {
        // Call service method to remove user
        userService.remove("test-uuid");

        // Verify delete operation was called with correct UUID
        verify(mockOps).delete("test-uuid");
    }
}
