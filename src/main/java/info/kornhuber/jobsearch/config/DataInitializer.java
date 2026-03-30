package info.kornhuber.jobsearch.config;

import info.kornhuber.jobsearch.auth.entity.Role;
import info.kornhuber.jobsearch.auth.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {

            if (roleRepository.findByRoleName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setRoleName("ROLE_USER");
                roleRepository.save(userRole);
            }

            if (roleRepository.findByRoleName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setRoleName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }
        };
    }
}