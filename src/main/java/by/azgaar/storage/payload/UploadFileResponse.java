package by.azgaar.storage.payload;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UploadFileResponse {

    @NonNull
    private String fileName;

    @NonNull
    private String fileDownloadUri;

    @NonNull
    private String fileType;

    @NonNull
    private long size;

}