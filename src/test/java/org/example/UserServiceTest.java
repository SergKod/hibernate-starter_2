package org.example;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;

public class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userDao);
    }

    @Test
    public void testCreateUser() {

        // Мокаем Scanner, чтобы эмулировать ввод: имя, email, возраст
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("John", "john@example.com", "30");


        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return null;
        }).when(userDao).save(any(User.class));

        userService.createUser(scanner);

        verify(userDao).save(any(User.class));
    }

    @Test
    public void testListUsers_Empty() {
        when(userDao.findAll()).thenReturn(Collections.emptyList());
        userService.listUsers();
        verify(userDao).findAll();
    }

    @Test
    public void testListUsers_NotEmpty() {
        User user = new User("John", "john@example.com", 30);
        when(userDao.findAll()).thenReturn(List.of(user));
        userService.listUsers();
        verify(userDao).findAll();
    }

    @Test
    public void testFindUser_Found() {
        Scanner scanner = mock(Scanner.class);

        // Последовательно: ID, новое имя, новый email, новый возраст
        when(scanner.nextLine()).thenReturn("1");
        User user = new User("John", "john@example.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        userService.findUser(scanner);
        verify(userDao).findById(1L);
    }

    @Test
    public void testFindUser_NotFound() {
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("2");
        when(userDao.findById(2L)).thenReturn(Optional.empty());

        userService.findUser(scanner);
        verify(userDao).findById(2L);
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("1");
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        userService.updateUser(scanner);
        verify(userDao).findById(1L);
        verify(userDao, never()).update(any());
    }

    @Test
    public void testUpdateUser_UserFound() {
        Scanner scanner = mock(Scanner.class);

        // ввод ID, имя, email, возраст
        when(scanner.nextLine()).thenReturn("1", "NewName", "newemail@example.com", "35");

        User user = new User("John", "john@example.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUser(scanner);

        verify(userDao).update(user);
        assertEquals("NewName", user.getName());
        assertEquals("newemail@example.com", user.getEmail());
        assertEquals(35, user.getAge());
    }

    @Test
    public void testDeleteUser() {
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("1");
        doNothing().when(userDao).delete(1L);

        userService.deleteUser(scanner);

        verify(userDao).delete(1L);
    }
}
