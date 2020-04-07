package by.azgaar.storage.controller;

import by.azgaar.storage.dto.MapDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.service.UserServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("maps")
public class MapController {

    private final UserServiceInterface userService;
    private final DtoAssemblerInterface<Map, MapDto> assembler;

    @Autowired
    public MapController(final UserServiceInterface userService,
                         final DtoAssemblerInterface<Map, MapDto> assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping
    @CrossOrigin(origins = "null", allowCredentials = "true")
    public List<MapDto> getAll(@AuthenticationPrincipal OAuth2User principal) {
        User user = userService.retrieveUser(principal);
        return user.getMaps().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @CrossOrigin(origins = "null", allowCredentials = "true")
    public MapDto getOne(@AuthenticationPrincipal OAuth2User principal, @PathVariable long id) {
        User user = userService.retrieveUser(principal);
        return user.getMaps().stream()
                .sorted(Comparator.comparing(Map::getId))
                .filter(map -> id == map.getId())
                .map(assembler::toModel)
                .findAny()
                .orElseThrow(() -> new AccessDeniedException("Access denied!"));
    }

}