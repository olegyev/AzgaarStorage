package by.azgaar.storage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.Calendar;

@Data
@EqualsAndHashCode(callSuper = false)
public class MapDto extends AbstractDto {

    private String filename;
    private String description;
    private Calendar created;
    private Calendar updated;
    private Calendar deleted;
    private URI downloadLink;

}