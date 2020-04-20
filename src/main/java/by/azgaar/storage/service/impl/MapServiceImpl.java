package by.azgaar.storage.service.impl;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.exception.AccessDeniedException;
import by.azgaar.storage.exception.BadRequestException;
import by.azgaar.storage.exception.NotFoundException;
import by.azgaar.storage.repo.MapRepo;
import by.azgaar.storage.service.MapServiceInterface;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class MapServiceImpl implements MapServiceInterface {

    private final MapRepo mapRepo;

    @Value("${file.upload-dir}")
    private String path;

    @Autowired
    public MapServiceImpl(final MapRepo mapRepo) {
        this.mapRepo = mapRepo;
    }

    @Override
    @Transactional
    public Page<Map> getAllByOwner(User owner, Pageable pageable) {
        return mapRepo.findAllByOwner(owner, pageable);
    }

    @Override
    @Transactional
    public Map getOneByOwner(User owner, long id) {
        Map map = mapRepo.findById(id).orElse(null);

        if (map == null) {
            throw new NotFoundException("Map is not found.");
        } else if (!map.getOwner().equals(owner)) {
            throw new AccessDeniedException("Access denied!");
        }

        return map;
    }

    @Override
    @Transactional
    public Map update(User owner, long id, Map newMap) {
        if (anotherMapIsInDb(owner, newMap.getFilename(), id)) {
            throw new BadRequestException("There is another map with the same filename in DB for logged user.");
        }

        Map mapFromDb = getOneByOwner(owner, id);

        File f1 = new File(path + "/" + mapFromDb.getFilename());
        File f2 = new File(path + "/" + newMap.getFilename());
        f1.renameTo(f2);

        // Only "filename".
        BeanUtils.copyProperties(newMap, mapFromDb, "id", "owner", "updated", "version", "picture");

        return mapFromDb;
    }

    @Override
    @Transactional
    public void delete(User owner, long id) {
        Map mapToDelete = getOneByOwner(owner, id);

        File fileToDelete = new File(path + "/" + mapToDelete.getFilename());
        fileToDelete.delete();

        mapRepo.delete(mapToDelete);
    }

    @Transactional
    public boolean anotherMapIsInDb(User owner, String newFilename, long id) {
        Map foundMap = mapRepo.findByOwnerAndFilename(owner, newFilename);
        return foundMap != null && foundMap.getId() != id;
    }

}