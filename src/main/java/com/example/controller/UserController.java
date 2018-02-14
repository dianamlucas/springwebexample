package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.model.User;
import com.example.service.UserDBService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * contains the RESTful end points for every CRUD operation matching the GET, PUT, POST and delete HTTP request methods.
 *
 * By using verbose control of the ResponseEntity directly in the code,
 * you can add headers and HTTP Status codes directly to the response.
 *
 * @RestController was introduced in Spring MVC 4 is a combination of @Controller and @ResponseBody annotation
 * @author dlucas8
 *
 */
@RestController
@RequestMapping( "/users" )
public class UserController {

    @Autowired
    UserDBService userService;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    // =========================================== Get All Users =================
    @GetMapping( produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<List<User>> getAll() {

        System.out.println( "getting all users" );
        final List<User> users = userService.getAll();

        if ( users == null || users.isEmpty() ) {
            System.out.println( "no users found" );
            return new ResponseEntity<List<User>>( HttpStatus.NO_CONTENT );
        }
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        return new ResponseEntity<List<User>>( users, httpHeaders, HttpStatus.OK );
    }

    // =========================================== Get User By ID =========================================

    @GetMapping( value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<User> get( @PathVariable( "id" ) int id ) {

        System.out.println( "getting user with id: " + id );
        final User user = userService.findById( id );

        if ( user == null ) {
            System.out.println( "user with id " + id + " not found" );
            return new ResponseEntity<User>( HttpStatus.NOT_FOUND );
        }

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        return new ResponseEntity<User>( user, httpHeaders, HttpStatus.OK );
    }

    // =========================================== Create New User ========================================

    @PostMapping( produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<Void> create( @RequestBody User user, UriComponentsBuilder ucBuilder ) throws JsonProcessingException {

        System.out.println( "creating new user: " + objectMapper.writeValueAsString( user )  );

        if ( userService.exists( user ) ) {
            System.out.println( "a user with name " + user.getUsername() + " already exists" );
            return new ResponseEntity<Void>( HttpStatus.CONFLICT );
        }

        userService.create( user );

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation( ucBuilder.path( "/users/{id}" ).buildAndExpand( user.getId() ).toUri() );
        headers.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        return new ResponseEntity<Void>( headers, HttpStatus.CREATED );
    }

    // =========================================== Update Existing User ===================================
    // Spring MVC is expecting to map the entire body of the RequestBody to a User object.
    @PutMapping( value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<User> update( @PathVariable int id, @RequestBody User user ) throws JsonProcessingException {

        System.out.println( "updating user: " + objectMapper.writeValueAsString( user ) );
        final User currentUser = userService.findById( id );

        if ( currentUser == null ) {
            System.out.println( "User with id " + id + " not found" );
            return new ResponseEntity<User>( HttpStatus.NOT_FOUND );
        }

        final User updatedUser = new User( id, user.getUsername() );

        userService.update( updatedUser, currentUser );// final User updateUser, final User currentUser

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        return new ResponseEntity<User>( updatedUser, httpHeaders, HttpStatus.OK );
    }

    // =========================================== Delete User ============================================

    @DeleteMapping( value = "{id}" )
    public ResponseEntity<Void> delete( @PathVariable( "id" ) int id ) {
        System.out.println( "deleting user with id: " + id );
        final User user = userService.findById( id );

        if ( user == null ) {
            System.out.println( "Unable to delete. User with id " + id + " not found" );
            return new ResponseEntity<Void>( HttpStatus.NOT_FOUND );
        }

        userService.delete( id );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }

}
