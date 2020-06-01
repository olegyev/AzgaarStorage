package by.azgaar.storage.dto.assembler.impl;

import by.azgaar.storage.controller.UserController;
import by.azgaar.storage.dto.UserDto;
import by.azgaar.storage.dto.assembler.DtoAssemblerInterface;
import by.azgaar.storage.entity.User;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserDtoAssembler
        extends RepresentationModelAssemblerSupport<User, UserDto>
        implements DtoAssemblerInterface<User, UserDto> {

    public UserDtoAssembler() {
        super(UserController.class, UserDto.class);
    }

    @Override
    public UserDto toModel(User user) {
        UserDto dto = instantiateModel(user);

        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMemorySlotsNum(user.getMemorySlotsNum());
        dto.setFirstVisit(user.getFirstVisit());
        dto.setLastVisit(user.getLastVisit());

        return dto;
    }

}