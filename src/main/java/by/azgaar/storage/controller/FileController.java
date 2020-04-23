package by.azgaar.storage.controller;

import by.azgaar.storage.dto.UploadDto;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.service.FileStorageServiceInterface;
import by.azgaar.storage.service.UserServiceInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URI;

@RestController
@CrossOrigin(origins = "null", allowCredentials = "true")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final UserServiceInterface userService;
    private final FileStorageServiceInterface fileStorageService;

    @Autowired
    public FileController(final UserServiceInterface userService,
                          final FileStorageServiceInterface fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    // Front-end: modules/save-and-load.js, from line 283.
    @PostMapping("upload")
    public ResponseEntity<UploadDto> uploadFile(@AuthenticationPrincipal OAuth2User principal,
                                                @RequestPart("file") MultipartFile file,
                                                @RequestPart("map") Map map) {
        User owner = userService.retrieveUser(principal);
        String filename = fileStorageService.storeFile(owner, file, map);
        URI downloadPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/" + filename).build().toUri();
        UploadDto dto = new UploadDto(owner.getName(), filename, downloadPath, file.getContentType(), file.getSize());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // Front-end: main.js, from line 178.
    @GetMapping("download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@AuthenticationPrincipal OAuth2User principal,
                                                 @PathVariable String filename,
                                                 HttpServletRequest request) {
        User owner = userService.retrieveUser(principal);
        Resource resource = fileStorageService.loadFileAsResource(owner, filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}