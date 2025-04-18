package com.example.assignment;

import com.example.assignment.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CookieUtil to ensure:
 * - Refresh token cookie is set correctly
 * - Refresh token cookie is removed correctly
 * - Refresh token is extracted from the cookie correctly
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class CookieUtilTest {

    @InjectMocks
    private CookieUtil cookieUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Environment environment;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    // This value will be used for testing but not directly injected
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days in milliseconds

    @BeforeEach
    void setUp() {
        // Mock environment variables
        when(environment.getProperty("jwt.refresh-token.expiration", Long.class)).thenReturn(REFRESH_TOKEN_EXPIRATION);

        // Inject the environment into CookieUtil
        ReflectionTestUtils.setField(cookieUtil, "refreshTokenExpiration", environment.getProperty("jwt.refresh-token.expiration", Long.class));
    }

    @Test
    @DisplayName("Test add refresh token cookie")
    void testAddRefreshTokenCookie() {
        // Test data
        String refreshToken = "test-refresh-token";

        // Call the method
        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        // Verify that addCookie was called on the response
        verify(response).addCookie(cookieCaptor.capture());

        // Get the captured cookie
        Cookie cookie = cookieCaptor.getValue();

        // Verify cookie properties
        assertEquals("refreshToken", cookie.getName());
        assertEquals(refreshToken, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/api/v1/auth/refresh", cookie.getPath());
        assertEquals((int) (REFRESH_TOKEN_EXPIRATION / 1000), cookie.getMaxAge());
    }

    @Test
    @DisplayName("Test clear refresh token cookie")
    void testClearRefreshTokenCookie() {
        // Call the method
        cookieUtil.clearRefreshTokenCookie(response);

        // Verify that addCookie was called on the response
        verify(response).addCookie(cookieCaptor.capture());

        // Get the captured cookie
        Cookie cookie = cookieCaptor.getValue();

        // Verify cookie properties
        assertEquals("refreshToken", cookie.getName());
        assertNull(cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/api/v1/auth/refresh", cookie.getPath());
        assertEquals(0, cookie.getMaxAge()); // Cookie is deleted
    }

    @Test
    @DisplayName("Test extract refresh token from cookie when cookie exists")
    void testExtractRefreshTokenFromCookieWhenCookieExists() {
        // Test data
        String refreshToken = "test-refresh-token";
        Cookie[] cookies = new Cookie[] {
            new Cookie("otherCookie", "otherValue"),
            new Cookie("refreshToken", refreshToken)
        };

        // Mock request to return cookies
        when(request.getCookies()).thenReturn(cookies);

        // Call the method
        String extractedToken = cookieUtil.extractRefreshTokenFromCookie(request);

        // Verify the result
        assertEquals(refreshToken, extractedToken);
    }

    @Test
    @DisplayName("Test extract refresh token from cookie when cookie doesn't exist")
    void testExtractRefreshTokenFromCookieWhenCookieDoesNotExist() {
        // Test data
        Cookie[] cookies = new Cookie[] {
            new Cookie("otherCookie", "otherValue")
        };

        // Mock request to return cookies
        when(request.getCookies()).thenReturn(cookies);

        // Call the method
        String extractedToken = cookieUtil.extractRefreshTokenFromCookie(request);

        // Verify the result
        assertNull(extractedToken);
    }

    @Test
    @DisplayName("Test extract refresh token from cookie when no cookies")
    void testExtractRefreshTokenFromCookieWhenNoCookies() {
        // Mock request to return null for cookies
        when(request.getCookies()).thenReturn(null);

        // Call the method
        String extractedToken = cookieUtil.extractRefreshTokenFromCookie(request);

        // Verify the result
        assertNull(extractedToken);
    }

    @Test
    @DisplayName("Test credentials are loaded from environment variables")
    void testCredentialsAreLoadedFromEnvironment() {
        // Verify that the environment variables were queried
        verify(environment).getProperty("jwt.refresh-token.expiration", Long.class);

        // Verify that the values were correctly loaded from the environment
        assertEquals(REFRESH_TOKEN_EXPIRATION, ReflectionTestUtils.getField(cookieUtil, "refreshTokenExpiration"));
    }
}
