package org.codevoke.probnb.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private TestService testService;

    private TestController testController;

    @BeforeEach
    void setUp() {
        testController = new TestController(testService);
        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class TestController {
        private final TestService testService;

        TestController(TestService testService) {
            this.testService = testService;
        }

        @PostMapping("/test")
        public String test(@Valid @RequestBody TestDTO testDTO) {
            return testService.process(testDTO);
        }
    }

    static class TestDTO {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        private String email;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    interface TestService {
        String process(TestDTO testDTO);
    }

    @Test
    void handleValidationExceptions_WhenFieldErrors_ShouldReturnBadRequest() throws Exception {
        // given
        TestDTO testDTO = new TestDTO();
        testDTO.setName(""); // invalid - empty name
        testDTO.setEmail("invalid-email"); // invalid email

        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.fields").exists());
    }

    @Test
    void handleValidationExceptions_WhenNameIsMissing_ShouldReturnBadRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.fields.name").exists());
    }

    @Test
    void handleValidationExceptions_WhenEmailIsMissing_ShouldReturnBadRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    void handleValidationExceptions_WhenBothFieldsInvalid_ShouldReturnBadRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    void handleHTTPException_WhenUserException_ShouldReturnCorrectStatus() throws Exception {
        // given
        when(testService.process(any())).thenThrow(UserException.UserNotFound(1L));

        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"test@test.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.error").value("User with id 1 not found"));
    }

    @Test
    void handleHTTPException_WhenServerException_ShouldReturnInternalServerError() throws Exception {
        // given
        when(testService.process(any())).thenThrow(ServerException.DatabaseError("Test error"));

        // when & then
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"test@test.com\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.error").value("Server error: database query failed. details: Test error"));
    }
} 