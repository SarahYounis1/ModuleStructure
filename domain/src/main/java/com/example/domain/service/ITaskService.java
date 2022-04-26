package com.example.domain.service;

import com.example.domain.aggregate.Task;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface ITaskService {

    Task save(Task task);
    Task update(Task task ,Long id) throws AccessDeniedException;
    void deleteByID(Long id) throws AccessDeniedException;
    Task getByID(Long id) throws AccessDeniedException;
   // Page<Task> getTasks(Long pageSize, Long pageNumber);

    Page<Task> getTasks(Optional<Integer> page, Optional<String> sortDirection, Optional<String> sortBy);
}
