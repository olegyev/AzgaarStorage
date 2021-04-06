package by.azgaar.storage.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.azgaar.storage.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	
	Optional<User> findByOAuth2Id(final String oAuth2Id);
	
	boolean existsByS3Key(final String s3Key);
}