package io.github.robertomike.hql.configs;

import io.github.robertomike.hql.hefesto.models.Address;
import io.github.robertomike.hql.hefesto.models.Pet;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Config {
    public SessionFactory sessionFactory() {
        var config = new Configuration();

        return config.addAnnotatedClass(User.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Pet.class)
                .addAnnotatedClass(UserPet.class)
                .buildSessionFactory();
    }

    public Session session(SessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }

    public void basicData(EntityManager entityManager) {
        entityManager.getTransaction().begin();
        try (var inputStream = getClass().getResourceAsStream("/data-base.sql")) {
            String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Arrays.asList(text.split(";")).forEach(s -> {
                Query q = entityManager.createNativeQuery(s + ";");
                q.executeUpdate();
            });

            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("error with the configuration" + e.getMessage());

            throw new RuntimeException(e);
        }
    }
}
