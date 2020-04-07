package by.azgaar.storage.dto.assembler;

import by.azgaar.storage.dto.AbstractDto;
import by.azgaar.storage.entity.AzgaarStorageEntity;

import org.springframework.hateoas.server.RepresentationModelAssembler;

public interface DtoAssemblerInterface<T extends AzgaarStorageEntity, S extends AbstractDto> extends RepresentationModelAssembler<T, S> {

    S toModel(T t);

}