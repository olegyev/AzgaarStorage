package by.azgaar.storage.service;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MapServiceInterface {

    Map create(Map map);

    Page<Map> getAllByOwner(User owner, String filename, Pageable pageable);

    Map getOneById(long id);

    Map getOneByOwner(final User owner, final long id);

    Map getOneByOwnerAndFilename(final User owner, final String filename);

    Map getOneByOwnerAndFileIdAndFilename(final User owner, final String fileId, final String filename);

    int countByOwner(final User owner);
    
    Map update(User owner, Map oldMap, Map newMap);

    String delete(User owner, long id);

    int saveMapData(User owner, Map map);

}