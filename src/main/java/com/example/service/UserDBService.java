package com.example.service;

import java.util.List;

import com.example.model.User;

public interface UserDBService {


    List<User> getAll();

    User findById(int id);

    User findByName(String name);

    void create(User user);

    void update(User updateUser, User currentUser );

    void delete(int id);

    boolean exists(User user);
}
