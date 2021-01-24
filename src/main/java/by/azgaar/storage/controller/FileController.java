package by.azgaar.storage.controller;

import by.azgaar.storage.dto.UploadDto;
import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.service.FileStorageServiceInterface;
import by.azgaar.storage.service.UserServiceInterface;

import com.amazonaws.services.s3.model.S3Object;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private final UserServiceInterface userService;
    private final FileStorageServiceInterface fileStorageService;

    @Autowired
    public FileController(final UserServiceInterface userService,
                          final FileStorageServiceInterface fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadDto> uploadMap(@AuthenticationPrincipal OAuth2User principal,
                                               @RequestPart("file") MultipartFile file,
                                               @RequestPart("map") Map map,
                                               @RequestParam("isQuickSave") boolean isQuickSave) {
        User owner = userService.retrieveUser(principal);
        int freeSlots = fileStorageService.putS3Map(owner, file, map, isQuickSave);
        String shareLink = fileStorageService.generateShareLink(owner, map.getFilename());
        UploadDto dto = new UploadDto(owner.getName(), map.getFilename(), shareLink, file.getContentType(), file.getSize(), freeSlots);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadMap(@AuthenticationPrincipal OAuth2User principal,
                                                @PathVariable String filename) {
        User owner = userService.retrieveUser(principal);
        S3Object mapToDownload = fileStorageService.getS3Map(owner, filename);
        String contentType = mapToDownload.getObjectMetadata().getContentType() != null ?
                mapToDownload.getObjectMetadata().getContentType() :
                "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(new InputStreamResource(mapToDownload.getObjectContent()));
    }
    
    @GetMapping("/getShareLink/{filename:.+}")
    public String getShareLink(@AuthenticationPrincipal OAuth2User principal,
    						   @PathVariable String filename) {
    	User owner = userService.retrieveUser(principal);
    	return fileStorageService.generateShareLink(owner, filename);
    }

}