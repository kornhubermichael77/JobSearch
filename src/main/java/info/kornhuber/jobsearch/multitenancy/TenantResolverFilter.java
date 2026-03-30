package info.kornhuber.jobsearch.multitenancy;

import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantResolverFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public TenantResolverFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                String username = authentication.getName();

                UserEntity user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found for tenant: " + username));

                if (user.getTenantDbName() == null) {
                    throw new RuntimeException("User has no tenant assigned: " + username);
                }

                TenantContext.setTenant(user.getTenantDbName());
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }
}