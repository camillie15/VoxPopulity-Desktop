package com.ug.project.controller;

import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Post;
import com.ug.project.service.PostService;

import java.util.List;

public class PostController {

    private final PostService postService = new PostService();

    public List<Post> getAll () {
        var posts = postService.getAll();
        for (var post: posts){
            System.out.println(post.getUser().getName());
        }
        return postService.getAll();
    }

    public boolean savePost(String title, String content, Integer idCommunity){
        title = title.trim();
        content = content.trim();
        boolean response = postService.save(title,content,idCommunity);
        if (response) {
            System.out.println("Post registrado exitosamente");
        }
        return response;

    }

    public List<Post> getPostsCurrentUser() {
        int id = SessionManager.getCurrentUser().getId();
        if(id <= 0 ) {
            System.out.println(SessionManager.getCurrentUser().getId());
            System.out.println("Error PostController - getPostsCurrentUser: id del usuario invalido");
            return List.of();
        }
        try {
            return postService.getAllByUserId(id);
        } catch (Exception e) {
            System.out.println("Error PostController - getPostsCurrentUser: " + e);
            return List.of();
        }
    }

    public boolean delete(int idPost) {
        boolean response = postService.deletePost(idPost);
        if (response) {
            System.out.println("Post eliminado exitosamente");
        }
        return response;
    }

    public boolean editPost (Post post){
        return postService.editPost(post);
    }
}
