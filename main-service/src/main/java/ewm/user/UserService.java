package ewm.user;

import ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    void deleteUserById(int id);

    void getUserById(int id);

    List<UserDto> findUsers(List<Integer> ids, Integer from, Integer size);
}
