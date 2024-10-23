package com.spkt.librasys.config;

import com.spkt.librasys.constant.PredefinedRole;
import com.spkt.librasys.entity.Role;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.repository.RoleRepository;
import com.spkt.librasys.repository.access.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
//    @ConditionalOnProperty(
//            prefix = "spring",
//            value = "datasource.driverClassName",
//            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {

            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                Role userRole =  roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());

                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                Role managerRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.MANAGER_ROLE)
                        .description("Manager role")
                        .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();
//                User user1 = User.builder()
//                        .username("quy@gmail.com")
//                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
//                        .roles(new HashSet<Role>(){{add(userRole);}})
//                        .build();
//                userRepository.save(user1);
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}

