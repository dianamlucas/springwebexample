package com.example.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.service.UserDBService;

/**
 * handles all the CRUD operations managed by an in-memory list
 * @author dlucas8
 *
 */
@Service
public class UserDBServiceImpl implements UserDBService {


    private static final AtomicInteger counter = new AtomicInteger();
    //in memory DB of users
    static List<User> users = new ArrayList<User>(
            Arrays.asList(
                    new User(counter.incrementAndGet(), "Diana Lucas"),
                    new User(counter.incrementAndGet(), "Chris Febles"),
                    new User(counter.incrementAndGet(), "Jason Pecsek"),
                    new User(counter.incrementAndGet(), "Dilshika Nanayakkara")));



    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User findById( int id ) {
        for (final User user : users){
            if (user.getId() == id){
                return user;
            }
        }
        return null;
    }

    @Override
    public User findByName( String name ) {
        for (final User user : users){
            if (user.getUsername().equals(name)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void create( final User user ) {
        final User newUser = new User (counter.incrementAndGet(), user.getUsername());
        users.add(newUser);

    }

    @Override
    public void update( final User updatedUser, final User currentUser ) {

        int index = users.indexOf(currentUser);
        System.out.println( index );
        users.set(index, updatedUser);

    }

    @Override
    public void delete( int id ) {
        User user = findById(id);
        users.remove(user);
    }

    @Override
    public boolean exists( User user ) {
        return findByName(user.getUsername()) != null;
    }

}
