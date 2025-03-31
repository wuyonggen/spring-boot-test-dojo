package io.github.wuyonggen.springboottestdojo.repository;

import io.github.wuyonggen.springboottestdojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}