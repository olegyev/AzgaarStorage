package by.azgaar.storage.dto.assembler.impl;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import by.azgaar.storage.controller.ClickCounterController;
import by.azgaar.storage.controller.MapController;
import by.azgaar.storage.dto.ClickEventDto;
import by.azgaar.storage.dto.MapDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.ClickEvent;
import by.azgaar.storage.entity.Map;

@Component
public class ClickCounterDtoAssembler extends RepresentationModelAssemblerSupport<ClickEvent, ClickEventDto> implements DtoAssemblerInterface<ClickEvent, ClickEventDto> {
	
	public ClickCounterDtoAssembler() {
        super(ClickCounterController.class, ClickEventDto.class);
    }
	
	@Override
    public ClickEventDto toModel(ClickEvent clickEvent) {
		ClickEventDto dto = createModelWithId(clickEvent.getId(), clickEvent);

        dto.setClickTimestamp(clickEvent.getClickTimestamp());

        dto.add(linkTo(methodOn(ClickCounterController.class)
                .getAll(null, null, null))
                .withRel("countClick"));

        return dto;
    }
	
}