package com.example.domain.service;

import com.example.domain.aggregate.Task;

public interface IUserService {

    Task save(Task task);
    Task update(Task task);
    void deleteByID(Long id);
    Task getByID(Long id);
}
