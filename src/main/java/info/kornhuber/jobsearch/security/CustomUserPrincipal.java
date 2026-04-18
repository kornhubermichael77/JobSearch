package info.kornhuber.jobsearch.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// extends -> So bleibt der Rest von Spring Security unkompliziert: Username, Passwort, Authorities, Enabled-Status laufen weiter wie gewohnt.
public class CustomUserPrincipal extends User {

    @Getter
    private final Long userId;
    @Getter
    private final String email;
    @Getter
    private final String tenantDbName;
    private final boolean enabled;

    public CustomUserPrincipal(Long userId,
                               String username,
                               String password,
                               String email,
                               String tenantDbName,
                               boolean enabled,
                               Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.userId = userId;
        this.email = email;
        this.tenantDbName = tenantDbName;
        this.enabled = enabled;
    }

    public boolean isEnabledFlag() {
        return enabled;
    }
}