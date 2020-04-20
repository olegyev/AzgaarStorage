package by.azgaar.storage.repo;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepo extends JpaRepository<Map, Long> {

    Page<Map> findAllByOwner(User owner, Pageable pageable);

    Map findByOwnerAndFilename(User owner, String filename);

}