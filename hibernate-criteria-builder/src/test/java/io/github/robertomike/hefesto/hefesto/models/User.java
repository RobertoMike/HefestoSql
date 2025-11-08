package io.github.robertomike.hefesto.hefesto.models;

import io.github.robertomike.hefesto.models.HibernateModel;
import io.github.robertomike.hefesto.hefesto.converters.StatusConverter;
import io.github.robertomike.hefesto.enums.Status;
import jakarta.persistence.*;
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
    public Long id;
    public String name;
    public String email;
    public String photo;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    public Set<Status> status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_pet",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "pet_id"))
    @ToString.Exclude
    public Set<Pet> pets;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public List<Address> addresses;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
