package se325.assignment01.concert.service.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private int version;
    private String token;

    public User() {
    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this(null, username, password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("User, id: ");
        buffer.append(id);
        buffer.append(", username: ");
        buffer.append(username);
        buffer.append(", password: ");
        buffer.append(password);
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;

        if (obj == this)
            return true;

        User user = (User) obj;
        return new EqualsBuilder().append(username, user.getUsername()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(username).hashCode();
    }

}