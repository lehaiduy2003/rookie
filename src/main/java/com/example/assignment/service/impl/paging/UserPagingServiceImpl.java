package com.example.assignment.service.impl.paging;

import com.example.assignment.annotation.Logging;
import com.example.assignment.dto.response.PagingRes;
import com.example.assignment.dto.response.UserRes;
import com.example.assignment.entity.User;
import com.example.assignment.mapper.UserMapper;
import com.example.assignment.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Logging
public class UserPagingServiceImpl extends PagingServiceImpl<UserRes, User, Long> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserPagingServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected PagingRes<UserRes> toPagingResult(Page<User> page, Function<User, UserRes> converter) {
        return userMapper.toPagingResult(page, converter);
    }

    @Override
    protected Page<User> findByRelatedId(Long id, Pageable pageable) {
        // Not implemented for users
        return Page.empty(pageable);
    }

    @Override
    protected Page<User> findByCriteria(String criteria, Pageable pageable) {
        // Not implemented for users
        return Page.empty(pageable);
    }

    @Override
    protected UserRes convertToDto(User entity) {
        return userMapper.toDto(entity);
    }
}
