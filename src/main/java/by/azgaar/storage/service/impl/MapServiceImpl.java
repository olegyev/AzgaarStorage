package by.azgaar.storage.service.impl;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.exception.AccessDeniedException;
import by.azgaar.storage.exception.BadRequestException;
import by.azgaar.storage.exception.NotFoundException;
import by.azgaar.storage.property.FileStorageProperties;
import by.azgaar.storage.repo.MapRepo;
import by.azgaar.storage.service.MapServiceInterface;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class MapServiceImpl implements MapServiceInterface {

    private final String fileStorageLocation;
    private final MapRepo mapRepo;

    @Autowired
    public MapServiceImpl(final FileStorageProperties fileStorageProperties,
                          final MapRepo mapRepo) {
        fileStorageLocation = fileStorageProperties.getUploadDir();
        this.mapRepo = mapRepo;
    }

    @Override
    @Transactional
    public Map create(Map map) {
        return mapRepo.save(map);
    }

    @Override
    @Transactional
    public Page<Map> getAllByOwner(User owner, Pageable pageable) {
        return mapRepo.findAllByOwner(owner, pageable);
    }

    @Override
    @Transactional
    public Map getOneById(String id) {
        return mapRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Map getOneByOwner(User owner, String id) {
        Map map = getOneById(id);

        if (map == null) {
            throw new NotFoundException("Map is not found.");
        } else if (!map.getOwner().equals(owner)) {
            throw new AccessDeniedException("Access denied!");
        }

        return map;
    }

    @Override
    @Transactional
    public Map getOneByOwnerAndFilename(User owner, String filename) {
        return mapRepo.findByOwnerAndFilename(owner, filename);
    }

    @Override
    @Transactional
    public Map update(User owner, String id, Map newMap) {
        if (sameFilenameIsInDb(owner, newMap.getFilename(), id)) {
            throw new BadRequestException("There is another map with the same filename in DB for logged user.");
        }

        Map mapFromDb = getOneByOwner(owner, id);

        String pathToMap = fileStorageLocation + "/" + owner.getId();
        File f1 = new File(pathToMap + "/" + mapFromDb.getFilename());
        File f2 = new File(pathToMap + "/" + newMap.getFilename());
        f1.renameTo(f2);

        BeanUtils.copyProperties(newMap, mapFromDb, "id", "owner");

        return mapFromDb;
    }

    @Override
    @Transactional
    public void delete(User owner, String id) {
        Map mapToDelete = getOneByOwner(owner, id);

        String pathToMap = fileStorageLocation + "/" + owner.getId();
        File fileToDelete = new File(pathToMap + "/" + mapToDelete.getFilename());
        fileToDelete.delete();

        mapRepo.delete(mapToDelete);
    }

    @Override
    @Transactional
    public int countByOwnerAndFilename(User owner, String filename) {
        return mapRepo.countByOwnerAndFilename(owner, filename);
    }

    private boolean sameFilenameIsInDb(User owner, String newFilename, String id) {
        Map foundMap = getOneByOwnerAndFilename(owner, newFilename);
        return foundMap != null && !foundMap.getId().equals(id);
    }

}