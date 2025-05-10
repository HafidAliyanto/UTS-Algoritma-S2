module com.uts.algo2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.uts.algo2 to javafx.fxml;
    exports com.uts.algo2;
}
