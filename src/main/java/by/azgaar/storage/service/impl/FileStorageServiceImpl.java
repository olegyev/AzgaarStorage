package by.azgaar.storage.service.impl;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;
import by.azgaar.storage.exception.BadRequestException;
import by.azgaar.storage.exception.FileStorageException;
import by.azgaar.storage.exception.NotFoundException;
import by.azgaar.storage.property.FileStorageProperties;
import by.azgaar.storage.service.FileStorageServiceInterface;
import by.azgaar.storage.service.MapServiceInterface;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.SetPublicAccessBlockRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileStorageServiceImpl implements FileStorageServiceInterface {

    private final AmazonS3 s3Client;
    private final String bucket;
    private final MapServiceInterface mapService;

    @Autowired
    public FileStorageServiceImpl(final FileStorageProperties fileStorageProperties,
                                  final MapServiceInterface mapService) {
        String bucket = fileStorageProperties.getS3Bucket();

        s3Client = AmazonS3ClientBuilder.standard().build();

        try {
            this.bucket = s3Client.doesBucketExistV2(bucket) ? bucket : s3Client.createBucket(bucket).getName();
        } catch (SdkClientException e) {
            throw new FileStorageException("Cannot create AWS S3 bucket where the uploaded files will be stored.");
        }

        s3Client.setPublicAccessBlock(new SetPublicAccessBlockRequest()
                .withBucketName(bucket)
                .withPublicAccessBlockConfiguration(new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(true)
                        .withIgnorePublicAcls(true)
                        .withBlockPublicPolicy(true)
                        .withRestrictPublicBuckets(true)));

        this.mapService = mapService;
    }

    @Override
    public String putS3Map(User owner, MultipartFile file, Map map) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (!isValid(filename)) {
                throw new FileStorageException("Filename contains invalid path sequence " + filename + ".");
            } else if (!bodyIsOk(map)) {
                throw new BadRequestException("Map data does not contain all required fields.");
            }

            mapService.saveMapData(owner, map);
            filename = map.getFilename();

            ObjectMetadata fileData = new ObjectMetadata();
            fileData.setContentType(file.getContentType());
            fileData.setContentLength(file.getSize());

            s3Client.putObject(bucket, (owner.getId() + "/" + filename), file.getInputStream(), fileData);

            return filename;
        } catch (IOException e) {
            throw new FileStorageException("Cannot store file " + filename + ". Please try again!");
        }
    }

    @Override
    public S3Object getS3Map(User owner, String filename) {
        Map mapToDownload = mapService.getOneByOwnerAndFilename(owner, filename);

        if (mapToDownload == null) {
            throw new NotFoundException("Map is not found.");
        }

        return s3Client.getObject(bucket, owner.getId() + "/" + filename);
    }

    @Override
    public void updateS3Map(String oldFilename, String newFilename) {
        s3Client.copyObject(
                bucket, oldFilename,
                bucket, newFilename
        );
        deleteS3Map(oldFilename);
    }

    @Override
    public void deleteS3Map(String filename) {
        s3Client.deleteObject(bucket, filename);
    }

    private boolean isValid(String filename) {
        return filename.matches("[-_A-Za-z0-9 ]+");
    }

    private boolean bodyIsOk(Map body) {
        return body.getFileId() != null &&
                body.getFilename() != null &&
                body.getUpdated() != null &&
                body.getVersion() != null;
    }

}