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
		if (!clientBodyIsOk(newMap)) {
			throw new BadRequestException("Map data from client should contain file ID, filename and version."
					+ " Owner and update date are set on the server");
		} else if (!dbBodyIsOk(oldMap)) {
			throw new BadRequestException("Map data does not contain all required fields");
		} else if (sameFilenameIsInDb(owner, newMap.getFilename(), oldMap.getId())) {
			throw new BadRequestException("There is another map with the same filename");
		}

		BeanUtils.copyProperties(newMap, oldMap, "id", "owner", "fileId", "updated");

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

		if (!clientBodyIsOk(map)) {
			throw new BadRequestException("Map data should contain file ID, filename and version."
					+ " Owner and update date are set on the server");
		} else if (mapFromDbByFileIdAndFilename == null && occupiedSlots == owner.getMemorySlotsNum()) {
			throw new BadRequestException("Map cannot be stored. You are out of memory slots for this map");
		}

		if (mapFromDbByFilename == null) {
			map.setOwner(owner);
			map.setUpdated(Calendar.getInstance());
			create(map);
			occupiedSlots++;
		} else {
			if (map.getFileId().equals(mapFromDbByFilename.getFileId())) {
				mapFromDbByFilename.setUpdated(Calendar.getInstance());
				update(owner, mapFromDbByFilename, map);
			} else {
				map.setOwner(owner);
				map.setUpdated(Calendar.getInstance());
				map.setFilename(
						map.getFilename() + "-" + (mapRepo.countByOwnerAndFilename(owner, map.getFilename()) + 1));
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

	private boolean dbBodyIsOk(Map body) {
		return body.getOwner() != null && body.getFileId() != null && body.getFilename() != null
				&& body.getUpdated() != null && body.getVersion() != null;
	}

	private boolean clientBodyIsOk(Map body) {
		return body.getOwner() == null && body.getUpdated() == null && body.getFileId() != null
				&& body.getFilename() != null && body.getVersion() != null;
	}

}