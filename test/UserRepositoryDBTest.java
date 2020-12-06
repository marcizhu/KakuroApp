package test;

import org.junit.jupiter.api.Test;
import src.domain.User;
import src.repository.DB;
import src.repository.UserRepositoryDB;
import src.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserRepositoryDBTest {
    @Test
    public void testGetUser() throws IOException {
        DB dbMock = mock(DB.class);

        User expectedUser = new User("Larry");
        ArrayList<Object> expectedUsers = new ArrayList<>();
        expectedUsers.add(expectedUser);
        expectedUsers.add(new User("John"));

        when(dbMock.readAll(User.class)).thenReturn(expectedUsers);
        
        UserRepository repo = new UserRepositoryDB(dbMock);
        User user = repo.getUser("Larry");

        assertTrue(expectedUser.equals(user));

        // Assert that the database driver was called properly
        verify(dbMock).readAll(User.class);
    }

    @Test
    public void testDeleteUser() throws IOException {
        DB dbMock = mock(DB.class);
        User user = new User("Larry");
        ArrayList<Object> expectedUsers = new ArrayList<>();
        expectedUsers.add(user);

        when(dbMock.readAll(User.class)).thenReturn(expectedUsers);

        UserRepository repo = new UserRepositoryDB(dbMock);
        repo.deleteUser(user);

        // Assert that the database driver was called properly
        verify(dbMock).readAll(User.class);
        verify(dbMock).writeToFile(new ArrayList<>(), "user");
    }

    @Test
    public void testSaveUser() throws IOException {
        DB dbMock = mock(DB.class);
        User user = new User("Larry");

        ArrayList<Object> expectedUsers = new ArrayList<>();
        expectedUsers.add(user);

        when(dbMock.readAll(User.class)).thenReturn(new ArrayList<>());

        UserRepository repo = new UserRepositoryDB(dbMock);
        repo.saveUser(user);


        // Assert that the database driver was called properly
        verify(dbMock).readAll(User.class);
        verify(dbMock).writeToFile(expectedUsers, "user");
    }
}


