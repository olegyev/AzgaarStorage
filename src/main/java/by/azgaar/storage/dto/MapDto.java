package by.azgaar.storage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.Calendar;

@Data
@EqualsAndHashCode(callSuper = false)
public class MapDto extends AbstractDto {

    private String owner;

    private String filename;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Calendar updated;

    private String version;

    private URI downloadLink;

}