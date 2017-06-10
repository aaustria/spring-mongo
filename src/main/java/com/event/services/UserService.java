package com.event.services;

import com.event.entities.User;
import com.event.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SequenceGeneratorService seqGenerator;

    public void registerUser(User user) {
        if (user.getId() == 0) {
            user.setId(seqGenerator.getNextSequence("user"));
        }
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.delete(id);
    }

    public User getUserByUserName(String username) {
        return userRepository.findFirstByUsername(username);
    }
}
