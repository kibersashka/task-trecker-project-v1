package com.task.tracker.authimpl.jwt.filter;

import com.task.tracker.authapi.dto.AccountResponse;
import com.task.tracker.authimpl.exception.AuthenticationHeaderException;
import com.task.tracker.authimpl.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public static final String BEARER = "Bearer";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            if(request.getRequestURI().startsWith("/auth")) {
                filterChain.doFilter(request, response);

                return;

            }
            String header = request.getHeader(AUTHORIZATION);
            String token = getTokenFromValidatedAuthorizationHeader(header);

            if (token == null) {
                log.error("Authorization header not found");
                throw new AuthenticationHeaderException("Authorization header not found");
            }

            AccountResponse accountResponse = jwtService.validateToken(token);

            if (accountResponse == null) {
                log.error("Invalid token");
                throw new AuthenticationHeaderException("Invalid token");
            }

            List<GrantedAuthority> authorities = accountResponse.roles().stream()
                    .map(
                            role -> new SimpleGrantedAuthority(
                                    "ROLE_%s".formatted(role.name()))
                    )
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    accountResponse,
                    null,
                    authorities
            );


            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AuthenticationHeaderException e) {

            log.error("JWT authentication error: {}", e.getMessage());

            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""
                {
                  "error": "Unauthorized",
                  "message": "%s"
                }
                """.formatted(e.getMessage()));

        } catch (Exception e) {

            log.error("Unexpected authentication error", e);

            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""
                {
                  "error": "Unauthorized",
                  "message": "Authentication failed"
                }
                """);
        }
    }

    private String getTokenFromValidatedAuthorizationHeader(String authorizationHeader)
            throws AuthenticationHeaderException {

        if (authorizationHeader == null) {
            throw new AuthenticationHeaderException(
                    "Invalid authentication scheme found in Authorization header"
            );
        }

        if (!authorizationHeader.startsWith(BEARER)) {
            throw new AuthenticationHeaderException(
                    "Invalid authentication scheme found in Authorization header"
            );
        }

        String token = getTokenFromAuthorizationHeader(authorizationHeader);

        if (token == null) {
            throw new AuthenticationHeaderException(
                    "Authorization header token is empty"
            );
        }

        return token;
    }

    private String getTokenFromAuthorizationHeader(String authorizationHeader) {
        return Optional.ofNullable(authorizationHeader)
                .filter(StringUtils::isNotBlank)
                .map(bearer -> StringUtils.removeStart(bearer, BEARER).trim())
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }
}
