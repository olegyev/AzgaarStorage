package by.azgaar.storage.repo.specs;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;

public final class MapJpaSpecification {

    public static Specification<Map> userIdContains(String ownerId) {
        return (root, query, builder) -> builder.like(root.<Map, User>join("owner").get("id"), contains(ownerId));
    }

    public static Specification<Map> filenameContains(String filename) {
        return (root, query, builder) -> builder.like(root.get("filename"), contains(filename));
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }

}