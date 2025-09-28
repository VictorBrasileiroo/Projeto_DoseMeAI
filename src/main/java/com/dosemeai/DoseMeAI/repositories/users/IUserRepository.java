package com.dosemeai.DoseMeAI.repositories.users;

import com.dosemeai.DoseMeAI.domain.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
