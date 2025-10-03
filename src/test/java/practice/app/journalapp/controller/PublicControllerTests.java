package practice.app.journalapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
;kj
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.UserService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PublicController.class)
class PublicControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // for JSON conversion

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UserService userService; // replaces the real UserService bean in Spring context

    @Test
    void testCreateUser() throws Exception {
        User user = User.builder()
                .id(new ObjectId())
                .username("john")
                .password("password123")
                .role(Arrays.asList("USER"))
                .build();

        // mock service call
        doNothing().when(userService).saveEntry(any(User.class));

        mockMvc.perform(post("/public/create-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/public/health-check"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
