module se.kth.julialof.lab1_final.booksdb {

    requires java.sql;

    requires javafx.controls;
    requires javafx.base;

    opens se.kth.julialof.lab1_final.booksdb to javafx.base;
    opens se.kth.julialof.lab1_final.booksdb.Model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports se.kth.julialof.lab1_final.booksdb;
}