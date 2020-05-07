package by.azgaar.storage.repo;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MapRepo extends JpaRepository<Map, Long> {

    Page<Map> findAllByOwner(User owner, Pageable pageable);

    Map findByOwnerAndFilename(User owner, String filename);

    @Query("SELECT COUNT (id) FROM Map WHERE owner = :owner AND fileId LIKE %:fileId%")
    int countByOwnerAndFileId(User owner, String fileId);

    @Query("SELECT COUNT (id) FROM Map WHERE owner = :owner AND filename LIKE %:filename%")
    int countByOwnerAndFilename(User owner, String filename);

}