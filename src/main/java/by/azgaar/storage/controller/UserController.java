package by.azgaar.storage.controller;

import by.azgaar.storage.dto.UserDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.service.UserServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserServiceInterface userService;
    private final DtoAssemblerInterface<User, UserDto> assembler;

    @Autowired
    public UserController(final UserServiceInterface userService,
                          final DtoAssemblerInterface<User, UserDto> assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping("/user-data")
    public ResponseEntity<UserDto> getUserData(@AuthenticationPrincipal OAuth2User principal) {
        User loggedUser = userService.retrieveUser(principal);
        UserDto dto = assembler.toModel(loggedUser);
        return ResponseEntity.ok(dto);
    }

}