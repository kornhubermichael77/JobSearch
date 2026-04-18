package info.kornhuber.jobsearch.multitenancy;

import info.kornhuber.jobsearch.security.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantResolverFilter extends OncePerRequestFilter {

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
                    && !(authentication instanceof AnonymousAuthenticationToken)) {

                Object principal = authentication.getPrincipal();

                if (principal instanceof CustomUserPrincipal customUserPrincipal) {
                    String tenantDbName = customUserPrincipal.getTenantDbName();

                    if (tenantDbName == null || tenantDbName.isBlank()) {
                        throw new TenantResolutionException(
                                "Dem authentifizierten User ist kein Tenant zugewiesen: " + customUserPrincipal.getUsername()
                        );
                    }

                    TenantContext.setTenant(tenantDbName);
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }
}