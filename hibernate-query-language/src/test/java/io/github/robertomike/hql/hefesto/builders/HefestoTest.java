package io.github.robertomike.hql.hefesto.builders;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hql.hefesto.models.Pet;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(BaseTest.class)
class HefestoTest {
    @Test
    void loadUsers() {
        var users = Hefesto.make(User.class).get();

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
    @Test
    void loadUsersWithPets() {
        var user = Hefesto.make(User.class)
                .with("pets")
                .findFirstById(1L);

        assertTrue(user.isPresent());
        assertFalse(user.get().getPets().isEmpty());
    }
    @Test
    void loadUsersWithManyRelations() {
        var user = Hefesto.make(User.class)
                .with("pets", "addresses")
                .findFirstById(1L);

        assertTrue(user.isPresent());
        assertFalse(user.get().getPets().isEmpty());
        assertFalse(user.get().getAddresses().isEmpty());
    }

    @Test
    void loadPets() {
        var pets = Hefesto.make(Pet.class).get();

        assertNotNull(pets);
        assertFalse(pets.isEmpty());
    }

    @Test
    void loadUserPet() {
        var list = Hefesto.make(UserPet.class).get();

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void paginateUsers() {
        var list = Hefesto.make(User.class).page(10, 1);

        assertNotNull(list);
        assertFalse(list.getData().isEmpty());
        assertEquals(1, list.getPage());
        assertTrue(list.getTotal() > 1);
    }

    @Test
    void findFirstBy() {
        var list = Hefesto.make(User.class)
                .findFirstBy("email", "javi@mail.com");

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void unsupportedMethods() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> Hefesto.make(User.class).join("", "", "")
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> Hefesto.make(User.class).join("", "", JoinOperator.INNER)
        );
    }
}