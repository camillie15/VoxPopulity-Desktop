
package com.ug.project.service;

import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Community;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;

public class PostService {

    private final PostRepository postRepo = new PostRepository();

    public List<Post> getAll() {
        try {
            return postRepo.findAll();
        } catch (Exception e) {
            System.out.println("Error en el PostService - getAll(): " + e);
        }
        return List.of();
    }

    public boolean save(String title, String content, int communityId) {

        try {
            //Creamos el objeto Post
            Post post = new Post();
            User user = new User();
            Community community = new Community();

            //Asignar id del usuario logueado al post creado
            var idUserLogged = SessionManager.getCurrentUser().getId();
            user.setId(idUserLogged);
            post.setUser(user);

            //Si no existe id de la comunidad actual entonces se lo cambiará a 0 automáticamente
            if(communityId >= 0){
                community.setId(communityId);
            }
            post.setCommunity(community);

            //Agregar la fecha y hora actual
            post.setCreatedDate(LocalDateTime.now());

            //Actualizar el status a activo
            post.setStatus(1);

            post.setTitle(title);
            post.setContent(content);

            //Llamanos al repositorio para guardar el post en la DB
            return postRepo.create(post);
        } catch (Exception e) {
            System.out.println("Error en el PostService - save(): " + e);
            return false;
        }
    }

    public List<Post> getAllByUserId(int id){
        try {
            var posts = postRepo.findByUserId(id);
            if(posts.isEmpty()) {
                System.out.println("Error Post Service - getAllByUserId: No se encontraron registros");
                return List.of();
            }
            return posts;
        } catch (Exception e) {
            System.out.println("Error Post Service - getAllByUserId: " + e);
            return List.of();
        }
    }

    public boolean deletePost(int idPost){
        try {
            return postRepo.delete(idPost);
        } catch (Exception e) {
            System.out.println("Error en el PostService - delete(): " + e);
            return false;
        }
    }

    public boolean editPost(Post post){
        return postRepo.update(post);
    }
}
