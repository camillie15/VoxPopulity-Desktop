package com.ug.project.service;

import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Community;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.repository.PostRepository;
import com.ug.project.repository.CommunityRepository;
import java.time.LocalDateTime;
import java.util.List;

public class PostService {

    private final PostRepository postRepo = new PostRepository();
    private final CommunityRepository communityRepo = new CommunityRepository();

    public List<Post> getAll() {
        try {
            return postRepo.findAll();
        } catch (Exception e) {
            System.out.println("Error en el PostService - getAll(): " + e);
        }
        return List.of();
    }

    public List<Post> getAllByCommunityId(int communityId) {
        try {
            return postRepo.findByCommunityId(communityId);
        } catch (Exception e) {
            System.out.println("Error en PostService - getAllByCommunityId: " + e);
            return List.of();
        }
    }

    // Cambiado para aceptar Integer y resolver la entidad Community correctamente
    public boolean save(String title, String content, Integer communityId) {

        // La comunidad es obligatoria
        if (communityId == null || communityId <= 0) {
            System.out.println("Error en el PostService - save(): CommunityId es obligatorio");
            return false;
        }

         //Creamos el objeto Post
         Post post = new Post();
         User user = new User();

         //Asignar id del usuario logueado al post creado
         var idUserLogged = SessionManager.getCurrentUser().getId();
         user.setId(idUserLogged);
         post.setUser(user);

         //Resolver la community si se proporcionó un id válido
         if (communityId != null && communityId > 0) {
             Community c = communityRepo.findById(communityId);
             if (c != null) {
                 post.setCommunity(c);
             } else {
                 // Si el id no existe, dejamos community en null y loggeamos
                 System.out.println("Advertencia: la comunidad con id " + communityId + " no existe. Se guardará el post sin comunidad.");
                 post.setCommunity(null);
             }
         } else {
             post.setCommunity(null);
         }

        //Agregar la fecha y hora actual
        post.setCreatedDate(LocalDateTime.now());

        //Actualizar el status a activo
        post.setStatus(1);

        post.setTitle(title);
        post.setContent(content);

        //Llamanos al repositorio para guardar el post en la DB
        try {
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
