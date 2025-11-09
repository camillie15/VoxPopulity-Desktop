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

    public Community getCommunity() { return community; }
    public void setCommunity(Community community) { this.community = community; }

    public void addComment(Comment c) { comments.add(c); c.setPost(this); }
    public void removeComment(Comment c) { comments.remove(c); c.setPost(null); }

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public List<Comment> getComments() { return comments; }

    @PrePersist
    public void prePersist() {
        if (createdDate == null) createdDate = LocalDateTime.now();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
