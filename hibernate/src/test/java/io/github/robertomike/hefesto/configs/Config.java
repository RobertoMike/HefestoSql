package io.github.robertomike.hefesto.configs;

import io.github.robertomike.hefesto.hefesto.models.Address;
import io.github.robertomike.hefesto.hefesto.models.Pet;
import io.github.robertomike.hefesto.hefesto.models.User;
import io.github.robertomike.hefesto.hefesto.models.UserPet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

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
        var prop = new Properties();

        try {
            var properties = getClass().getResource("/database.properties");
            var directory = Objects.requireNonNull(properties).getPath();
            var fileReader = new FileReader(directory);
            prop.load(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var config = new Configuration().addProperties(prop);

        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Address.class);
        config.addAnnotatedClass(Pet.class);
        config.addAnnotatedClass(UserPet.class);

        return config.buildSessionFactory();
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
