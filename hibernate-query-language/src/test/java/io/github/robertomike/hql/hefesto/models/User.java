package io.github.robertomike.hql.hefesto.models;

import io.github.robertomike.hefesto.models.HibernateModel;
import io.github.robertomike.hql.hefesto.converters.StatusConverter;
import io.github.robertomike.hql.enums.Status;
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
    private Long id;
    private String name;
    private String email;
    private String photo;
    private Boolean active;
    private Boolean verified;
    private String role;
    private Integer level;
    private String phone;
    
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    private Set<Status> status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_pet",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "pet_id"))
    @ToString.Exclude
    private Set<Pet> pets;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Address> addresses;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
