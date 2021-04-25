package by.azgaar.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UploadDto extends AbstractDto {

    @NonNull
    private String ownerName;

    @NonNull
    private String filename;

    @NonNull
    private String shareLink;

    @NonNull
    private String fileType;

    private long size;

    private int freeSlots;

}