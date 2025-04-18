package com.example.assignment;

import com.example.assignment.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PasswordUtil to ensure:
 * - Password encoding works correctly
 * - Password matching works correctly
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class PasswordUtilTest {

    @InjectMocks
    private PasswordUtil passwordUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String RAW_PASSWORD = "testPassword123";
    private static final String ENCODED_PASSWORD = "encodedTestPassword123";

    @BeforeEach
    void setUp() {
        // Setup common mocks for all tests
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        // Setup for matches method - default to false
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        // Override for a specific case
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
    }

    @Test
    @DisplayName("Test password encoding")
    void testEncode() {
        // Call the method
        String result = passwordUtil.encode(RAW_PASSWORD);

        // Verify that encoding was called on the passwordEncoder
        verify(passwordEncoder).encode(RAW_PASSWORD);

        // Verify the result
        assertEquals(ENCODED_PASSWORD, result);
    }

    @Test
    @DisplayName("Test password matching with correct password")
    void testMatchesWithCorrectPassword() {
        // Call the method
        boolean result = passwordUtil.matches(RAW_PASSWORD, ENCODED_PASSWORD);

        // Verify that matches were called on the passwordEncoder
        verify(passwordEncoder).matches(RAW_PASSWORD, ENCODED_PASSWORD);

        // Verify the result
        assertTrue(result);
    }

    @Test
    @DisplayName("Test password matching with incorrect password")
    void testMatchesWithIncorrectPassword() {
        // Test data
        String incorrectPassword = "wrongPassword";

        // Call the method
        boolean result = passwordUtil.matches(incorrectPassword, ENCODED_PASSWORD);

        // Verify that matches were called on the passwordEncoder
        verify(passwordEncoder).matches(incorrectPassword, ENCODED_PASSWORD);

        // Verify the result
        assertFalse(result);
    }
}
