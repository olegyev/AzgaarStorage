package by.azgaar.storage.dto.assembler.impl;

import by.azgaar.storage.controller.MapController;
import by.azgaar.storage.dto.MapDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.Map;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MapDtoAssembler
        extends RepresentationModelAssemblerSupport<Map, MapDto>
        implements DtoAssemblerInterface<Map, MapDto> {

    public MapDtoAssembler() {
        super(MapController.class, MapDto.class);
    }

    @Override
    public MapDto toModel(Map map) {
        MapDto dto = createModelWithId(map.getId(), map);

        dto.setOwner(map.getOwner().getName());
        dto.setFileId(map.getFileId());
        dto.setFilename(map.getFilename());
        dto.setUpdated(map.getUpdated());
        dto.setVersion(map.getVersion());

        URI downloadPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/" + map.getFilename()).build().toUri();
        dto.setDownloadLink(downloadPath);
        
        dto.setThumbnail(map.getThumbnail());

        dto.add(linkTo(methodOn(MapController.class)
                .getAll(null, null, null))
                .withRel("maps"));

        return dto;
    }

}