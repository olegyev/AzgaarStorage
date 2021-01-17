package by.azgaar.storage.service.impl;

import java.util.Calendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.azgaar.storage.entity.User;
import by.azgaar.storage.exception.RandomStringException;
import by.azgaar.storage.repo.UserRepo;
import by.azgaar.storage.service.UserServiceInterface;
import by.azgaar.storage.util.RandomAlphaNumeric;

@Service
public class UserServiceImpl implements UserServiceInterface {

	private final UserRepo userRepo;

	@Autowired
	public UserServiceImpl(final UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	@Transactional
	public User retrieveUser(final OAuth2User principal) {
		final String oAuth2Id = principal.getAttribute("sub") != null ? principal.getAttribute("sub")
				: principal.getAttribute("id").toString();

		User user = userRepo.findByOAuth2Id(oAuth2Id).orElseGet(() -> instantiateUser(oAuth2Id, principal));
		user.setLastVisit(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		return userRepo.save(user);
	}

	private User instantiateUser(final String oAuth2Id, final OAuth2User principal) {
		final User user = new User();
		user.setOAuth2Id(oAuth2Id);
		user.setName(principal.getAttribute("name"));
		user.setEmail(principal.getAttribute("email"));
		user.setS3Key(generateRandomS3Key());
		user.setFirstVisit(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		return user;
	}

	private String generateRandomS3Key() throws RandomStringException {
		boolean success = false;
		String generatedString = null;
		int maxNumberOfTries = 1_000_000;
		final RandomAlphaNumeric random = new RandomAlphaNumeric();

		while (!success) {
			generatedString = random.nextString();

			if (!userRepo.existsByS3Key(generatedString)) {
				success = true;
			}

			if (maxNumberOfTries == 0) {
				throw new RandomStringException("Could not generate random S3 key. Try again!");
			}

			maxNumberOfTries--;
		}

		return generatedString;
	}

}