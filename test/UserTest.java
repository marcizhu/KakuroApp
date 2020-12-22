package test;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import src.domain.controllers.UserCtrl;
import src.domain.entities.User;
import src.repository.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserTest {
    @Test
    public void testRegisterUserHappyPath() throws Exception {
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserCtrl userCtrl = new UserCtrl(userRepositoryMock);

        String username = "Alex";

        when(userRepositoryMock.getUser(username)).thenReturn(null);

        assertEquals(true, userCtrl.registerUser(username));
        verify(userRepositoryMock).getUser(username);
        ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock).saveUser(userArgument.capture());
        assertEquals(username, userArgument.getValue().getName());
    }

    @Test
    public void testRegisterUserAlreadyExists() throws Exception {
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserCtrl userCtrl = new UserCtrl(userRepositoryMock);

        String username = "Alex";
        User existentUser = new User(username);

        when(userRepositoryMock.getUser(username)).thenReturn(existentUser);

        assertEquals(false, userCtrl.registerUser(username));
        verify(userRepositoryMock).getUser(username);
        verify(userRepositoryMock, never()).saveUser(any(User.class));
    }

    @Test
    public void testGetUserList() throws Exception {
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserCtrl userCtrl = new UserCtrl(userRepositoryMock);

        User alex = new User("Alex");
        User xavi = new User("Xavi");
        User cesc = new User("Cesc");
        User marc = new User("Marc");
        ArrayList<User> usersInDB = new ArrayList<>(Arrays.asList(alex, xavi, cesc, marc));

        when(userRepositoryMock.getAllUsers()).thenReturn(usersInDB);

        ArrayList<String> expectedResponse = new ArrayList<>(Arrays.asList(alex.getName(), xavi.getName(), cesc.getName(), marc.getName()));
        assertEquals(expectedResponse, userCtrl.getUserList());
    }
}
