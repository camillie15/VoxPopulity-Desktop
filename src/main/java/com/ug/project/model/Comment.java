package com.ug.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId")
    private Integer id;

    @Column(name = "content", length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comments_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postId", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comments_post"))
    private Post post;

    @Column(name = "createdDate")
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) createdDate = LocalDateTime.now();
    }

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
