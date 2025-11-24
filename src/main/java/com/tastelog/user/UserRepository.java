package com.tastelog.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//User 엔티티를 DB에 저장/조회하는 DAO(Repository) 역할을 하는 인터페이스.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
