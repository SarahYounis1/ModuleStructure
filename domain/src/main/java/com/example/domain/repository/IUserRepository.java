package com.example.domain.repository;


import com.example.domain.aggregate.User;

import java.util.List;

public interface IUserRepository {
    User save(User user);
    User update(User user);
    void deleteByID(Long id);
    User getByID(Long id);
}
