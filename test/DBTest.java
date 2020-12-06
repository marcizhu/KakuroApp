package test;

import org.junit.jupiter.api.*;
import src.repository.DB;


import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.StreamSupport;

public class DBTest {
    private DB testDB = new DB("test/database/");

    @BeforeAll
    public static void setUp () throws IOException {
        // Create database with empty files
        String path = "test/database/";
        String[] tables = {"testobject"};

        for (String s : tables) {
            File f = new File(path + s + ".json");
            if (!f.createNewFile()) {
                // This will empty the contents of the file
                new PrintWriter(f).close();
            }
        }
    }

    @AfterEach
    public void tearDown () throws IOException {
        // Empty each table in the database
        String path = "test/database/";
        String[] tables = {"testobject"};

        for (String s : tables) {
            File f = new File(path + s + ".json");
            // This will empty the contents of the file
            new PrintWriter(f).close();
        }
    }

    @Test
    public void testReadAll() throws IOException {
        ArrayList<Object> expectedObjects = new ArrayList<>();
        expectedObjects.add(new TestObject(0, "0", false));
        expectedObjects.add(new TestObject(1, "1", true));

        String rawJSON = "[{\"a\": 0, \"b\": \"0\", \"c\": false}, {\"a\": 1, \"b\": \"1\", \"c\": true}]";
        FileWriter writer = new FileWriter("test/database/testobject.json");
        writer.write(rawJSON);
        writer.close();

        ArrayList<Object> objects = testDB.readAll(TestObject.class);

        assertTrue(objects.equals(expectedObjects));
    }

    @Test
    public void testWriteToFile() throws IOException {
        System.out.println("TODO");
        // TODO
    }

    private class TestObject {
        private int a;
        private String b;
        private boolean c;

        public TestObject (int a, String b, boolean c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public boolean equals(Object obj) {
            TestObject o = (TestObject)obj;
            return o.a == a && o.b.equals(b) && o.c == c;
        }
    }

}

