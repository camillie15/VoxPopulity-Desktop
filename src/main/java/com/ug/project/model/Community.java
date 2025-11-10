package com.ug.project.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Communities")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "communityId")
    private Integer id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Community_Members",
            joinColumns = @JoinColumn(name = "communityId"),
            inverseJoinColumns = @JoinColumn(name = "userId"))
    private Set<User> members = new HashSet<>();

    // Constructor
    public Community() {}

    public Community(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Membership helpers
    public void addMember(User u) {
        members.add(u);
    }
    public void removeMember(User u) {
        members.remove(u);
    }

    public int getMembersCount() {
        return members == null ? 0 : members.size();
    }

    // Getters / setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<User> getMembers() { return members; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Community that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() {
        return name + (description != null && !description.isBlank() ? " - " + description : "");
    }
}
