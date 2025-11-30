package com.ug.project.controller;

import com.ug.project.model.Post;
import com.ug.project.service.PostService;

import java.util.List;

public class PostController {

    private final PostService postService = new PostService();

    public List<Post> getAll () {
        return postService.getAll();
    }

    public void savePost(String title, String content, int idCommunity){

        postService.save(title,content,idCommunity);

    }

}
