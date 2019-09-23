package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.service.domain.User;

public class UserMapper {
    public static User toDomainModel(UserDTO dto) {
        return new User(dto.getUsername(), dto.getPassword());
    }

    public static UserDTO toDto(User user) {
        return new UserDTO(user.getUsername(), user.getPassword());
    }
}