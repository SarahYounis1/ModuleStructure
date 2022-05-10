package com.example.repository.adapter;
import com.example.domain.aggregate.Task;
import com.example.exception.NotAllowedDateException;
import com.example.repository.entity.TaskEntity;
import com.example.repository.entity.UserEntity;
import com.example.repository.repository.TaskRepository;
import com.example.repository.repository.UserRepository;
import com.sun.xml.bind.v2.schemagen.episode.SchemaBindings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceAdapterTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TaskServiceAdapter taskServiceImplementation;
    private final ModelMapper modelMapper = new ModelMapper();

    private final UserEntity user = new UserEntity(1L, "Sarah", "Sarahajam@gmail.com", "sarahY", "sarah123", 21);
    private final UserEntity userF = new UserEntity(2L, "Sarah2", "Sarahajam2@gmail.com", "sarahY2", "sarah1232", 23);
    private final TaskEntity task = new TaskEntity("Testing the application", false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
    private final TaskEntity task1 = new TaskEntity("description", false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));

    @Test
    void getAllTasks() {
        List<TaskEntity> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TaskEntity task = new TaskEntity("description", false, null, null);
            tasks.add(task); }
        Page<TaskEntity> tasksPage = new PageImpl<>(tasks, PageRequest.of(0, 3), tasks.size());
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        Optional<Integer> page = Optional.of(1);
        Optional<String> sortBy = Optional.of("id");
        Optional<String> sortDirection = Optional.of("asc");
        when(taskRepository.findAllByUser_Id(this.user.getId(),
                PageRequest.of(page.orElse(0), 5,
                        Sort.Direction.fromString(sortDirection.orElse("asc")),
                        sortBy.orElse("id")))).thenReturn(tasksPage);
        assertEquals(tasksPage.stream().count(), taskServiceImplementation.getTasks(page, sortDirection, sortBy).stream().count());
    }

    private TaskEntity convertToEntity(Task task){

        return modelMapper.map(task, TaskEntity.class);
    }

    private Task convertToModel(TaskEntity taskEntity){
        return modelMapper.map(taskEntity, Task.class);
    }
    @Test
    void getTaskPass() throws AccessDeniedException {
        this.task.setUser(this.user);
        this.user.addTask(this.task);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.user));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        assertEquals(this.task.getId(), taskServiceImplementation.getByID(this.task.getId()).getId());
    }

    @Test
    void getTaskFailAccess() {
        this.task.setUser(this.user);
        this.user.addTask(this.task);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.userF));//return another user logged in
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        assertThrows(AccessDeniedException.class, () -> taskServiceImplementation.getByID(this.task.getId()));
    }


    @Test
    void createTaskPass(){
       // this.task.setId(1L);
        this.task.setUser(this.user);
        this.user.addTask(this.task);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        task.setUser(user);
        lenient().when(taskRepository.save(task)).thenReturn(task);
        Task taskM = convertToModel(task);
        System.out.println(taskM);
        System.out.println(taskM);
        assertEquals(task.getId(), taskServiceImplementation.save(taskM).getId());

    }

    @Test
    void createTaskFail() throws NotAllowedDateException{
        //this.task.setId(4L);
        this.task.setUser(this.user);
        this.user.addTask(this.task);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        List<TaskEntity> tasks = new ArrayList<>();
        tasks.add(this.task1);
        when(taskRepository.findAllByUser_IdAndEndDateIsAfterAndStartDateBefore
                (this.user.getId(),this.task.getStartDate(),this.task.getEndDate())).thenReturn(tasks);
        assertThrows(NotAllowedDateException.class, ()-> taskServiceImplementation.save(convertToModel(this.task)));
    }

    @Test
    void editTaskPass() throws AccessDeniedException{
        this.task.setUser(this.user);
        this.user.addTask(task);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.user));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        when(taskRepository.save(this.task)).thenReturn(this.task);
        assertEquals(this.task.getId(),taskServiceImplementation.update(convertToModel(task),this.task.getId() ,task.getUser()).getId());
    }
    @Test
    void editTaskFail() throws AccessDeniedException{
        this.task.setUser(this.user);
        user.addTask(task);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.user));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.userF);
        assertThrows(AccessDeniedException.class,()->taskServiceImplementation.update(convertToModel(task),this.task.getId(),task.getUser()));
    }
    @Test
    void editTaskFailDate() throws NotAllowedDateException {
        this.task.setUser(this.user);
        this.user.addTask(this.task);
        this.task.setId(1L);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.user));
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(this.user);
        List<TaskEntity> tasks = new ArrayList<>();
        tasks.add(this.task1);
        when(taskRepository.findAllByUser_IdAndEndDateIsAfterAndStartDateBefore
                (this.user.getId(),this.task.getStartDate(),this.task.getEndDate())).thenReturn(tasks);
        assertThrows(NotAllowedDateException.class, ()-> taskServiceImplementation.update(convertToModel(this.task),this.task.getId(),this.task.getUser()));
    }

    @Test
    void  deleteTaskPass()throws AccessDeniedException{
        this.task.setUser(this.user);
        user.addTask(task);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.user));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.user);
        when(taskRepository.existsById(this.task.getId())).thenReturn(true);
        taskServiceImplementation.deleteByID(this.task.getId());
        verify(taskRepository,times(1)).deleteById(task.getId());
    }
    @Test
    void  deleteTaskFail()throws AccessDeniedException{
        this.task.setUser(this.user);
        this.user.addTask(task);
        this.task.setId(1L);
        when(taskRepository.findById(this.task.getId())).thenReturn(Optional.of(this.task));
        when(userRepository.findById(this.task.getUserId())).thenReturn(Optional.of(this.user));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).thenReturn(this.userF);
        assertThrows(AccessDeniedException.class,()->taskServiceImplementation.deleteByID(this.task.getId()));
    }


}
