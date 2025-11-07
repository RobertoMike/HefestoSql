package io.github.robertomike.hefesto.benchmarks;

import io.github.robertomike.hefesto.benchmarks.models.Comment;
import io.github.robertomike.hefesto.benchmarks.models.Post;
import io.github.robertomike.hefesto.benchmarks.models.User;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.Sort;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Benchmarks comparing Hefesto Criteria Builder vs raw Hibernate Criteria API.
 * 
 * Tests various query scenarios:
 * - Simple queries
 * - Queries with WHERE conditions
 * - Queries with JOINs
 * - Complex queries with multiple conditions
 * - Aggregate functions
 */
@State(Scope.Benchmark)
public class CriteriaBuilderBenchmark extends BaseBenchmark {
    
    @Override
    protected void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Post.class);
        configuration.addAnnotatedClass(Comment.class);
    }
    
    @Override
    protected void populateTestData(Session session) {
        // Create 1000 users
        for (int i = 0; i < 1000; i++) {
            User user = new User("User" + i, "user" + i + "@example.com", 20 + (i % 50));
            session.persist(user);
            
            // Each user has 5 posts
            for (int j = 0; j < 5; j++) {
                Post post = new Post("Post" + j + " by User" + i, "Content of post " + j, user);
                session.persist(post);
                user.getPosts().add(post);
                
                // Each post has 10 comments
                for (int k = 0; k < 10; k++) {
                    Comment comment = new Comment("Comment " + k + " on post " + j, user, post);
                    session.persist(comment);
                    post.getComments().add(comment);
                }
            }
        }
    }
    
    // ==================== SIMPLE QUERIES ====================
    
    @Benchmark
    public List<User> simpleQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class).get();
    }
    
    @Benchmark
    public List<User> simpleQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.select(root);
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== WHERE QUERIES ====================
    
    @Benchmark
    public List<User> whereQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .where("age", Operator.GREATER, 25)
                .where("name", Operator.LIKE, "User%")
                .get();
    }
    
    @Benchmark
    public List<User> whereQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.select(root);
        cr.where(
            cb.and(
                cb.gt(root.get("age"), 25),
                cb.like(root.get("name"), "User%")
            )
        );
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== JOIN QUERIES ====================
    
    @Benchmark
    public List<User> joinQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .join("posts")
                .where("posts.title", Operator.LIKE, "Post1%")
                .get();
    }
    
    @Benchmark
    public List<User> joinQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        var postsJoin = root.join("posts");
        cr.select(root);
        cr.where(cb.like(postsJoin.get("title"), "Post1%"));
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== COMPLEX QUERIES ====================
    
    @Benchmark
    public List<User> complexQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .join("posts")
                .join("comments")
                .where("age", Operator.LESS, 30)
                .where("posts.title", Operator.LIKE, "Post%")
                .where("comments.text", Operator.LIKE, "Comment%")
                .orderBy("name")
                .limit(100)
                .get();
    }
    
    @Benchmark
    public List<User> complexQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        var postsJoin = root.join("posts");
        var commentsJoin = root.join("comments");
        cr.select(root);
        cr.where(
            cb.and(
                cb.lt(root.get("age"), 30),
                cb.like(postsJoin.get("title"), "Post%"),
                cb.like(commentsJoin.get("text"), "Comment%")
            )
        );
        cr.orderBy(cb.asc(root.get("name")));
        return session.createQuery(cr).setMaxResults(100).getResultList();
    }
    
    // ==================== COUNT QUERIES ====================
    
    @Benchmark
    public Long countQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .where("age", Operator.GREATER, 40)
                .countResults();
    }
    
    @Benchmark
    public Long countQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<User> root = cr.from(User.class);
        cr.select(cb.count(root));
        cr.where(cb.gt(root.get("age"), 40));
        return session.createQuery(cr).getSingleResult();
    }
    
    // ==================== PAGINATION QUERIES ====================
    
    @Benchmark
    public List<User> paginationQuery_Hefesto() {
        Hefesto.setSession(session);
        // Using page() for pagination (page size 20, starting at page 3 = offset 50)
        return Hefesto.make(User.class)
                .where("age", Operator.GREATER, 20)
                .orderBy("name")
                .page(20, 3)
                .getData();
    }
    
    @Benchmark
    public List<User> paginationQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        
        // First, do a COUNT query (like Hefesto's page() method does internally)
        CriteriaQuery<Long> countCr = cb.createQuery(Long.class);
        Root<User> countRoot = countCr.from(User.class);
        countCr.select(cb.count(countRoot));
        countCr.where(cb.gt(countRoot.get("age"), 20));
        long total = session.createQuery(countCr).getSingleResult();
        
        // Then, do the actual data query
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.where(cb.gt(root.get("age"), 20));
        cr.orderBy(cb.asc(root.get("name")));
        return session.createQuery(cr)
                .setFirstResult(50)
                .setMaxResults(20)
                .getResultList();
    }
    
    // ==================== AGGREGATE QUERIES ====================
    
    @Benchmark
    public Double aggregateQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .where("age", Operator.GREATER, 18)
                .avg("age")
                .findFirstFor(Double.class);
    }
    
    @Benchmark
    public Double aggregateQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Double> cr = cb.createQuery(Double.class);
        Root<User> root = cr.from(User.class);
        cr.select(cb.avg(root.get("age")));
        cr.where(cb.gt(root.get("age"), 18));
        return session.createQuery(cr).getSingleResult();
    }
    
    // ==================== MULTIPLE JOINS ====================
    
    @Benchmark
    public List<User> multipleJoinsQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .join("posts", JoinOperator.LEFT, join -> {
                    join.join("comments", JoinOperator.LEFT);
                })
                .where("posts.title", Operator.LIKE, "Post%")
                .get();
    }
    
    @Benchmark
    public List<User> multipleJoinsQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        Join<Object, Object> posts = root.join("posts", JoinType.LEFT);
        posts.join("comments", JoinType.LEFT);
        cr.where(cb.like(posts.get("title"), "Post%"));
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== OR CONDITIONS ====================
    
    @Benchmark
    public List<User> orConditionsQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("age", Operator.LESS, 20);
                    group.where("age", Operator.GREATER, 60);
                })
                .get();
    }
    
    @Benchmark
    public List<User> orConditionsQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.where(cb.or(
                cb.lt(root.get("age"), 20),
                cb.gt(root.get("age"), 60)
        ));
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== IN CLAUSE ====================
    
    @Benchmark
    public List<User> inClauseQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .whereIn("age", 25, 30, 35, 40, 45, 50)
                .get();
    }
    
    @Benchmark
    public List<User> inClauseQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.where(root.get("age").in(25, 30, 35, 40, 45, 50));
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== ORDER BY MULTIPLE ====================
    
    @Benchmark
    public List<User> orderByMultipleQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .where("age", Operator.GREATER, 25)
                .orderBy("age", Sort.DESC)
                .orderBy("name", Sort.ASC)
                .limit(100)
                .get();
    }
    
    @Benchmark
    public List<User> orderByMultipleQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.where(cb.gt(root.get("age"), 25));
        cr.orderBy(
                cb.desc(root.get("age")),
                cb.asc(root.get("name"))
        );
        return session.createQuery(cr)
                .setMaxResults(100)
                .getResultList();
    }
    
    // ==================== GROUP BY ====================
    
    @Benchmark
    public List<Object[]> groupByQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .addSelect("age")
                .count("id", "total")
                .groupBy("age")
                .findFor(Object[].class);
    }
    
    @Benchmark
    public List<Object[]> groupByQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cr = cb.createQuery(Object[].class);
        Root<User> root = cr.from(User.class);
        cr.multiselect(
                root.get("age"),
                cb.count(root.get("id"))
        );
        cr.groupBy(root.get("age"));
        return session.createQuery(cr).getResultList();
    }
    
    // ==================== LIKE QUERIES ====================
    
    @Benchmark
    public List<User> likeQuery_Hefesto() {
        Hefesto.setSession(session);
        return Hefesto.make(User.class)
                .where("name", Operator.LIKE, "%User%")
                .where("email", Operator.LIKE, "%@example.com")
                .get();
    }
    
    @Benchmark
    public List<User> likeQuery_RawHibernate() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cr = cb.createQuery(User.class);
        Root<User> root = cr.from(User.class);
        cr.where(cb.and(
                cb.like(root.get("name"), "%User%"),
                cb.like(root.get("email"), "%@example.com")
        ));
        return session.createQuery(cr).getResultList();
    }
}

