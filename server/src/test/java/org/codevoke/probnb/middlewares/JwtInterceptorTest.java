package org.codevoke.probnb.middlewares;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.codevoke.probnb.annotations.JwtRequired;
import org.codevoke.probnb.utils.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtInterceptorTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    private JwtInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        jwtInterceptor = new JwtInterceptor();
        // Inject JwtService using reflection since it's autowired
        try {
            var field = JwtInterceptor.class.getDeclaredField("jwtService");
            field.setAccessible(true);
            field.set(jwtInterceptor, jwtService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void preHandle_WhenNoJwtRequiredAnnotation_ShouldReturnTrue() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(null);

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isTrue();
        verify(jwtService, never()).validateAccessToken(anyString());
    }

    @Test
    void preHandle_WhenJwtRequiredAndValidToken_ShouldReturnTrue() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.validateAccessToken("valid-token")).thenReturn(true);
        
        Claims claims = mock(Claims.class);
        when(claims.get("user_id")).thenReturn(1L);
        when(jwtService.getAccessClaims("valid-token")).thenReturn(claims);

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isTrue();
        verify(request).setAttribute("userId", 1L);
    }

    @Test
    void preHandle_WhenJwtRequiredAndNoAuthorizationHeader_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WhenJwtRequiredAndInvalidAuthorizationHeader_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WhenJwtRequiredAndInvalidToken_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtService.validateAccessToken("invalid-token")).thenReturn(false);

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WhenJwtRequiredAndTokenWithoutBearer_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("valid-token-without-bearer");

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WhenJwtRequiredAndEmptyToken_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WhenJwtRequiredAndExceptionInTokenValidation_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.validateAccessToken("valid-token")).thenThrow(new RuntimeException("Token validation error"));

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WhenJwtRequiredAndExceptionInClaimsExtraction_ShouldReturnFalse() throws Exception {
        // given
        when(handlerMethod.getMethodAnnotation(JwtRequired.class)).thenReturn(mock(JwtRequired.class));
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.validateAccessToken("valid-token")).thenReturn(true);
        when(jwtService.getAccessClaims("valid-token")).thenThrow(new RuntimeException("Claims extraction error"));

        // when
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
} 