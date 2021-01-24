package by.azgaar.storage.service.impl;

import java.util.Calendar;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.exception.AccessDeniedException;
import by.azgaar.storage.exception.BadRequestException;
import by.azgaar.storage.exception.NotFoundException;
import by.azgaar.storage.repo.MapRepo;
import by.azgaar.storage.repo.specs.MapJpaSpecification;
import by.azgaar.storage.service.MapServiceInterface;

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
		if (!dbBodyIsOk(map)) {
			throw new BadRequestException("Map data does not contain all required fields");
		}
		return mapRepo.save(map);
	}

	@Override
	@Transactional
	public Page<Map> getAllByOwner(User owner, String filename, Pageable pageable) {
		Specification<Map> spec = Specification
				.where(MapJpaSpecification.userIdEqualsTo(owner.getId()))
				.and(filename == null ? null : MapJpaSpecification.filenameContains(filename));
		return mapRepo.findAll(spec, pageable);
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
			throw new NotFoundException("Map is not found");
		} else if (!map.getOwner().equals(owner)) {
			throw new AccessDeniedException("Access denied!");
		}

		return map;
	}

	@Override
	@Transactional
	public Map getOneByOwnerAndFilename(final User owner, final String filename) {
		return mapRepo.findByOwnerAndFilename(owner, filename);
	}
	
	@Override
	@Transactional
	public int countByOwner(final User owner) {
		return mapRepo.countByOwner(owner);
	}
	
	@Override
	@Transactional
	public int countByOwnerAndFilename(final User owner, final String filename) {
		return mapRepo.countByOwnerAndFilename(owner, filename);
	}
	
	@Override
	@Transactional
	public Map rename(final User owner, final Map oldMap, final Map newMap) {
		newMap.setOwner(owner);
		newMap.setUpdated(oldMap.getUpdated());
		
		if (!dbBodyIsOk(oldMap) || !dbBodyIsOk(newMap)) {
			throw new BadRequestException("Map data does not contain all required fields.");
		}
		
		if (sameFilenameIsInDb(owner, newMap.getFilename(), oldMap.getId())) {
			throw new BadRequestException("There is another map with the same filename.");
		}

		BeanUtils.copyProperties(newMap, oldMap, "id", "owner", "fileId");
		mapRepo.save(oldMap);
		return oldMap;
	}

	@Override
	@Transactional
	public Map updateFilename(final User owner, final Map oldMap, final Map newMap) {
		if (!dbBodyIsOk(oldMap) || !dbBodyIsOk(newMap)) {
			throw new BadRequestException("Map data does not contain all required fields.");
		}
		BeanUtils.copyProperties(newMap, oldMap, "id", "owner", "fileId");
		mapRepo.save(oldMap);
		return oldMap;
	}
	
	@Override
	@Transactional
	public Map updateFileId(final User owner, final Map oldMap, final Map newMap) {
		if (!dbBodyIsOk(oldMap) || !dbBodyIsOk(newMap)) {
			throw new BadRequestException("Map data does not contain all required fields.");
		}
		BeanUtils.copyProperties(newMap, oldMap, "id", "owner", "filename");
		mapRepo.save(oldMap);
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
	public int saveMapData(final User owner, final Map map, final boolean isQuickSave) {
		final Map mapFromDbByFilename = getOneByOwnerAndFilename(owner, map.getFilename());
		int occupiedSlots = countByOwner(owner);
		final boolean userHasMemorySlots = occupiedSlots < owner.getMemorySlotsNum();
		
		if (!userHasMemorySlots && mapFromDbByFilename == null) {
			throw new BadRequestException("Map cannot be stored. You are out of memory slots.");
		}
		
		map.setOwner(owner);
		map.setUpdated(Calendar.getInstance());

		if (mapFromDbByFilename == null && userHasMemorySlots) {
			// Save brand new map
			create(map);
			occupiedSlots++;
		} else {
			if (mapFromDbByFilename != null && map.getFileId().equals(mapFromDbByFilename.getFileId())) {
				// Rename or rewrite current existing map via "Save As" (not "Rename")
				updateFilename(owner, mapFromDbByFilename, map);
			} else if (mapFromDbByFilename != null && !isQuickSave) {
				// Rewrite another existing map
				updateFileId(owner, mapFromDbByFilename, map);
			} else {
				if (userHasMemorySlots) {
					// If collision occurred (map with the already stored filename was created and a user clicked Quick Save) - to prevent occasional rewriting
					map.setFilename(map.getFilename() + "-" + (countByOwnerAndFilename(owner, map.getFilename()) + 1));
					create(map);
					occupiedSlots++;
				} else {
					// Collision occurred, but a user has no more memory slots - to prevent storing by the new file ID
					throw new BadRequestException("Map cannot be stored. You are out of memory slots.");
				}
			}
		}

		int freeSlots = owner.getMemorySlotsNum() - occupiedSlots;
		return freeSlots;
	}

	private boolean sameFilenameIsInDb(User owner, String newFilename, long id) {
		Map foundMap = getOneByOwnerAndFilename(owner, newFilename);
		return foundMap != null && foundMap.getId() != id;
	}

	private boolean dbBodyIsOk(Map body) {
		return body.getOwner() != null &&
				body.getFileId() != null &&
				body.getFilename() != null &&
				body.getUpdated() != null &&
				body.getVersion() != null;
	}

}