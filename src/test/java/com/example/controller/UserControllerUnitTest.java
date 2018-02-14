package com.example.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.model.User;
import com.example.service.UserDBService;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umuc.rest.support.controller.advice.DefaultControllerAdvice;
import edu.umuc.rest.support.filters.CORSFilter;

public class UserControllerUnitTest {

    private MockMvc mockMvc;
    /**
     * annotating the UserService with the @Mock annotation, we can return mocked data when we call a method from this service
     */
    @Mock
    private UserDBService userService;
    /**
     * Using the @InjectMocks annotation, we can inject the mocked service inside our UserController
     * This injects mock or spy fields into tested objects automatically
     */
    @InjectMocks
    private UserController userController;

    @Before
    public void init(){

        //initializes fields annotated with Mockito annotations.
        MockitoAnnotations.initMocks(this);
        /**
         * MockMvc is the main entry point for server-side Spring MVC test support
         * This builds a MockMvc instance by registering one or more @Controller instances and
         * configuring Spring MVC infrastructure programmatically.
         * We can add filters, interceptors or etc. using the .addFilter() or .addInterceptor() methods
         */
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .addFilter(new CORSFilter())
                .setControllerAdvice( new DefaultControllerAdvice() ) //
                .build();
    }

 // =========================================== Get All Users =============

    @Test
    public void testGetAll_success() throws Exception{
        //Create test data which’ll be returned as a response in the rest service.
        List<User> users = Arrays.asList(
                new User(1, "Diana Lucas"),
                new User(2, "Chris Febles"),
                new User(3, "Jason Pecsek"),
                new User(4, "Dilshika Nanayakkara"));

        //Configure mock object to return the test data when the getAll() method of the UserService is invoked.
        when(userService.getAll()).thenReturn(users);
        //Invoke an HTTP GET request to the /users URI and Validate if the response is correct.
        mockMvc.perform(get("/users"))//
        .andExpect(status().isOk())//
        .andExpect(content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON_UTF8_VALUE ))//
        .andExpect(jsonPath("$", hasSize(4)))//
        .andExpect(jsonPath("$[0].id", is(1)))//
        .andExpect(jsonPath("$[0].username", is("Diana Lucas")))//
        .andExpect(jsonPath("$[3].id", is(4)))//
        .andExpect(jsonPath("$[3].username", is("Dilshika Nanayakkara")));
        //Verify that the getAll() method of the UserService is invoked exactly once.
        verify(userService, times(1)).getAll();
        //Verify that after the response, no more interactions are made to the UserService
        verifyNoMoreInteractions(userService);

    }
 // =========================================== Get User By ID ==================
    @Test
    public void testGetUserById_success() throws Exception {
        User user = new User(1, "Diana Lucas");

        when(userService.findById(1)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("Diana Lucas")));

        verify(userService, times(1)).findById(1);
        verifyNoMoreInteractions(userService);
    }
    @Test
    public void testGetUserById_fail_404_not_found() throws Exception {

        when(userService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(1);
        verifyNoMoreInteractions(userService);
    }
 // =========================================== Create New User ============
    @Test
    public void testCreateUser_success() throws Exception {
        User user = new User(-1,"Arya Stark");

        when(userService.exists(user)).thenReturn(false);
        doNothing().when(userService).create(user);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString("http://localhost/users/")));

        verify(userService, times(1)).exists(user);
        verify(userService, times(1)).create(user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testCreateUser_fail_404_not_found() throws Exception {
        User user = new User(-1,"username exists");
        when(userService.exists(user)).thenReturn(true);
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(asJsonString(user)))
                .andExpect(status().isConflict());
        verify(userService, times(1)).exists(user);
        verifyNoMoreInteractions(userService);
    }
 // =========================================== Update Existing User ===================================

    @Test
    public void testUpdateUser_success() throws Exception {
        User user = new User(-1, "Arya Stark");

        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).update(user,user);

        mockMvc.perform(
                put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(asJsonString(user)))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).update(user,user);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testUpdateUser_fail_404_not_found() throws Exception {
        User user = new User(-1, "user not found");

        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(
                put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(asJsonString(user)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }
 // =========================================== Delete User ============================================

    @Test
    public void testDeleteUser_success() throws Exception {
        User user = new User(-1, "Arya Stark");

        when(userService.findById(user.getId())).thenReturn(user);
        doNothing().when(userService).delete(user.getId());

        mockMvc.perform(
                delete("/users/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(user.getId());
        verify(userService, times(1)).delete(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testDeleteUser_fail_404_not_found() throws Exception {
        User user = new User(-1, "user not found");

        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(
                delete("/users/{id}", user.getId()))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userService);
    }
 // =========================================== CORS Headers ===========================================

    @Test
    public void testCorsHeaders() throws Exception {

        mockMvc.perform(get("/users") //
                .requestAttr( "ORIGINID", "id:dev" )) //

        .andExpect( header().string(  "Access-Control-Allow-Methods", is("POST, GET, PUT, OPTIONS, DELETE"))) //
        .andExpect( header().string( "Access-Control-Allow-Origin", is("https://dev.umuc.edu") ) );
    }
    /*
     * converts a Java object into JSON representation
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
