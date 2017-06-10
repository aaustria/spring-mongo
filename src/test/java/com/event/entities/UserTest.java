package com.event.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;


public class UserTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization_singleEvent() throws Exception {
        User user = new User(15000l, "useraccount", "salt","password", "email@addr.com");
        String json = mapper.writeValueAsString(user);
        assertEquals(user, mapper.readValue(json, User.class));
    }

}