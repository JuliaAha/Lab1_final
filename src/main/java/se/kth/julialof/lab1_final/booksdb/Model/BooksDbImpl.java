package se.kth.julialof.lab1_final.booksdb.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BooksDbImpl implements BooksDbInterface {

    private List<Book> books;
    private Connection connection = null;
    private final String user = "library_user";
    private final String pwd = "pass123";

    public BooksDbImpl() {
        this.books = new ArrayList<>(Arrays.asList(DATA));
    }
    @Override
    public boolean connect(String database) throws BooksDbException {
        try {
            connection = DriverManager.getConnection(database, user, pwd);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM MyLibrary.Books");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("Title"));
            }
            return true;

        } catch (Exception e){
            throw new BooksDbException();
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        // mock implementation
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new BooksDbException();
        }

    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        String sql = "SELECT * FROM Books WHERE Title LIKE '%" + searchTitle + "%';";
        return searchFor(sql);
    }

    private String getAuthorStringForBook(String bookISBN) throws BooksDbException{
        String authors = "";
        String sql = "SELECT a.AuthorFirstName, a.AuthorLastName " +
                "FROM Author a " +
                "JOIN Book_Author ba ON a.AuthorID = ba.AuthorID " +
                "WHERE ba.BookISBN = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bookISBN);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(rs.getString("AuthorFirstName"))
                        .append(" ")
                        .append(rs.getString("AuthorLastName"));
            }
            authors = sb.toString();
        } catch(Exception e){
            throw new BooksDbException();
        }
        return authors;
    }
    public String getGenreForBook(int genreId) throws BooksDbException {
        String sql = "SELECT GenreName FROM Genre WHERE GenreID = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, genreId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("GenreName");
            } else {
                return null; // or throw an exception if the genre is not found
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving genre", e);
        }
    }

    public List<Book> searchBooksByISBN(String searchISBN) throws BooksDbException {
        String sql = "SELECT * FROM Books WHERE ISBN LIKE '%" + searchISBN + "%';";
        return searchFor(sql);
    }

    public List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getGenre().toString().equalsIgnoreCase(searchGenre)) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        String sql = "SELECT \n" +
                "    b.ISBN, \n" +
                "    b.Title, \n" +
                "    b.Published,\n" +
                "    g.GenreID\n" +
                "FROM \n" +
                "    Books b\n" +
                "JOIN \n" +
                "    Book_Author ba ON b.ISBN = ba.BookISBN\n" +
                "JOIN \n" +
                "    Author a ON ba.AuthorID = a.AuthorID\n" +
                "JOIN\n" +
                "    Genre g ON b.GenreID = g.GenreID\n" +
                "WHERE \n" +
                "    a.AuthorFirstName LIKE '%" + searchAuthor + "%' OR a.AuthorLastName LIKE '%" + searchAuthor + "%';";
        return searchFor(sql);
    }

    @Override
    public List<Book> searchBooksByGrade(String searchGrade) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        int numberToSearchFor = Integer.parseInt(searchGrade);
        for (Book book : books) {
            if (numberToSearchFor == book.getGrade()) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public void addBook(Book newBook) throws BooksDbException {
        this.books.add(newBook);

        /*
        String query = "INSERT INTO Books (ISBN, Title, GenreID)\n" +
                "VALUES ('" + newBook.getIsbn() + "', '" + newBook.getTitle() + "', 2);";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e){
            throw new BooksDbException("Error adding book", e);
        }

         */
    }

    @Override
    public void deleteBook(Book book) throws BooksDbException {
        this.books.remove(book);
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {

    }

    @Override
    public List<Book> getAllBooks() throws BooksDbException {

        return this.books;
    }

    private List<Book> searchFor(String sql) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String isbn = resultSet.getString("ISBN");
                System.out.println(isbn);
                String title = resultSet.getString("Title");
                System.out.println(title);
                String authors = getAuthorStringForBook(isbn);
                System.out.println(authors);
                Date published = resultSet.getDate("Published");
                System.out.println(published);
                System.out.println("genre done");
                String genreName = getGenreForBook(resultSet.getInt("GenreID"));
                Genre genre = Genre.valueOf(genreName); // Convert to enum
                result.add(new Book(0,isbn,title,authors,published,genre));
            }
        } catch (Exception e){
            throw new BooksDbException("searchBooksByTitle Wrong");
        }
        return result;
    }
    private static Book[] DATA = new Book[]{
            new Book(1, "123456789", "Databases Illuminated","Catherine M. Ricardo", new Date(0, 1, 1), Genre.Crime)
            /*
            new Book(1, "123456789", "Databases Illuminated","Catherine M. Ricardo", new Date(0, 1, 1), Genre.Crime),
            new Book(2, "234567891", "Databases Illuminated", "Din Mamma",new Date(0, 1, 1), Genre.Crime),
            new Book(3, "456789012", "The buried giant", "Kazuo Ishiguro",new Date(0, 1, 1), Genre.Crime),
            new Book(4, "567890123", "Never let me go", "Kazuo Ishiguro",new Date(0, 1, 1), Genre.Crime),
            new Book(5, "678901234", "The remains of the day", "Kazuo Ishiguro",new Date(0, 1, 1), Genre.Drama),
            new Book(6, "234567890", "Alias Grace", "Margaret Atwood",new Date(0, 1, 1), Genre.Crime),
            new Book(7, "345678911", "The handmaids tale", "Margaret Atwood",new Date(0, 1, 1), Genre.Memoir),
            new Book(8, "345678901", "Shuggie Bain", "Douglas Stuart",new Date(0, 1, 1), Genre.Mystery),
            new Book(9, "345678912", "Microserfs", "Douglas Coupland",new Date(0, 1, 1), Genre.Romance),
   */
    };



}