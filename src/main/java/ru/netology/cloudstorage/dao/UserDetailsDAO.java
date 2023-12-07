package ru.netology.cloudstorage.dao;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name="user_details")
@Getter
public class UserDetailsDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String authorities;
}
