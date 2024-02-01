package io.github.robertomike.hibernate5.hefesto.models.alias;

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
