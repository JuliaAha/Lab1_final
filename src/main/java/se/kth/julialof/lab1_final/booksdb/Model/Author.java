package se.kth.julialof.lab1_final.booksdb.Model;

import java.util.Date;

public class Author {
    private String authorName;
    private int authorID;
    private Date dateOfBirth;

    public Author(String authorName, int authorID, java.sql.Date dateOfBirth) {
        this.authorName = authorName;
        this.authorID = authorID;
        this.dateOfBirth = dateOfBirth;
    }

    public Author() {
    }

    public String getAuthorName() {
        return authorName;
    }
    public int getAuthorID() {
        return authorID;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
}
