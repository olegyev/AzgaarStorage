package by.azgaar.storage.controller;

import by.azgaar.storage.dto.MapDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.service.FileStorageServiceInterface;
import by.azgaar.storage.service.MapServiceInterface;
import by.azgaar.storage.service.UserServiceInterface;

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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("maps")
@CrossOrigin(origins = "null", allowCredentials = "true")
public class MapController {

    private final UserServiceInterface userService;
    private final MapServiceInterface mapService;
    private final FileStorageServiceInterface fileStorageService;
    private final DtoAssemblerInterface<Map, MapDto> assembler;
    private final PagedResourcesAssembler<Map> pagedResourcesAssembler;

    @Autowired
    public MapController(final UserServiceInterface userService,
                         final MapServiceInterface mapService,
                         final FileStorageServiceInterface fileStorageService,
                         final DtoAssemblerInterface<Map, MapDto> assembler) {
        this.userService = userService;
        this.mapService = mapService;
        this.fileStorageService = fileStorageService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = new PagedResourcesAssembler<>(null, null);
    }

    // For create method see: by.azgaar.storage.controller.FileController.uploadFile() = "/upload".

    @GetMapping
    public ResponseEntity<PagedModel<MapDto>> getAll(@AuthenticationPrincipal OAuth2User principal,
                                                     @PageableDefault(sort = {"updated"}, direction = Sort.Direction.DESC) Pageable defaultPageable) {
        User owner = userService.retrieveUser(principal);
        Page<Map> maps = mapService.getAllByOwner(owner, defaultPageable);
        PagedModel<MapDto> dto = pagedResourcesAssembler.toModel(maps, assembler);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("{id}")
    public ResponseEntity<MapDto> getOne(@AuthenticationPrincipal OAuth2User principal,
                                         @PathVariable String id) {
        User owner = userService.retrieveUser(principal);
        Map map = mapService.getOneByOwner(owner, id);
        MapDto dto = assembler.toModel(map);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{id}")
    //@CrossOrigin(methods = {RequestMethod.OPTIONS, RequestMethod.PUT})
    public ResponseEntity<MapDto> update(@AuthenticationPrincipal OAuth2User principal,
                                         @PathVariable String id,
                                         @Valid @RequestBody Map newMap) {
        User owner = userService.retrieveUser(principal);
        Map oldMap = mapService.getOneByOwner(owner, id);
        String oldMapFilename = oldMap.getFilename();
        Map updatedMap = mapService.update(owner, oldMap /*id*/, newMap);
        fileStorageService.updateS3Map(
                owner.getId() + "/" + oldMapFilename,
                owner.getId() + "/" + updatedMap.getFilename()
        );
        MapDto dto = assembler.toModel(updatedMap);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> delete(@AuthenticationPrincipal OAuth2User principal,
                                             @PathVariable String id) {
        User owner = userService.retrieveUser(principal);
        String mapToDeleteFilename = mapService.delete(owner, id);
        fileStorageService.deleteS3Map(owner.getId() + "/" + mapToDeleteFilename);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}