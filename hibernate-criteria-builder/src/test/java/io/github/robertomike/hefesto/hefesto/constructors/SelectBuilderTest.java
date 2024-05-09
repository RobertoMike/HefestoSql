package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.hefesto.models.Pet;
import io.github.robertomike.hefesto.hefesto.models.alias.UserNameWithPetName;
import io.github.robertomike.hefesto.hefesto.models.User;
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
                .findFirstById(1);

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
                .findFirstById(2);

        assertFalse(result.isEmpty());
        assertNotNull(result.get().getUserName());
        assertNotNull(result.get().getPetName());
        assertEquals("petto", result.get().getUserName());
        assertEquals("grillo", result.get().getPetName());
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
                .findFirstFor(Float.class);

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
                .where("id", 1)
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
                .where("id", 1)
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
                .where("id", 1)
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
                .where("id", 1)
                .findFor(String.class);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }
}
