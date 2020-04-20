package by.azgaar.storage.repo;

import by.azgaar.storage.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {
}