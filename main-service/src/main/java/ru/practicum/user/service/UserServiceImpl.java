package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.common.PageableBuilder;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto getUser(Long userId) {
        log.info("getUser params: userId = {}", userId);
        return mapper.toDto(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found")));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("getUsers params: ids = {}, from = {}, size = {}", ids, from, size);
        PageRequest page = PageableBuilder.getPageable(from > 0 ? from / size : 0, size);

        if (ids == null || ids.isEmpty()) {
            log.info("getUsers call: findAll");
            return repository.findAll(page)
                    .map(mapper::toDto)
                    .getContent();
        }
        log.info("getUsers call: findAllByIdIn");
        return repository.findAllByIdIn(ids, page)
                .map(mapper::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public UserDto registerUser(UserRequestDto userRequestDto) {
        log.info("registerUser params: userRequestDto = {}", userRequestDto);
        repository.findByEmail(userRequestDto.getEmail()).ifPresent(user -> {
            throw new ConflictException("USer with same email already exist");
        });
        User user = repository.save(mapper.toEntity(userRequestDto));
        log.info("registerUser result user = {}", user);
        return mapper.toDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.info("delete params: userId = {}", userId);
        repository.deleteById(userId);
    }

    @Override
    public User getOrThrow(Long id) {
        var user = repository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found.");
        }
        return user.get();
    }
}
