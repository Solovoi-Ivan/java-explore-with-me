package ru.practicum.ewm.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "USERS")
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @NonNull
    @Column(name = "user_name")
    private String name;
    @NonNull
    @Column(name = "email")
    private String email;
}