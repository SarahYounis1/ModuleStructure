package com.example.domain.repository;
import com.example.domain.aggregate.Task;
import com.example.repository.entity.UserEntity;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

public interface ITaskRepository {

    Task save(Task task);
    //Task update(Task task, Long id) throws AccessDeniedException;
    Task update(Task task, Long id, UserEntity u) throws AccessDeniedException;
    void deleteByID(Long id) throws AccessDeniedException;
    Task getByID(Long id) throws AccessDeniedException;
  //  List<Task> getTasks(Long pageSize, Long pageNumber);
    Page<Task> getTasks(Optional<Integer> page, Optional<String> sortDirection, Optional<String> sortBy);
}
