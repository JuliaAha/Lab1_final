package se.kth.julialof.lab1_final.booksdb.View;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import se.kth.julialof.lab1_final.booksdb.Model.Book;
import se.kth.julialof.lab1_final.booksdb.Model.Grade;

import java.util.Optional;

public class GradeBookDialog extends Dialog<Grade> {
    private final ComboBox<Integer> gradeComboBox = new ComboBox<>();

    public GradeBookDialog(Book book) {
        setTitle("Grade Book");
        setHeaderText("Set grade for: " + book.getTitle());

        // Populate ComboBox with grades 1 through 5
        for (int i = 1; i <= 5; i++) {
            gradeComboBox.getItems().add(i);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Select Grade:"), 0, 0);
        grid.add(gradeComboBox, 1, 0);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && gradeComboBox.getValue() != null) {
                return new Grade(gradeComboBox.getValue());
            }
            return null;
        });
    }

    public static Optional<Grade> showGradeDialog(Book book) {
        GradeBookDialog dialog = new GradeBookDialog(book);
        return dialog.showAndWait(); // Show the dialog and return the selected grade
    }
}
