package com.example.assignment.mapper;

import com.example.assignment.dto.request.UserCreationReq;
import com.example.assignment.dto.response.UserDetailsRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.Customer;
import com.example.assignment.entity.User;
import com.example.assignment.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;

/**
 * Mapper interface for converting between User entity and DTOs.
 * This interface uses MapStruct to generate the implementation at compile time.
 * The component model is set to "spring" to allow for dependency injection.
 * This mapper extends PagingMapper to provide pagination support.
 */
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public interface UserMapper extends PagingMapper {
    /**
     * Converts a UserCreation DTO to a User entity.
     *
     * @param userCreationReq the UserCreation DTO
     * @param userProfile  the UserProfile entity
     * @return the User entity
     */
    User toEntity(UserCreationReq userCreationReq, UserProfile userProfile);

    /**
     * Converts a User entity to a User DTO.
     * This method maps the UserProfile fields to the User DTO response.
     * If the User entity is a Customer, it will be mapped from customer to UserRes.
     *
     * @param user the User entity
     * @return the User DTO response
     */
    // for destructuring the UserProfile entity to UserRes
    @Mapping(source = "user.userProfile.firstName", target = "firstName")
    @Mapping(source = "user.userProfile.lastName", target = "lastName")
    @Mapping(source = "user.userProfile.avatar", target = "avatar")
    @SubclassMapping(source = Customer.class, target = UserRes.class)
    UserRes toDto(User user);

    /**
     * Converts a User entity to a UserDetails DTO.
     * This method maps the UserProfile fields to the UserDetails DTO response.
     *
     * @param user the User entity
     * @return the UserDetails DTO response
     */
    @Mapping(source = "user.userProfile.firstName", target = "firstName")
    @Mapping(source = "user.userProfile.lastName", target = "lastName")
    @Mapping(source = "user.userProfile.avatar", target = "avatar")
    @SubclassMapping(source = Customer.class, target = UserDetailsRes.class)
    UserDetailsRes toUserDetailsDto(User user);
    /**
     * subclass mapping from UserCreation DTO to Customer entity.
     *
     * @param userCreationReq the UserCreation DTO
     * @param userProfile  the UserProfile entity
     * @return the Customer entity
     */
    @SubclassMapping(source = UserCreationReq.class, target = Customer.class)
    @Mapping(target = "userProfile", source = "userProfile")
    Customer toCustomer(UserCreationReq userCreationReq, UserProfile userProfile);

}
