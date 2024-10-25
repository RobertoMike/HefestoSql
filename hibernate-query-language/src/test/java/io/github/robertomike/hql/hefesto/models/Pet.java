package io.github.robertomike.hql.hefesto.models;

import io.github.robertomike.hefesto.models.HibernateModel;
import javax.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pets")
public class Pet implements HibernateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_pet",
            joinColumns = {@JoinColumn(name = "pet_id")},
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ToString.Exclude
    private Set<User> users;

    public Pet(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
