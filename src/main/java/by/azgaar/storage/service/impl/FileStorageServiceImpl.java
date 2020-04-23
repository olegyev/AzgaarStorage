package by.azgaar.storage.service.impl;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.exception.BadRequestException;
import by.azgaar.storage.exception.FileStorageException;
import by.azgaar.storage.exception.NotFoundException;
import by.azgaar.storage.property.FileStorageProperties;
import by.azgaar.storage.service.FileStorageServiceInterface;
import by.azgaar.storage.service.MapServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageServiceInterface {

    private final Path fileStorageLocation;
    private final MapServiceInterface mapService;

    @Autowired
    public FileStorageServiceImpl(final FileStorageProperties fileStorageProperties,
                                  final MapServiceInterface mapService) {
        fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException("Cannot create directory where the uploaded files will be stored.");
        }

        this.mapService = mapService;
    }

    @Override
    public String storeFile(User owner, MultipartFile file, Map map) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (filename.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + filename);
            } else if (!mapIsOk(map)) {
                throw new BadRequestException("Map data does not contain all required fields.");
            }

            saveMapData(owner, map);

            Path userFolderPath;
            if (Files.notExists(Paths.get(fileStorageLocation + "/" + owner.getId()))) {
                try {
                    userFolderPath = Paths.get(fileStorageLocation + "/" + owner.getId());
                    Files.createDirectories(userFolderPath);
                } catch (Exception e) {
                    throw new FileStorageException("Cannot create directory for user " + owner.getName());
                }
            } else {
                userFolderPath = Paths.get(fileStorageLocation + "/" + owner.getId());
            }

            Path targetLocation = userFolderPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;

        } catch (IOException e) {
            throw new FileStorageException("Cannot store file " + filename + ". Please try again!");
        }
    }

    @Override
    public Resource loadFileAsResource(User owner, String fileName) {
        try {
            Map mapToDownload = mapService.getOneByOwnerAndFilename(owner, fileName);

            if (mapToDownload == null) {
                throw new NotFoundException("Map is not found.");
            }

            Path userFolderPath = Paths.get(fileStorageLocation + "/" + owner.getId());
            Path filePath = userFolderPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File is not found " + fileName);
            }

        } catch (MalformedURLException e) {
            throw new NotFoundException("File is not found " + fileName);
        }
    }

    private void saveMapData(User owner, Map map) {
        Map mapFromDb = mapService.getOneByOwnerAndFilename(owner, map.getFilename());

        if (mapFromDb == null) {
            map.setOwner(owner);
            mapService.create(map);
        } else {
            mapService.update(owner, mapFromDb.getId(), map);
        }
    }

    private boolean mapIsOk(Map map) {
        return map.getFilename() != null &&
                map.getUpdated() != null &&
                map.getVersion() != null &&
                map.getPicture() != null;
    }

}