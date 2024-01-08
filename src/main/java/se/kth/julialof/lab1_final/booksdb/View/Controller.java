
package se.kth.julialof.lab1_final.booksdb.View;

import se.kth.julialof.lab1_final.booksdb.Model.*;
import java.util.List;
import java.util.Optional;
import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            List<Book> result = null;
            if (mode == SearchMode.Grade) {
                result = booksDb.searchBooksByGrade(searchFor);
            } else {
                if (searchFor == null || searchFor.length() < 2) {
                    booksView.showAlertAndWait("Enter a search string!", WARNING);
                    return;
                }
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        result = booksDb.searchBooksByISBN(searchFor);
                        break;
                    case Author:
                        result = booksDb.searchBooksByAuthor(searchFor);
                        break;
                    case Genre:
                        result = booksDb.searchBooksByGenre(searchFor);
                        break;
                }
            }
            if (result == null || result.isEmpty()) {
                booksView.showAlertAndWait("No results found.", INFORMATION);
                booksView.displayBooks(result);
            } else {
                booksView.displayBooks(result);
            }
        } catch (NumberFormatException e) {
            booksView.showAlertAndWait("Invalid grade format", ERROR);
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.", ERROR);
        }
    }

    public void handleAddNewBook(Book newBook) {
        try {
            booksDb.addBook(newBook);
            booksView.showAlertAndWait("Book added", INFORMATION);
        } catch (Exception e) {
            booksView.showAlertAndWait("Error adding book", ERROR);
        }
    }

    public void handleRemoveBook(Book book) {
        try {
            booksDb.deleteBook(book);
            booksView.showAlertAndWait("Book removed", INFORMATION);
            booksView.clearBooksOnPane();
        } catch (BooksDbException e) {
            booksView.showAlertAndWait("Error removing book", ERROR);
        }
    }
    public void handleGradeBook(Book book) {
        Optional<Grade> result = GradeBookDialog.showGradeDialog(book);
        result.ifPresent(grade -> {
            try {
                book.setGrade(grade.getValue());
                onSearchSelected(book.getTitle(),SearchMode.Title);
                booksDb.updateBook(book);
                booksView.showAlertAndWait("Book graded successfully", INFORMATION);
            } catch (BooksDbException e) {
                booksView.showAlertAndWait("Error grading book", ERROR);
            }
        });
    }

    public void handleConnectToDB(){
        String database = "jdbc:mysql://localhost:3306/MyLibrary";
        try {
            if(booksDb.connect(database)){
                booksView.showAlertAndWait("Successfully connected to Database", INFORMATION);
            }
        } catch (BooksDbException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDisconnectFromDb(){
        try {
            booksDb.disconnect();
            booksView.showAlertAndWait("Successfully disconnected to Database", INFORMATION);
        } catch (BooksDbException e) {
            throw new RuntimeException(e);
        }
    }

}