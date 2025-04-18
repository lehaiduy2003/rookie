package com.example.assignment.mapper;

import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.entity.UserProfile;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between UserProfile entity and DTOs.
 * This mapper used by UserMapper to convert UserCreation and UserInfoUpdating DTOs to UserProfile entity.
 * The component model is set to "spring" to allow for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    /**
     * Converts a UserCreation DTO to a UserProfile entity.
     *
     * @param userCreationReq the UserCreation DTO
     * @return the UserProfile entity
     */
    UserProfile toEntity(UserCreationReq userCreationReq);

}
