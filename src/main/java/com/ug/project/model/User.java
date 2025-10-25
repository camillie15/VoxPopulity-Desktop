package com.ug.project.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Integer id;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "lastName", length = 60)
    private String lastName;

    @Column(name = "email", length = 60, unique = true)
    private String email;

    @Column(name = "username", length = 60, unique = true)
    private String username;

    @Column(name = "password", length = 60)
    private String password;

    @Column(name = "rol")
    private Integer rol;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public void addPost(Post p) { posts.add(p); p.setUser(this); }
    public void removePost(Post p) { posts.remove(p); p.setUser(null); }

    public void addComment(Comment c) { comments.add(c); c.setUser(this); }
    public void removeComment(Comment c) { comments.remove(c); c.setUser(null); }

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getRol() { return rol; }
    public void setRol(Integer rol) { this.rol = rol; }
    public List<Post> getPosts() { return posts; }
    public List<Comment> getComments() { return comments; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
