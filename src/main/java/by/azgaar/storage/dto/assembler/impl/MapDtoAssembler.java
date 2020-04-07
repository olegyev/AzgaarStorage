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
        MapDto dto = createModelWithId(map.getId(), map); // instantiateModel(map);

        dto.setFilename(map.getFilename());
        dto.setDescription(map.getDescription());
        dto.setCreated(map.getCreated());
        dto.setUpdated(map.getUpdated());
        dto.setDeleted(map.getDeleted());

        URI downloadPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/" + map.getFilename()).build().toUri();
        dto.setDownloadLink(downloadPath);

        dto.add(linkTo(methodOn(MapController.class)
                .getAll(null))
                .withRel("maps"));

        return dto;
    }

}