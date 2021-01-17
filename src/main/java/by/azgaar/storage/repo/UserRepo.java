package by.azgaar.storage.repo;

import by.azgaar.storage.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
	
	Optional<User> findByOAuth2Id(final String oAuth2Id);
	
	boolean existsByS3Key(final String s3Key);
}