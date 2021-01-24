package by.azgaar.storage.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.azgaar.storage.dto.MapDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.service.FileStorageServiceInterface;
import by.azgaar.storage.service.MapServiceInterface;
import by.azgaar.storage.service.UserServiceInterface;

@RestController
@RequestMapping("/maps")
public class MapController {

	private final UserServiceInterface userService;
	private final MapServiceInterface mapService;
	private final FileStorageServiceInterface fileStorageService;
	private final DtoAssemblerInterface<Map, MapDto> assembler;
	private final PagedResourcesAssembler<Map> pagedResourcesAssembler;

	@Autowired
	public MapController(final UserServiceInterface userService, final MapServiceInterface mapService,
			final FileStorageServiceInterface fileStorageService, final DtoAssemblerInterface<Map, MapDto> assembler) {
		this.userService = userService;
		this.mapService = mapService;
		this.fileStorageService = fileStorageService;
		this.assembler = assembler;
		this.pagedResourcesAssembler = new PagedResourcesAssembler<>(null, null);
	}

	// For create method see:
	// by.azgaar.storage.controller.FileController.uploadMap() = "/upload".

	@GetMapping
	public ResponseEntity<PagedModel<MapDto>> getAll(@AuthenticationPrincipal OAuth2User principal,
			@RequestParam(required = false) String filename,
			@PageableDefault(sort = { "updated" }, direction = Sort.Direction.DESC) Pageable defaultPageable) {
		User owner = userService.retrieveUser(principal);
		Page<Map> maps = mapService.getAllByOwner(owner, filename, defaultPageable);
		PagedModel<MapDto> dto = pagedResourcesAssembler.toModel(maps, assembler);
		return ResponseEntity.ok(dto);
	}

	@GetMapping("{id}")
	public ResponseEntity<MapDto> getOne(@AuthenticationPrincipal OAuth2User principal, @PathVariable long id) {
		User owner = userService.retrieveUser(principal);
		Map map = mapService.getOneByOwner(owner, id);
		MapDto dto = assembler.toModel(map);
		System.out.println(dto.getUpdated().getTime());
		return ResponseEntity.ok(dto);
	}

	@PutMapping("{id}")
	public ResponseEntity<MapDto> rename(@AuthenticationPrincipal OAuth2User principal,
										 @PathVariable long id,
										 @Valid @RequestBody Map newMap) {
		User owner = userService.retrieveUser(principal);
		Map oldMap = mapService.getOneByOwner(owner, id);
		final String oldMapFilename = oldMap.getFilename();
		Map renamedMap = mapService.rename(owner, oldMap, newMap);
		fileStorageService.updateS3Map(owner.getS3Key() + "/" + oldMapFilename, owner.getS3Key() + "/" + renamedMap.getFilename());
		MapDto dto = assembler.toModel(renamedMap);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<HttpStatus> delete(@AuthenticationPrincipal OAuth2User principal, @PathVariable long id) {
		User owner = userService.retrieveUser(principal);
		String mapToDeleteFilename = mapService.delete(owner, id);
		fileStorageService.deleteS3Map(owner.getS3Key() + "/" + mapToDeleteFilename);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}