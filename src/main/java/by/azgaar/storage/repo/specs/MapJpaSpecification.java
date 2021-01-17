package by.azgaar.storage.repo.specs;

import java.text.MessageFormat;

import org.springframework.data.jpa.domain.Specification;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

public final class MapJpaSpecification {

	public static Specification<Map> userIdEqualsTo(long ownerId) {
		return (root, query, builder) -> builder.equal(root.<Map, User>join("owner").get("id"), ownerId);
	}

	public static Specification<Map> filenameContains(String filename) {
		return (root, query, builder) -> builder.like(root.get("filename"), contains(filename));
	}

	private static String contains(String expression) {
		return MessageFormat.format("%{0}%", expression);
	}

}