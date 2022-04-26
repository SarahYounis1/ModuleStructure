package com.example.service.service;

import com.example.domain.aggregate.Task;
import com.example.domain.repository.ITaskRepository;
import com.example.domain.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements ITaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private ITaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        LOGGER.debug("Saving a new task : {}", task);
        return taskRepository.save(task);
    }

    @Override
    public Task update(Task task ,Long id) throws AccessDeniedException {
        LOGGER.debug("Updating a task : {}", task);
        return taskRepository.update(task ,id);
    }

    @Override
    public void deleteByID(Long id) throws AccessDeniedException {
        LOGGER.debug("Deleting the task with the id equal to {}", id);
        taskRepository.deleteByID(id);
    }

    @Override
    public Task getByID(Long id) throws AccessDeniedException {
        LOGGER.debug("Getting the task with the id equal to {}", id);
        return taskRepository.getByID(id);
    }

    @Override
    public Page<Task> getTasks(Optional<Integer> page, Optional<String> sortDirection, Optional<String> sortBy) {
        LOGGER.debug("Getting {} of tasks in the page equal to {}", page);
        return taskRepository.getTasks(page,sortDirection,sortBy);
    }

//    @Override
//    public Page<Task> getTasks(Long pageSize, Long pageNumber) {
//        LOGGER.debug("Getting {} of tasks in the page equal to {}", pageSize, pageNumber);
//        return taskRepository.getTasks(pageSize, pageNumber);
//    }
}
