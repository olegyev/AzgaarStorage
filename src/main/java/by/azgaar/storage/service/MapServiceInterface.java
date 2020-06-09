package by.azgaar.storage.service;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MapServiceInterface {

    Map create(Map map);

    Page<Map> getAllByOwner(User owner, Pageable pageable);

    Map getOneById(long id);

    Map getOneByOwner(User owner, long id);

    Map getOneByOwnerAndFilename(User owner, String filename);

    Map getOneByOwnerAndFileIdAndFilename(User owner, String fileId, String filename);

    Map update(User owner, Map oldMap, Map newMap);

    String delete(User owner, long id);

    int saveMapData(User owner, Map map);

}