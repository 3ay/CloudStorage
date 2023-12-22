package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.dao.UserDetailsDAO;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserDetailsDAO, Long> {
    Optional<UserDetailsDAO> findByLogin(String login);
}
