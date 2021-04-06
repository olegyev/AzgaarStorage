package by.azgaar.storage.repo;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MapRepo extends JpaRepository<Map, Long>, JpaSpecificationExecutor<Map> {

    Map findByOwnerAndFilename(User owner, String filename);

    Map findByOwnerAndFileId(User owner, String fileId);

    @Query("SELECT COUNT (id) FROM Map WHERE owner = :owner")
    int countByOwner(User owner);

    @Query("SELECT COUNT (id) FROM Map WHERE owner = :owner AND filename LIKE %:filename%")
    int countByOwnerAndFilename(User owner, String filename);

}