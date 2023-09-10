package ru.practicum.explorewithme.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;
    @Column(name = "name", length = 250, nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && email.equals(user.email) && name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }
}
