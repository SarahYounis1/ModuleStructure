package com.example.repository.repository;
import com.example.repository.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface TaskRepository extends JpaRepository<TaskEntity,Long> {
    void deleteAllByUser_Id(Long id);
    Page<TaskEntity>findAllByUser_Id(Long id, Pageable pageable);
    List<TaskEntity>findAllByUser_Id(Long id);
    List<TaskEntity> findAllByUser_IdAndEndDateIsAfterAndStartDateBefore
            (Long id,@Param("start") Date start,@Param("end") Date end);


    //No code is needed CRUD functions already exist within JPARepository

}