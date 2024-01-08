package se.kth.julialof.lab1_final.booksdb.Model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Representation of a book.
 *
 * @author anderslm@kth.se
 */
public class Book{

    private int bookId;
    private String isbn; // should check format
    private String title;
    private Date published;
    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    private ArrayList<Author> authors;

    private Genre genre;
    private Grade grade;
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{10}|\\d{13})$");

    public Book(String isbn) {
        this.isbn = isbn;
    }

    public static boolean isValidIsbn(String isbn) {
        return ISBN_PATTERN.matcher(isbn).matches();
    }

    public Book(int bookId, String isbn, String title, String authorString, Date published, Genre genre) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        //this.authors = parseAuthors(authorString);
        this.authors = new ArrayList<>();

        String[] authorNames = authorString.split(",");
        for (String name : authorNames) {
            Author author = new Author(name.trim(), 0, new Date(1, 1, 1));
            this.addAuthor(author);
        }
        // add authors
        this.grade = new Grade();
        this.genre = genre;

    }
    public Book(String title,String author, String isbn, Genre genre) {
        this(-1, isbn, title, author, new Date(1, 1, 1), genre);
    }
    public void addAuthor(Author author) {
        if (this.authors == null) {
            this.authors = new ArrayList<>();
        }
        authors.add(author);
    }
    /* private ArrayList<Author> parseAuthors(String authorString) {
        ArrayList<Author> authors = new ArrayList<>();
        String[] authorNames = authorString.split(",");
        for (String name : authorNames) {
            authors.add(new Author(name.trim(), 0, new Date(1, 1, 1)));
        }
        return authors;
    } */

    public ArrayList<Author> getAuthors() {
        return authors;
    }
    public String getAuthorNames() {
        StringBuilder authorNames = new StringBuilder();
        for (Author author : authors) {
            if (authorNames.length() > 0) {
                authorNames.append(", ");
            }
            authorNames.append(author.getAuthorName());
        }
        return authorNames.toString();
    }

    public int getGrade() {
        return grade.getValue();
    }

    public void setGrade(int grade) {
        this.grade = new Grade(grade);
    }

    public int getBookId() { return bookId; }
    public String getIsbn() {
        //if(isValidIsbn(isbn))
        //throw new IllegalArgumentException("not a valid isbn");
        return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }



    @Override
    public String toString() {
        String temp = getAuthorNames();
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", authors=" + temp +
                ", genre=" + genre +
                "," + grade +
                '}';
    }

    public Genre getGenre() {
        return genre;
    }
}