package test.repository;

import org.junit.jupiter.api.Test;
import src.domain.entities.User;
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
        verify(dbMock).writeToFile(new ArrayList<>(), "User");
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
        verify(dbMock).writeToFile(expectedUsers, "User");
    }

    @Test
    public void testUpdateUser() throws IOException {
        DB dbMock = mock(DB.class);
        User user = new User("Larry");
        user.setScore(420);

        ArrayList<Object> expectedUsers = new ArrayList<>();
        expectedUsers.add(user);

        // DB returns Larry with a score of 420
        when(dbMock.readAll(User.class)).thenReturn(expectedUsers);

        UserRepository repo = new UserRepositoryDB(dbMock);

        // We increase Larry's score by 5 and save it
        user.setScore(425);
        repo.saveUser(user);

        expectedUsers = new ArrayList<>();
        expectedUsers.add(user);

        verify(dbMock).readAll(User.class);
        // Therefore, we expect the database to write the updated Larry with score 425
        verify(dbMock).writeToFile(expectedUsers, "User");
    }
}


