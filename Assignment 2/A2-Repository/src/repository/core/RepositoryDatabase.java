/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Airi
 */
public class RepositoryDatabase {

    private static RepositoryDatabase database_instance; // SINGLETON
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet result_set = null;

    private String user = "root";
    private String pass = "root1234";

    private RepositoryDatabase() {

        // 1. Get a connection to database
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BookRepo?serverTimezone=UTC", user, pass);
        } catch (SQLException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnectionInstance() {
        return connection;
    }

    // SINGLETON
    public static RepositoryDatabase getInstance() {
        if (database_instance == null) {
            database_instance = new RepositoryDatabase();
            System.out.println("RepositoryDatabase - Instance is created!");
        }
        return database_instance;
    }

    public Statement createStatement() {

        try {
            // 2. Create a statement
            statement = connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return statement;
    }

    public ResultSet executeQuery(String query) {
        try {
            createStatement();
            result_set = statement.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result_set;
    }

    public int executeUpdate(String query) {
        int update = 0;
        try {
            createStatement();
            update = statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return update;
    }

    public void cleanup() {

        try {
            if (result_set != null) {
                result_set.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws SQLException {
        RepositoryDatabase database = new RepositoryDatabase();
        database.createStatement();
        //database.executeUpdate("UPDATE book SET last_name = 'Hello2', first_name = 'Hello' WHERE (id = '2')");
        //database.executeUpdate("UPDATE `BookRepo`.`book` SET `image_data` = LOAD_FILE('src/java/repository/database/endofownership_photo_final.jpeg') WHERE (`id` = '2')");
        //database.executeUpdate("UPDATE `BookRepo`.`book` SET `image_data` = LOAD_FILE('endofownership_photo_final.jpeg') WHERE (`id` = '2');");
        //database.executeQuery("SELECT * FROM book");

        Session session = null;
        session = new Session();
        session.login("Jasmine", "test123");

        BookRepository b1 = BookRepository.getInstance();
        System.out.println();
        System.out.println("DROP TABLE");
        System.out.println();
        b1.dropBookTable();
        System.out.println("CREATE TABLE");
        System.out.println();

        b1.createBookTable(session);
        ArrayList<Book> books;

        CoverImage cover1 = new CoverImage();
        cover1.setMimeType("image/jpeg");
        cover1.setImagePath("./endofownership_photo_final.jpeg");

        Author author = new Author("Aurelius", "Marcus");
        Book book1 = new Book("Meditations", "Written in Greek, without any intention of publication, by the only Roman emperor", "0140449337", author,
                "Penguin Classic", "England", cover1);

        System.out.println("BEFORE adding Book 1:");
        books = b1.listAllBooks(session);
        try {
            System.out.println(b1.addNewBook(session, book1)); // GET ID
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Book book : books) {
            System.out.println(book);
        }

        Author author2 = new Author("Epictetus", "Unknown");
        Book book2 = new Book("Discourses, Fragments, Handbook", "About things that are within our power and those that are not.", "0199595186",
                author2, "Oxford University Press", "England");

        System.out.println();
        System.out.println("BEFORE adding Book 2:");
        books = b1.listAllBooks(session);
        try {
            System.out.println(b1.addNewBook(session, book2)); // GET ID
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Book book : books) {
            System.out.println(book);
        }

        Author author3 = new Author("Kishimi", "Ichiro");
        Book book3 = new Book("Courage to be Happy", "The Courage to be Happy is a profound insight into the way we should live our lives that has already sold more than one million copies in Japan.", "1911630210", author3, "Allen & Unwin", "London, England");

        System.out.println();
        System.out.println("BEFORE adding Book 3:");
        //books = b1.listAllBooks(session);
        try {
            System.out.println(b1.addNewBook(session, book3)); // Get ID
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        // --- UPDATE / DELETE / SEARCH -- //
        Author author4 = new Author("Laurent", "Deversa");
        System.out.println();
        System.out.println("BEFORE:");
        books = b1.listAllBooks(session);
        for (Book book : books) {
            System.out.println(book);
        }

        try {
            b1.updateBookInfo(session, 2, "Margin", "1232", author4); // UPDATE
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println();
        System.out.println("AFTER:");
        books = b1.listAllBooks(session);
        for (Book book : books) {
            System.out.println(book);
        }

        System.out.println();
        System.out.println("Delete one book:");
        try {
            b1.deleteBook(session, 3);
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        books = b1.listAllBooks(session);
        for (Book book : books) {
            System.out.println(book);
        }

        try {
            //b1.deleteAllBooks();
            System.out.println(b1.addNewBook(session, book3)); // Get ID
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println();
        System.out.println("After - Add another book:");
        books = b1.listAllBooks(session);
        System.out.println(books);

        System.out.println();
        System.out.println("GetBookInfo with ID 4");
        //Book resultBook = b1.getBookInfo(session, 4);
        System.out.println(b1.getBookInfo(session, 4));
        //System.out.println(resultBook.getTitle() == null);

        System.out.println();
        System.out.println("GetBookInfo with ISBN \"0140449337\"");
        System.out.println(b1.getBookInfo(session, "0140449337"));
        Book resultBook2 = b1.getBookInfo(session, "1212");
        System.out.println(resultBook2.getTitle() == null);

        /*
        // --- DATABASE: Cover ADD and GET BLOB --- //
        File file = new File("./endofownership_photo_final.jpeg");

        try {
            b1.setBookCoverImage(session, file, "image/jpeg", 2);
        } catch (RepositoryException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }


        String selectSQL = "SELECT image_data FROM book where id=?";
        PreparedStatement pstmt = database.connection.prepareStatement(selectSQL);
        pstmt.setInt(1, 2);
        ResultSet rs = pstmt.executeQuery();
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("image");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println("Writing to file " + file.getAbsolutePath());
        while (rs.next()) {
            InputStream input = rs.getBinaryStream("image_data");
            byte[] buffer = new byte[1024];
            try {
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
                output.close();
                //rs.close();
            } catch (IOException ex) {
                Logger.getLogger(RepositoryDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         */
        System.out.println();
        System.out.println("Book ArrayList: ");
        books = b1.listAllBooks(session);
        for (Book book : books) {
            System.out.println(book);
        }
    }

}
