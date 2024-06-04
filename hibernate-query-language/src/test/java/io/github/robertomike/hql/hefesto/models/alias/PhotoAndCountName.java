package io.github.robertomike.hql.hefesto.models.alias;

import io.github.robertomike.hefesto.models.HibernateModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoAndCountName implements HibernateModel {
    private String photo;
    private Long nameCount;
}
