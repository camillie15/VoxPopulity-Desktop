
package com.ug.project.service;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Community;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.repository.PostRepository;
import jakarta.persistence.EntityManager;

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

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Post post = new Post();

            // Usuario REAL administrado por JPA
            int idUser = SessionManager.getCurrentUser().getId();
            User user = em.find(User.class, idUser);
            post.setUser(user);

            // Comunidad REAL administrada por JPA
            Community community = em.find(Community.class, communityId);
            post.setCommunity(community);

            post.setCreatedDate(LocalDateTime.now());
            post.setStatus(1);
            post.setTitle(title);
            post.setContent(content);

            em.persist(post);
            em.getTransaction().commit();

            // Crear notificación
            NotificationService ns = new NotificationService();
            ns.notifyNewPost(user, post);

            return true;

        } catch (Exception e) {
            System.out.println("Error en PostService.save(): " + e);
            em.getTransaction().rollback();
            return false;

        } finally {
            em.close();
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
