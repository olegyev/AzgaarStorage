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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MapServiceImpl implements MapServiceInterface {

    private final MapRepo mapRepo;

    @Autowired
    public MapServiceImpl(final MapRepo mapRepo) {
        this.mapRepo = mapRepo;
    }

    @Override
    @Transactional
    public Map create(Map map) {
        if (!bodyIsOk(map)) {
            throw new BadRequestException("Map data does not contain all required fields.");
        }

        return mapRepo.save(map);
    }

    @Override
    @Transactional
    public Page<Map> getAllByOwner(User owner, Pageable pageable) {
        return mapRepo.findAllByOwner(owner, pageable);
    }

    @Override
    @Transactional
    public Map getOneById(long id) {
        return mapRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Map getOneByOwner(User owner, long id) {
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
    public Map getOneByOwnerAndFileIdAndFilename(User owner, String fileId, String filename) {
        return mapRepo.findByOwnerAndFileIdAndFilename(owner, fileId, filename);
    }

    @Override
    @Transactional
    public Map update(User owner, Map oldMap, Map newMap) {
        if (sameFilenameIsInDb(owner, newMap.getFilename(), oldMap.getId())) {
            throw new BadRequestException("There is another map with the same filename in DB for logged user.");
        } else if (!bodyIsOk(newMap)) {
            throw new BadRequestException("Map data does not contain all required fields.");
        }

        BeanUtils.copyProperties(newMap, oldMap, "id", "owner", "fileId");

        return oldMap;
    }

    @Override
    @Transactional
    public String delete(User owner, long id) {
        Map mapToDelete = getOneByOwner(owner, id);
        mapRepo.delete(mapToDelete);
        return mapToDelete.getFilename();
    }

    @Override
    @Transactional
    public int saveMapData(User owner, Map map) {
        Map mapFromDbByFilename = getOneByOwnerAndFilename(owner, map.getFilename());
        Map mapFromDbByFileIdAndFilename = getOneByOwnerAndFileIdAndFilename(owner, map.getFileId(), map.getFilename());
        int occupiedSlots = mapRepo.countByOwnerAndFileId(owner, map.getFileId());

        if (mapFromDbByFileIdAndFilename == null && occupiedSlots == owner.getMemorySlotsNum()) {
            throw new BadRequestException("Map cannot be stored. You are out of memory slots for this map.");
        }

        if (mapFromDbByFilename == null) {
            map.setOwner(owner);
            create(map);
            occupiedSlots++;
        } else {
            if (map.getFileId().equals(mapFromDbByFilename.getFileId())) {
                update(owner, mapFromDbByFilename, map);
            } else {
                map.setOwner(owner);
                map.setFilename(map.getFilename() + "-" + (mapRepo.countByOwnerAndFilename(owner, map.getFilename()) + 1));
                create(map);
                occupiedSlots++;
            }
        }

        int freeSlots = owner.getMemorySlotsNum() - occupiedSlots;
        return freeSlots;
    }

    private boolean sameFilenameIsInDb(User owner, String newFilename, long id) {
        Map foundMap = getOneByOwnerAndFilename(owner, newFilename);
        return foundMap != null && foundMap.getId() != id;
    }

    private boolean bodyIsOk(Map body) {
        return body.getFileId() != null &&
                body.getFilename() != null &&
                body.getUpdated() != null &&
                body.getVersion() != null;
    }

}