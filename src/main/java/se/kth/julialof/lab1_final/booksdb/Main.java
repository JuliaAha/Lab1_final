package se.kth.julialof.lab1_final.booksdb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import se.kth.julialof.lab1_final.booksdb.Model.BooksDbException;
import se.kth.julialof.lab1_final.booksdb.Model.BooksDbImpl;
import se.kth.julialof.lab1_final.booksdb.View.BooksPane;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage)  {

        BooksDbImpl booksDb = new BooksDbImpl(); // model
        // Don't forget to connect to the db, somewhere...

        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception ignored) {}
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}