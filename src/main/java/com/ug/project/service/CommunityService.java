package com.ug.project.service;

import com.ug.project.model.Community;
import com.ug.project.repository.CommunityRepository;

import java.util.List;

public class CommunityService {
    private static final CommunityRepository repo = new CommunityRepository();

    public List<Community> listAll() {
        return repo.findAll();
    }

    public Community create(String name, String description) {
        Community c = new Community(name, description);
        return repo.save(c);
    }

    public void delete(Community c) {
        repo.delete(c);
    }

    public void joinCommunity(Community c, com.ug.project.model.User u) {
        repo.join(c, u);
    }

    public void leaveCommunity(Community c, com.ug.project.model.User u) {
        repo.leave(c, u);
    }

    public Community findById(Integer id) {
        return repo.findAll().stream().filter(c -> c.getId() != null && c.getId().equals(id)).findFirst().orElse(null);
    }
}
