package se.kth.julialof.lab1_final.booksdb.View;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import se.kth.julialof.lab1_final.booksdb.Model.Book;
import se.kth.julialof.lab1_final.booksdb.Model.Genre;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.sql.Date;

/**
 * A simplified example of a form, using JavaFX Dialog and DialogPane. Type
 * parameterized for FooBook.
 *
 * @author Anders Lindström, anderslm@kth.se
 */
public class AddBookDialog extends Dialog<Book> {

    private final TextField titleField = new TextField();
    private final TextField isbnField = new TextField();
    private final TextField authorField = new TextField();
    private final ComboBox<Genre> genreChoice = new ComboBox(FXCollections.observableArrayList(Genre.values()));
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    public AddBookDialog() {
        buildAddBookDialog();
    }

    private void buildAddBookDialog() {

        this.setTitle("Add a new book");
        this.setResizable(false); // really?

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Title "), 1, 1);
        grid.add(titleField, 2, 1);
        grid.add(new Label("Author "), 1, 2);
        grid.add(authorField, 2, 2);
        grid.add(new Label("Isbn "), 1, 3);
        grid.add(isbnField, 2, 3);
        grid.add(new Label("Genre "), 1, 4);
        grid.add(genreChoice, 2, 4);

        this.getDialogPane().setContent(grid);

        final ButtonType buttonTypeOk
                = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel
                = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        // this callback returns the result from our dialog, via
        // Optional<FooBook> result = dialog.showAndWait();
        // FooBook book = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, Book>() {
            @Override
            public Book call(ButtonType b) {
                if (b == buttonTypeOk && isValidData()) {
                    if (isValidData()) {
                        return new Book(
                                0, isbnField.getText(), titleField.getText(), authorField.getText(), new Date(0,0,0), genreChoice.getValue());
                    }
                }

                return null;
            }
        });

        // add an event filter to keep the dialog active if validation fails
        // (yes, this is ugly in FX)
        Button okButton
                = (Button) this.getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isValidData()) {
                    event.consume();
                    showErrorAlert("Form error", "Invalid input");
                }
            }
        });
    }

    // TODO for the student: check each input separately, to give better
    // feedback to the user
    /* private boolean isValidData() {
        if (genreChoice.getValue() == null) {
            //System.out.println(isbnField.getText());
            return false;
        }
        if (!Book.isValidIsbn(isbnField.getText())) {
            //System.out.println(isbnField.getText());
            return false;
        }


        // if(...) - keep on validating user input...
        return true;
    } */
    private boolean isValidData() {
        StringBuilder errors = new StringBuilder();
        if (titleField.getText().trim().isEmpty()) {
            errors.append("Title is required.\n");
        }
        if (authorField.getText().trim().isEmpty()) {
            errors.append("Author is required.\n");
        }
        if (isbnField.getText().trim().isEmpty() || !Book.isValidIsbn(isbnField.getText().trim())) {
            errors.append("A valid ISBN is required.\n");
        }
        if (genreChoice.getValue() == null) {
            errors.append("Genre selection is required.\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Form error", errors.toString());
            return false;
        }
        return true;
    }

    private void clearFormData() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        genreChoice.setValue(null);
    }

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}