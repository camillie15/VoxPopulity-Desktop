package com.ug.project.repository;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.model.Community;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio que gestiona el acceso a datos de los posts.
 * Implementa operaciones CRUD sobre la entidad Post utilizando JPA.
 */
public class PostRepository {

    private final JPAUtil jpaUtil = new JPAUtil();

    /**
     * Crea un nuevo post en la base de datos.
     * Adjunta las entidades relacionadas (User y Community) antes de persistir.
     * 
     * @param post Objeto Post a crear
     * @return true si el post se creó exitosamente
     * @throws RuntimeException si ocurre un error durante la creación
     */
    public boolean create (Post post){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                // Adjuntar (attach) user si existe id
                if (post.getUser() != null && post.getUser().getId() != null) {
                    User attachedUser = em.find(User.class, post.getUser().getId());
                    post.setUser(attachedUser);
                }

                // Adjuntar community si existe id
                if (post.getCommunity() != null && post.getCommunity().getId() != null) {
                    Community attachedCommunity = em.find(Community.class, post.getCommunity().getId());
                    post.setCommunity(attachedCommunity);
                }

                em.persist(post);
                tx.commit();
                return true;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al crear el Post", e);
            }
        }
    }

    /**
     * Obtiene todos los posts activos del sistema ordenados por ID descendente.
     * 
     * @return Lista de posts con status = 1
     * @throws RuntimeException si ocurre un error durante la consulta
     */
    public List<Post> findAll() {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                String jpql = "SELECT p FROM Post p WHERE p.status = 1 ORDER BY p.id DESC";
                TypedQuery<Post> query = em.createQuery(jpql, Post.class);
                return query.getResultList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Busca un post por su ID.
     * 
     * @param id ID del post a buscar
     * @return Optional conteniendo el post si existe, Optional vacío en caso contrario
     * @throws RuntimeException si ocurre un error durante la consulta
     */
    public Optional<Post> findById(Integer id) {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                Post post = em.find(Post.class, id);
                return Optional.ofNullable(post);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Obtiene todos los posts activos creados por un usuario específico.
     * 
     * @param userId ID del usuario
     * @return Lista de posts del usuario ordenados por ID descendente
     * @throws RuntimeException si ocurre un error durante la consulta
     */
    public List<Post> findByUserId(int userId){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                String jpql = "SELECT p FROM Post p WHERE p.status = 1 AND p.user.id = :userId ORDER BY p.id DESC";
                TypedQuery<Post> query = em.createQuery(jpql, Post.class);
                query.setParameter("userId", userId);
                return query.getResultList();
            } catch (Exception e) {
                System.out.println("Error PostRepository - findByUserId: " + e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Obtiene todos los posts activos de una comunidad específica.
     * 
     * @param communityId ID de la comunidad
     * @return Lista de posts de la comunidad ordenados por ID descendente
     * @throws RuntimeException si ocurre un error durante la consulta
     */
    public List<Post> findByCommunityId(int communityId) {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                String jpql = "SELECT p FROM Post p WHERE p.status = 1 AND p.community.id = :communityId ORDER BY p.id DESC";
                TypedQuery<Post> query = em.createQuery(jpql, Post.class);
                query.setParameter("communityId", communityId);
                return query.getResultList();
            } catch (Exception e) {
                System.out.println("Error PostRepository - findByCommunityId: " + e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Actualiza el título y contenido de un post existente.
     * 
     * @param post Objeto Post con la información actualizada
     * @return true si el post se actualizó exitosamente
     * @throws RuntimeException si ocurre un error durante la actualización
     */
    public boolean update(Post post) {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Post existing = em.find(Post.class, post.getId());
                existing.setTitle(post.getTitle());
                existing.setContent(post.getContent());
                em.merge(existing);
                tx.commit();
                return true;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al actualizar", e);
            }
        }
    }

    /**
     * Realiza un borrado lógico de un post cambiando su status a 0.
     * 
     * @param idPost ID del post a eliminar
     * @return true si el post se eliminó exitosamente
     * @throws RuntimeException si ocurre un error durante la eliminación
     */
    public boolean delete(int idPost){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Post existing = em.find(Post.class, idPost);
                existing.setStatus(0);
                tx.commit();
                return true;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al eliminar", e);
            }
        }
    }
}
