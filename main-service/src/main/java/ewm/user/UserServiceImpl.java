package ewm.user;

import ewm.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static ewm.utils.Helper.findUserById;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User newUser = userRepository.save(UserMapper.toEntity(userDto));
        return UserMapper.toDto(newUser);
    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        getUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> findUsers(List<Integer> ids, Integer from, Integer size) {
        if (ids != null) {
            List<User> users = userRepository.findAllById(ids);
            return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
        } else {
            PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
            Page<User> users = userRepository.findAll(pageable);
            return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    public void getUserById(int id) {
        UserMapper.toDto(findUserById(userRepository, id));
    }
}
