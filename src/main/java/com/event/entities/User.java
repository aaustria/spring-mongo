package com.event.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Represents User model
 */
public class User {
    @Id
    long id;

    @Indexed(unique = true)
    String username;

    String salt;

    String hash;

    String emailAddress;

    public User() {

    }

    public User(long id, String username, String salt, String hash, String emailAddress) {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.hash = hash;
        this.emailAddress = emailAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (!username.equals(user.username)) return false;
        if (salt != null ? !salt.equals(user.salt) : user.salt != null) return false;
        if (hash != null ? !hash.equals(user.hash) : user.hash != null) return false;
        return emailAddress != null ? emailAddress.equals(user.emailAddress) : user.emailAddress == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + username.hashCode();
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        return result;
    }
}
