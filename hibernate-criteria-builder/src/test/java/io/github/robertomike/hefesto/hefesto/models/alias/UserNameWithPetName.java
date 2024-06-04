package io.github.robertomike.hefesto.hefesto.models.alias;

import io.github.robertomike.hefesto.models.HibernateModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNameWithPetName implements HibernateModel {
    private String userName;
    private String petName;
}
