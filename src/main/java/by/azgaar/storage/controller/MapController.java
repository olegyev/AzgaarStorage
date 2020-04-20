package by.azgaar.storage.controller;

import by.azgaar.storage.dto.MapDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
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
    private final DtoAssemblerInterface<Map, MapDto> assembler;
    private final PagedResourcesAssembler<Map> pagedResourcesAssembler;

    @Autowired
    public MapController(final UserServiceInterface userService,
                         final MapServiceInterface mapService,
                         final DtoAssemblerInterface<Map, MapDto> assembler) {
        this.userService = userService;
        this.mapService = mapService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = new PagedResourcesAssembler<>(null, null);
    }

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
                                         @PathVariable long id) {
        User owner = userService.retrieveUser(principal);
        Map map = mapService.getOneByOwner(owner, id);
        MapDto dto = assembler.toModel(map);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{id}")
    //@CrossOrigin(methods = {RequestMethod.OPTIONS, RequestMethod.PUT})
    public ResponseEntity<MapDto> update(@AuthenticationPrincipal OAuth2User principal,
                                         @PathVariable long id,
                                         @Valid @RequestBody Map newMap) {
        User owner = userService.retrieveUser(principal);
        Map map = mapService.update(owner, id, newMap);
        MapDto dto = assembler.toModel(map);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> delete(@AuthenticationPrincipal OAuth2User principal,
                                             @PathVariable long id) {
        User owner = userService.retrieveUser(principal);
        mapService.delete(owner, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}