package by.azgaar.storage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UploadDto extends AbstractDto {

    @NonNull
    private String ownerName;

    @NonNull
    private String fileName;

    @NonNull
    private URI downloadLink;

    @NonNull
    private String fileType;

    @NonNull
    private long size;

}