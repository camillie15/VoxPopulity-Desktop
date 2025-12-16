package com.ug.project.service;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.infrastructure.SessionManager;
import com.ug.project.model.Community;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.repository.PostRepository;
import com.ug.project.repository.CommunityRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los posts.
 * Coordina las operaciones entre el controlador y el repositorio.
 */
public class PostService {

    private final PostRepository postRepo = new PostRepository();
    private final CommunityRepository communityRepo = new CommunityRepository();

    /**
     * Obtiene todos los posts activos del sistema.
     * 
     * @return Lista de posts, lista vacía en caso de error
     */
    public List<Post> getAll() {
        try {
            return postRepo.findAll();
        } catch (Exception e) {
            System.out.println("Error en el PostService - getAll(): " + e);
        }
        return List.of();
    }

    /**
     * Obtiene todos los posts que pertenecen a una comunidad específica.
     * 
     * @param communityId ID de la comunidad
     * @return Lista de posts de la comunidad, lista vacía en caso de error
     */
    public List<Post> getAllByCommunityId(int communityId) {
        try {
            return postRepo.findByCommunityId(communityId);
        } catch (Exception e) {
            System.out.println("Error en PostService - getAllByCommunityId: " + e);
            return List.of();
        }
    }

    /**
     * Guarda un nuevo post en el sistema.
     * Crea el post asociado al usuario actual y a una comunidad específica.
     * También genera una notificación de nuevo post.
     * 
     * @param title Título del post
     * @param content Contenido del post
     * @param communityId ID de la comunidad (obligatorio)
     * @return true si el post se guardó exitosamente, false en caso contrario
     */
    public boolean save(String title, String content, Integer communityId) {

        // La comunidad es obligatoria
        if (communityId == null || communityId <= 0) {
            System.out.println("Error en el PostService - save(): CommunityId es obligatorio");
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Post post = new Post();

            // Usuario REAL administrado por JPA
            int idUser = SessionManager.getCurrentUser().getId();
            User user = em.find(User.class, idUser);
            post.setUser(user);
         //Asignar id del usuario logueado al post creado
         var idUserLogged = SessionManager.getCurrentUser().getId();
         user.setId(idUserLogged);
         post.setUser(user);

            // Comunidad REAL administrada por JPA
            Community community = em.find(Community.class, communityId);
            post.setCommunity(community);
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

    /**
     * Obtiene todos los posts creados por un usuario específico.
     * 
     * @param id ID del usuario
     * @return Lista de posts del usuario, lista vacía si no hay registros o en caso de error
     */
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

    /**
     * Elimina un post del sistema mediante borrado lógico.
     * 
     * @param idPost ID del post a eliminar
     * @return true si el post se eliminó exitosamente, false en caso contrario
     */
    public boolean deletePost(int idPost){
        try {
            return postRepo.delete(idPost);
        } catch (Exception e) {
            System.out.println("Error en el PostService - delete(): " + e);
            return false;
        }
    }

    /**
     * Actualiza la información de un post existente.
     * 
     * @param post Objeto Post con la información actualizada
     * @return true si el post se actualizó exitosamente, false en caso contrario
     */
    public boolean editPost(Post post){
        return postRepo.update(post);
    }
}
