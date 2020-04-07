package by.azgaar.storage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UploadDto extends AbstractDto {

    @NonNull
    private String fileName;

    @NonNull
    private String fileDownloadUri;

    @NonNull
    private String fileType;

    @NonNull
    private long size;

}