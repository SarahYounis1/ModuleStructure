package com.example.repository.adapter;
import com.example.domain.aggregate.Task;
import com.example.domain.repository.ITaskRepository;
import com.example.exception.NotAllowedDateException;
import com.example.exception.NotFoundException;
import com.example.repository.entity.TaskEntity;
import com.example.repository.entity.UserEntity;
import com.example.repository.repository.TaskRepository;
import com.example.repository.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TaskServiceAdapter implements ITaskRepository {

   private TaskRepository taskRepository;
    private UserRepository userRepository;
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private ModelMapper modelMapper=new ModelMapper();

    @Autowired
    public TaskServiceAdapter(UserRepository userRepository , TaskRepository taskRepository) {
       this.taskRepository = taskRepository;
        this.userRepository=userRepository;
    }

    public void checkTimeValidation(TaskEntity task , boolean edit){
        UserEntity requestingUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //get user from token
        Date startDate = task.getStartDate();
        Date endDate = task.getEndDate();
        List<TaskEntity> tasks = taskRepository.findAllByUser_IdAndEndDateIsAfterAndStartDateBefore(
                requestingUser.getId(),startDate,endDate);
        int count= tasks.size();
        if(count>1)throw new NotAllowedDateException("invalid Date");
        else if (count==1 & !edit)throw new NotAllowedDateException("invalid time");
        else if(count==1 && edit) {
            if(tasks.get(0).getId() != task.getId())throw new NotAllowedDateException("invalid time");
        }

    }
    private TaskEntity convertToEntity(Task task){
        return modelMapper.map(task, TaskEntity.class);
    }

    private Task convertToModel(TaskEntity taskEntity){
        return modelMapper.map(taskEntity, Task.class);
    }

    @Override
    public Task save(Task task) {
        TaskEntity taskE = convertToEntity(task);
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkTimeValidation(taskE,false);
        taskE.setUser(requestingUser);
        taskRepository.save(taskE);
        requestingUser.addTask(taskE);
        userRepository.save(requestingUser);
      return convertToModel(taskE) ;
    }

    @Override
    public Task update(Task task, Long id ,UserEntity u) throws AccessDeniedException {
        TaskEntity taskE = convertToEntity(task);
        taskE.setUser(u);
        TaskEntity taskR = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("task not found"+id));
        UserEntity requestedUser=userRepository.findById(taskE.getUserId()).orElseThrow(() -> new NotFoundException("User not found" + taskR.getUserId()));
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue()
                && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            checkTimeValidation(taskE,true);
            taskR.setDescription(taskE.getDescription());
            taskR.setCompleted(taskE.isCompleted());
            taskR.setUser(requestingUser);
            taskR.setStartDate(taskE.getStartDate());
            taskR.setEndDate(taskE.getEndDate());
            taskRepository.save(taskR);
            return convertToModel(taskR);
        }
        else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }
    }

    @Override
    public void deleteByID(Long id) throws AccessDeniedException {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not Found" +id));
        UserEntity requestedUser=userRepository.findById(task.getUserId()).orElseThrow(() -> new NotFoundException("User not found "+task.getUserId()));
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
            }
        }
        else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }
    }

    @Override
    public Task getByID(Long id) throws AccessDeniedException {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task Not Found" +id));
        UserEntity requestedUser = userRepository.findById(task.getUserId()).orElseThrow(() -> new NotFoundException("User not found"+task.getUserId()));
        UserEntity requestingUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword()))
            return convertToModel(task);
        else throw new AccessDeniedException("You are not allowed to access this page!");
    }

    @Override
    public Page<Task> getTasks(Optional<Integer> page, Optional<String> sortDirection, Optional<String> sortBy) {
        UserEntity requestingUser= (UserEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Page<TaskEntity> pa = taskRepository.findAllByUser_Id(requestingUser.getId(), PageRequest.of(page.orElse(0), 5,
                Sort.Direction.fromString(sortDirection.orElse("desc")),sortBy.orElse("id")));
        return  new PageImpl<Task>(pa.stream().map(entity -> convertToModel(entity)).collect(Collectors.toList()));
    }

}
