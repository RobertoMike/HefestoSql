package io.github.robertomike.hql.hefesto.models.alias;

import io.github.robertomike.hefesto.models.HibernateModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserWithAddress implements HibernateModel {
    private String userName;
    private String address;
}
