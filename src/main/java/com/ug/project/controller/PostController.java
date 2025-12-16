package com.ug.project.controller;

import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Post;
import com.ug.project.service.PostService;
import com.ug.project.service.NotificationService;


import java.util.List;

/**
 * Controlador para gestionar las operaciones relacionadas con los posts.
 * Actúa como intermediario entre la capa de presentación y la capa de servicios.
 */
public class PostController {

    private final PostService postService = new PostService();

    /**
     * Obtiene todos los posts del sistema.
     * 
     * @return Lista de todos los posts disponibles
     */
    public List<Post> getAll () {
        var posts = postService.getAll();
        for (var post: posts){
            System.out.println(post.getUser().getName());
        }
        return postService.getAll();
    }

    /**
     * Guarda un nuevo post en el sistema.
     * 
     * @param title Título del post
     * @param content Contenido del post
     * @param idCommunity ID de la comunidad a la que pertenece el post
     * @return true si el post se guardó exitosamente, false en caso contrario
     */
    public boolean savePost(String title, String content, Integer idCommunity){
        title = title.trim();
        content = content.trim();
        boolean response = postService.save(title,content,idCommunity);
        if (response) {
            System.out.println("Post registrado exitosamente");
        }
        return response;

    }

    /**
     * Obtiene todos los posts del usuario actualmente autenticado.
     * 
     * @return Lista de posts del usuario actual, lista vacía si hay errores
     */
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

    /**
     * Elimina un post del sistema (borrado lógico).
     * 
     * @param idPost ID del post a eliminar
     * @return true si el post se eliminó exitosamente, false en caso contrario
     */
    public boolean delete(int idPost) {
        boolean response = postService.deletePost(idPost);
        if (response) {
            System.out.println("Post eliminado exitosamente");
        }
        return response;
    }

    /**
     * Edita un post existente en el sistema.
     * 
     * @param post Objeto Post con la información actualizada
     * @return true si el post se editó exitosamente, false en caso contrario
     */
    public boolean editPost (Post post){
        return postService.editPost(post);
    }
}
