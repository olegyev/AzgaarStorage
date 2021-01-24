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
import com.amazonaws.services.s3.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class FileStorageServiceImpl implements FileStorageServiceInterface {

	private final FileStorageProperties fileStorageProperties;
    private final AmazonS3 s3Client;
    private final String bucket;
    private final MapServiceInterface mapService;

    @Autowired
    public FileStorageServiceImpl(final FileStorageProperties fileStorageProperties,
                                  final MapServiceInterface mapService) {
    	this.fileStorageProperties = fileStorageProperties;
    	
        String bucket = fileStorageProperties.getS3Bucket();

        s3Client = AmazonS3ClientBuilder.standard().build();

        try {
            this.bucket = s3Client.doesBucketExistV2(bucket) ? bucket : s3Client.createBucket(bucket).getName();
        } catch (SdkClientException e) {
            throw new FileStorageException("Cannot create AWS S3 bucket where the uploaded files will be stored");
        }

        s3Client.setPublicAccessBlock(new SetPublicAccessBlockRequest()
                .withBucketName(bucket)
                .withPublicAccessBlockConfiguration(new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(false)
                        .withIgnorePublicAcls(false)
                        .withBlockPublicPolicy(false)
                        .withRestrictPublicBuckets(false)));

        this.mapService = mapService;
    }

    @Override
    public int putS3Map(final User owner, final MultipartFile file, final Map map, final boolean isQuickSave) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String key = "";

        try {
            if (!isValid(filename)) {
                throw new FileStorageException("Filename contains invalid path sequence " + filename);
            }
            
            key = owner.getS3Key() + "/" + map.getFilename();

            int freeSlots = mapService.saveMapData(owner, map, isQuickSave);

            ObjectMetadata fileData = new ObjectMetadata();
            fileData.setContentType(file.getContentType());
            fileData.setContentLength(file.getSize());
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file.getInputStream(), fileData);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjectRequest);

            return freeSlots;
        } catch (IOException e) {
            throw new FileStorageException("Cannot store file " + filename + ". Please try again!");
        }
    }

    @Override
    public S3Object getS3Map(User owner, String filename) {
        Map mapToDownload = mapService.getOneByOwnerAndFilename(owner, filename);

        if (mapToDownload == null) {
            throw new NotFoundException("Map is not found");
        }

        try {
            return s3Client.getObject(bucket, owner.getS3Key() + "/" + filename);
        } catch (AmazonS3Exception e) {
            throw new BadRequestException("No such map in the cloud storage found!");
        }
    }

    @Override
    public void updateS3Map(String oldFilename, String newFilename) {
    	CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, oldFilename, bucket, newFilename);
    	copyObjectRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);
    	s3Client.copyObject(copyObjectRequest);
        deleteS3Map(oldFilename);
    }

    @Override
    public void deleteS3Map(String filename) {
        s3Client.deleteObject(bucket, filename);
    }
    
    @Override
    public String generateShareLink(User owner, String filename) {
    	final String s3Url = fileStorageProperties.getS3Url();
    	return UriComponentsBuilder
    			.fromHttpUrl(s3Url)
    			.path(owner.getS3Key() + "/" + filename)
				.build()
				.toUriString();
    }

    private boolean isValid(String filename) {
        return filename.matches("[-_A-Za-z0-9 ]+");
    }

}