package com.ug.project.infrastructure;

import com.ug.project.model.Post;

public class PostContext {

    private static PostContext instance;
    private Post currentPost;

    private PostContext() {
        // Constructor privado para singleton
    }

    public static PostContext getInstance() {
        if (instance == null) {
            instance = new PostContext();
        }
        return instance;
    }

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post post) {
        this.currentPost = post;
    }

    public void clearCurrentPost() {
        this.currentPost = null;
    }

}
