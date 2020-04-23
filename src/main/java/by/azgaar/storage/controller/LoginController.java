package by.azgaar.storage.controller;

import by.azgaar.storage.dto.LoginDto;
import by.azgaar.storage.service.LoginServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "null")
public class LoginController {

    private final LoginServiceInterface loginService;

    @Autowired
    public LoginController(final LoginServiceInterface loginService) {
        this.loginService = loginService;
    }

    @GetMapping("login")
    public ResponseEntity<LoginDto> login() {
        return ResponseEntity.ok(loginService.compoundLinks());
    }

}