package com.ug.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Integer id;

    @Column(name = "tittle", length = 50)
    private String title;

    @Column(name = "content", length = 500)
    private String content;

    @Column(name = "status")
    private int status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false,
            foreignKey = @ForeignKey(name = "fk_posts_user"))
    private User user;

    @Column(name = "createdDate")
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communityId", foreignKey = @ForeignKey(name = "fk_posts_community"))
    private Community community;

    // --- Constructor vacío requerido por JPA ---
    public Post() {
    }

    // --- Constructor conveniente ---
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.status = 1; // Asumimos 1 como Activo por defecto
    }

    @PrePersist
    public void prePersist() {
        if (createdDate == null) createdDate = LocalDateTime.now();
        if (status == 0) status = 1; // Asegurar estado activo al crear
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    // Getter y Setter corregidos para status
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public Community getCommunity() { return community; }
    public void setCommunity(Community community) { this.community = community; }

    public List<Comment> getComments() { return comments; }

    // Métodos helper para relaciones
    public void addComment(Comment c) { comments.add(c); c.setPost(this); }
    public void removeComment(Comment c) { comments.remove(c); c.setPost(null); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
