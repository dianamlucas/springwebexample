package com.example.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * All wrapper classes in java.lang are immutable – String, Integer, Boolean, Character, Byte, Short, Long, Float, Double, BigDecimal, BigInteger 1. Create a
 * final class that is thread-safe 2. Set the values of properties using constructor only. 3. Make the properties of the class final and private 4. Do not
 * provide any setters for these properties.
 *
 * @author dlucas8
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String username;

    @JsonCreator
    public User( @JsonProperty( "id" ) final int id, @JsonProperty( "username" ) final String username ) {
        this.id = id;
        this.username = username;
    }

    public User( final User user ) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;

        User user = ( User ) o;

        if ( id != user.id )
            return false;
        if ( username != null ? !username.equals( user.username ) : user.username != null )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + ( username != null ? username.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + '}';
    }
}
