package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.config.SecurityConfig;
import cz.cvut.fel.ear.reservation_system.environment.Environment;
import cz.cvut.fel.ear.reservation_system.environment.Generator;
import cz.cvut.fel.ear.reservation_system.environment.TestConfiguration;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(
        classes = {UserControllerSecurityTest.TestConfig.class,
                SecurityConfig.class})
public class UserControllerSecurityTest extends BaseControllerTestRunner {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.user = Generator.generateUser();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(userService);
    }

    @WithAnonymousUser
    @Test
    public void registerSupportsAnonymousAccess() throws Exception {
        final User toRegister = Generator.generateUser();
        mockMvc.perform(
                        post("/rest/users").content(toJson(toRegister)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        verify(userService).create(any(User.class));
    }

    @WithAnonymousUser
    @Test
    public void registerAdminThrowsUnauthorizedForAnonymousUser() throws Exception {
        final User toRegister = Generator.generateUser();
        toRegister.setRole(Role.ADMIN);

        mockMvc.perform(
                        post("/rest/users").content(toJson(toRegister)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
        verify(userService, never()).create(any());
    }

    @WithMockUser
    @Test
    public void registerAdminThrowsForbiddenForNonAdminUser() throws Exception {
        user.setRole(Role.STANDARD_USER);
        Environment.setCurrentUser(user);
        final User toRegister = Generator.generateUser();
        toRegister.setRole(Role.ADMIN);

        mockMvc.perform(
                        post("/rest/users").content(toJson(toRegister)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
        verify(userService, never()).create(any());
    }

    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private UserService userService;

        @Bean
        public UserController userController() {
            return new UserController(userService);
        }
    }


}
