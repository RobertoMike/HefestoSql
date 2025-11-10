package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hefesto.hql.builders.Hefesto;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hql.hefesto.models.Pet;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import io.github.robertomike.hql.hefesto.models.alias.UserNameWithPetName;
import io.github.robertomike.hql.hefesto.models.alias.UserWithAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
public class SelectBuilderTest {
    @Test
    void onlyIdAndName() {
        var result = Hefesto.make(User.class)
                .setSelects("id", "name")
                .findFirst();

        assertFalse(result.isEmpty());
        assertNotNull(result.get().getName());
        assertNull(result.get().getEmail());
    }

    @Test
    void onlyIdAndEmailAsName() {
        var result = Hefesto.make(User.class)
                .addSelect("id")
                .addSelect("email", "name")
                .findFirstById(1L);

        assertFalse(result.isEmpty());
        assertNotNull(result.get().getName());
        assertEquals("test@mail.com", result.get().getName());
        assertNull(result.get().getEmail());
    }

    @Test
    void selectAliasClass() {
        var result = new Hefesto<>(User.class, UserNameWithPetName.class)
                .addSelect("name", "userName")
                .join("pets")
                .addSelect("pets.name", "petName")
                .findFirstById(2L);

        assertFalse(result.isEmpty());
        assertNotNull(result.get().getUserName());
        assertNotNull(result.get().getPetName());
        assertEquals("petto", result.get().getUserName());
        assertEquals("grillo", result.get().getPetName());
    }

    @Test
    void selectAliasClassWithoutConstruct() {
        var result = new Hefesto<>(User.class, UserWithAddress.class)
                .addSelect("name", "userName")
                .join("addresses")
                .addSelect("addresses.address", "address")
                .findFirstById(1L);

        assertFalse(result.isEmpty());
        assertNotNull(result.get().getUserName());
        assertNotNull(result.get().getAddress());
        assertEquals("test", result.get().getUserName());
        assertEquals("calle del sol", result.get().getAddress());
    }

    @Test
    void selectAliasClassWithNestedSetter() {
        var result = new Hefesto<>(User.class, UserPet.class)
                .join("pets")
                .addSelect("user.name")
                .addSelect("pets.name", "pet.name")
                .findFirstById(2L);

        assertFalse(result.isEmpty());
        assertNotNull(result.get().getUser());
        assertNotNull(result.get().getPet());
        assertEquals("petto", result.get().getUser().getName());
        assertEquals("grillo", result.get().getPet().getName());
    }

    @Test
    void selectMaxIdOfUsers() {
        var result = new Hefesto<>(User.class)
                .addSelect("id", "maxId", SelectOperator.MAX)
                .findFirstFor(Long.class);

        assertNotNull(result);
        assertTrue(result > 1);
    }

    @Test
    void selectAvgIdOfUsers() {
        var result = new Hefesto<>(User.class)
                .addSelect("id", SelectOperator.AVG)
                .findFirstFor(Double.class);

        assertNotNull(result);
        assertTrue(result > 1);
    }

    @Test
    void selectCountIdOfUsers() {
        var result = new Hefesto<>(User.class)
                .addSelect("id", SelectOperator.COUNT)
                .findFirstFor(Long.class);

        assertNotNull(result);
        assertTrue(result > 1);
    }

    @Test
    void selectMinIdOfUsers() {
        var result = new Hefesto<>(User.class)
                .addSelect("id", SelectOperator.MIN)
                .findFirstFor(Long.class);

        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    void selectNameOfPetFromUser() {
        var result = new Hefesto<>(User.class)
                .join("pets")
                .addSelect("pets.name", "petName")
                .where("user.id", 1L)
                .orderBy("pets.id")
                .findFirstFor(String.class);

        assertNotNull(result);
        assertEquals("lola", result);
    }

    @Test
    void selectPetFromUser() {
        var result = new Hefesto<>(User.class)
                .join("pets")
                .addSelect("pets.id", "id")
                .addSelect("pets.name", "name")
                .where("user.id", 1L)
                .findFirstFor(Pet.class);

        assertNotNull(result);
        assertEquals("lola", result.getName());
    }

    @Test
    void selectPetsFromUser() {
        var result = new Hefesto<>(User.class)
                .join("pets")
                .addSelect("pets.id", "id")
                .addSelect("pets.name", "name")
                .where("user.id", 1L)
                .findFor(Pet.class);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void selectAllNameOfPetFromUser() {
        var result = new Hefesto<>(User.class)
                .join("pets")
                .addSelect("pets.name", "petName")
                .where("user.id", 1L)
                .findFor(String.class);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void getWithSelect() {
        var result = new Hefesto<>(User.class)
                .get("id", "name");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
