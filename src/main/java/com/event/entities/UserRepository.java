package com.event.entities;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,Long> {

    User findFirstByUsername(String username);

    User findById(long id);
}
