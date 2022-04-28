package com.example.controller;
import com.example.domain.aggregate.Task;
import com.example.domain.aggregate.User;
import com.example.repository.entity.TaskEntity;
import com.example.repository.entity.UserEntity;
import com.example.repository.repository.TaskRepository;
import com.example.security.JWTSecurity.JwtUtil;
import com.example.security.UserDetailsServiceImpl;
import com.example.service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskRestController.class)
public class TaskRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    TaskService taskServiceImplementation;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    JwtUtil jwtUtil;
    private UserEntity user = new UserEntity(1L,"Sarah","Sarahajam@gmail.com","sarahY","sarah123",21);
    @WithMockUser
    @Test
    void returnAllTasks() throws Exception{
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    void returnTask() throws Exception{
        mockMvc.perform( MockMvcRequestBuilders
                .get("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }


    @WithMockUser
    @Test
    void createTask() throws Exception{
        TaskEntity task =new TaskEntity();
        task.setCompleted(false);
        task.setDescription("test task");
        task.setUser(this.user);
        String taskJson = objectMapper.writeValueAsString(task);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/tasks")
                .accept(MediaType.APPLICATION_JSON).content(taskJson)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    void editTask() throws Exception{
        TaskEntity task =new TaskEntity();
        task.setCompleted(false);
        task.setDescription("test task");
        task.setUser(this.user);
        String taskJson = objectMapper.writeValueAsString(task);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/tasks/1")
                .accept(MediaType.APPLICATION_JSON).content(taskJson)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }


    @WithMockUser
    @Test
    void deleteTask() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/tasks/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }


}
