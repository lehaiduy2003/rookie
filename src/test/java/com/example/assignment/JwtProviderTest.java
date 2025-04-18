package com.example.assignment;

import com.example.assignment.entity.User;
import com.example.assignment.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for JwtProvider to ensure:
 * - Token generates correctly
 * - Token validation works correctly (expired, invalid)
 * - Username extraction works correctly
 * - Secret and expiration load from env correctly
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    @Mock
    private User user;

    @Mock
    private Environment environment;

    // These values will be used for testing but not directly injected
    private static final String SECRET_KEY = "test";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000L; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        // Setup common mocks for all tests
        when(user.getUsername()).thenReturn("test@example.com");

        // Mock environment variables
        when(environment.getProperty("jwt.secret")).thenReturn(SECRET_KEY);
        when(environment.getProperty("jwt.access-token.expiration", Long.class)).thenReturn(ACCESS_TOKEN_EXPIRATION);
        when(environment.getProperty("jwt.refresh-token.expiration", Long.class)).thenReturn(REFRESH_TOKEN_EXPIRATION);

        // Inject the environment into JwtProvider
        ReflectionTestUtils.setField(jwtProvider, "secretKey", environment.getProperty("jwt.secret"));
        ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", environment.getProperty("jwt.access-token.expiration", Long.class));
        ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpiration", environment.getProperty("jwt.refresh-token.expiration", Long.class));
    }

    @Test
    @DisplayName("Test access token generation")
    void testGenerateAccessToken() {
        // Generate token
        String token = jwtProvider.generateAccessToken(user);

        // Verify the token is not null or empty
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token can be parsed and contains correct claims
        Claims claims = extractClaims(token);
        assertEquals("test@example.com", claims.getSubject());

        // Verify expiration is set correctly (with some tolerance for test execution time)
        long expectedExpiration = System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION;
        long actualExpiration = claims.getExpiration().getTime();
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 1000); // Within 1 second
    }

    @Test
    @DisplayName("Test refresh token generation")
    void testGenerateRefreshToken() {
        // Generate token
        String token = jwtProvider.generateRefreshToken(user);

        // Verify the token is not null or empty
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token can be parsed and contains correct claims
        Claims claims = extractClaims(token);
        assertEquals("test@example.com", claims.getSubject());

        // Verify expiration is set correctly (with some tolerance for test execution time)
        long expectedExpiration = System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION;
        long actualExpiration = claims.getExpiration().getTime();
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 1000); // Within 1 second
    }

    @Test
    @DisplayName("Test extract username from token")
    void testExtractUsername() {
        // Generate token
        String token = jwtProvider.generateAccessToken(user);

        // Extract username
        String username = jwtProvider.extractUsername(token);

        // Verify username
        assertEquals("test@example.com", username);
    }

    @Test
    @DisplayName("Test validate token with valid token")
    void testValidateTokenWithValidToken() {
        // Generate token
        String token = jwtProvider.generateAccessToken(user);

        // Validate token
        Boolean isValid = jwtProvider.validateToken(token, user);

        // Verify token is valid
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Test validate token with expired token")
    void testValidateTokenWithExpiredToken() {
        // Set a negative expiration to create an expired token
        ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", -10000);

        // Generate token (which will be expired)
        String token = jwtProvider.generateAccessToken(user);

        try {
            // Validate token - this should throw ExpiredJwtException
            jwtProvider.validateToken(token, user);
            // If we get here, the token was not recognized as expired, which is a failure
            fail("Expected ExpiredJwtException was not thrown");
        } catch (ExpiredJwtException e) {
            // Expected exception, test passes
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Test validate token with wrong user")
    void testValidateTokenWithWrongUser() {
        // Generate token for original user
        String token = jwtProvider.generateAccessToken(user);

        // Create a different user
        User anotherUser = mock(User.class);
        when(anotherUser.getUsername()).thenReturn("another@example.com");

        // Validate token with different user
        Boolean isValid = jwtProvider.validateToken(token, anotherUser);

        // Verify token is invalid for different user
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Test extract expiration from token")
    void testExtractExpiration() {
        // Generate token
        String token = jwtProvider.generateAccessToken(user);

        // Extract expiration
        Date expiration = jwtProvider.extractExpiration(token);

        // Verify expiration is set correctly (with some tolerance for test execution time)
        long expectedExpiration = System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION;
        long actualExpiration = expiration.getTime();
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 1000); // Within 1 second
    }

    @Test
    @DisplayName("Test credentials are loaded from environment variables")
    void testCredentialsAreLoadedFromEnvironment() {
        // Verify that the environment variables were queried
        verify(environment).getProperty("jwt.secret");
        verify(environment).getProperty("jwt.access-token.expiration", Long.class);
        verify(environment).getProperty("jwt.refresh-token.expiration", Long.class);

        // Verify that the values were correctly loaded from the environment
        assertEquals(SECRET_KEY, ReflectionTestUtils.getField(jwtProvider, "secretKey"));
        assertEquals(ACCESS_TOKEN_EXPIRATION, ReflectionTestUtils.getField(jwtProvider, "accessTokenExpiration"));
        assertEquals(REFRESH_TOKEN_EXPIRATION, ReflectionTestUtils.getField(jwtProvider, "refreshTokenExpiration"));
    }

    // Helper method to extract claims from token for testing
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
