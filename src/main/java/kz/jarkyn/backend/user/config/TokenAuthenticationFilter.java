package kz.jarkyn.backend.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.jarkyn.backend.user.model.PermissionEnum;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.service.RoleService;
import kz.jarkyn.backend.user.service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final RoleService roleService;

    public TokenAuthenticationFilter(
            UserService userService,
            RoleService roleService
    ) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authToken = authHeader.substring("Bearer ".length());
        UserEntity user = userService.findByAuthToken(authToken);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }
        List<SimpleGrantedAuthority> authorities = roleService.findPermission(user.getRole())
                .stream()
                .map(PermissionEnum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getId(), authToken, authorities));
        filterChain.doFilter(request, response);
    }
}