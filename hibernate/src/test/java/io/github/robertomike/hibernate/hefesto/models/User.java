package io.github.robertomike.hibernate.hefesto.models;

import javax.persistence.*;

import io.github.robertomike.hefesto.models.HibernateModel;
import io.github.robertomike.hibernate.enums.Status;
import io.github.robertomike.hibernate.hefesto.converters.StatusConverter;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User implements HibernateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String photo;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    private Set<Status> status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_pet",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "pet_id"))
    @ToString.Exclude
    private Set<Pet> pets;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Address> addresses;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
