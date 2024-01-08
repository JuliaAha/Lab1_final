package se.kth.julialof.lab1_final.booksdb.View;

import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import se.kth.julialof.lab1_final.booksdb.Model.*;
import java.util.Arrays;
import static javafx.scene.control.Alert.AlertType.WARNING;


/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view
    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;
    private MenuBar menuBar;
    private ComboBox<Genre> genreComboBox;



    public BooksPane(BooksDbImpl booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    public void clearBooksOnPane(){
        booksInTable.clear();
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg  the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus(controller);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        //TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Integer> gradeCol = new TableColumn<>("Grade");
        TableColumn<Book, String> authorCol = new TableColumn<>("Authors");
        booksTable.getColumns().addAll(titleCol, isbnCol, authorCol, gradeCol, genreCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell,
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
       // publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        authorCol.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            return new SimpleStringProperty(book.getAuthorNames());
        });

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }


    private void initMenus(Controller controller) {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        MenuItem genreItem = new MenuItem("Genre");
        MenuItem searchGradeItem = new MenuItem("Grade");
        searchMenu.getItems().addAll(authorItem, titleItem, isbnItem, genreItem, searchGradeItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);

        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        connectItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.handleConnectToDB();
            }
        });

        disconnectItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.handleDisconnectFromDb();
            }
        });

        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                AddBookDialog addBookDialog = new AddBookDialog();
                Optional<Book> result = addBookDialog.showAndWait();
                result.ifPresent(book -> {
                    controller.handleAddNewBook(book);
                });
            }
        });
        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                /*
                RemoveBookDialog removeBookDialog = new RemoveBookDialog();
                Optional<Book> result = removeBookDialog.showAndWait();
                result.ifPresent(book -> {
                    controller.handleRemoveBook(book);
                });
                 */
                Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null){
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirm Removal");
                    confirmationAlert.setHeaderText("Are you sure you want to remove: '" + selectedBook.getTitle() + "' ?" );
                    confirmationAlert.setContentText("This action cannot be undone.");

                    if (confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).isPresent()){
                        controller.handleRemoveBook(selectedBook);
                    }
                }

            }
        });

        titleItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Search by Title");
                dialog.setHeaderText("Book Search");
                dialog.setContentText("Please enter a title:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(title -> {
                    SearchMode mode = SearchMode.Title;
                    controller.onSearchSelected(title, mode);
                });
            }
        });
        isbnItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Search by ISBN");
                dialog.setHeaderText("Book Search");
                dialog.setContentText("Please enter ISBN:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(isbn -> {
                    SearchMode mode = SearchMode.ISBN;
                    controller.onSearchSelected(isbn, mode);
                });
            }
        });
        authorItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Search by Authors");
                dialog.setHeaderText("Book Search");
                dialog.setContentText("Please enter Authors:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(author -> {
                    SearchMode mode = SearchMode.Author;
                    controller.onSearchSelected(author, mode);
                });
            }
        });
        genreItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Genre> genres = Arrays.asList(Genre.values());
                ChoiceDialog<Genre> dialog = new ChoiceDialog<>(genres.get(0), genres);
                dialog.setTitle("Search by Genre");
                dialog.setHeaderText("Book Search");
                dialog.setContentText("Choose a genre:");

                Optional<Genre> result = dialog.showAndWait();
                result.ifPresent(genre -> {
                    SearchMode mode = SearchMode.Genre;
                    controller.onSearchSelected(genre.toString(), mode);
                });
            }
        });

        searchGradeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Search by Grade");
                dialog.setHeaderText("Book Search");
                dialog.setContentText("Please enter Grade:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(grade -> {
                    try {
                        int gradeNumber = Integer.parseInt(grade);
                        controller.onSearchSelected(String.valueOf(gradeNumber), SearchMode.Grade);
                    } catch (NumberFormatException e) {
                        showAlertAndWait("Invalid grade format", Alert.AlertType.ERROR);
                    }
                });
            }
        });
        MenuItem gradeItem = new MenuItem("Grade");
        manageMenu.getItems().add(gradeItem);

        gradeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    controller.handleGradeBook(selectedBook);
                } else {
                    showAlertAndWait("No book selected", WARNING);
                }
            }
        });
    }
}