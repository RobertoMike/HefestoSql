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
    public Long id;
    public String name;
    public String email;
    public String photo;
    public Boolean active;
    public Boolean verified;
    public String role;
    public Integer level;
    public String phone;
    
    @Column(name = "deleted_at")
    public java.time.LocalDateTime deletedAt;
    
    @Column(name = "created_at")
    public java.time.LocalDateTime createdAt;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    public Set<Status> status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_pet",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "pet_id"))
    @ToString.Exclude
    public Set<Pet> pets;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    public List<Address> addresses;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
