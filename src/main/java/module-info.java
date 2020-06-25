module com.github.ruslanye {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencsv;

    opens com.github.ruslanye.RankResolver.Controller to javafx.fxml;
    exports com.github.ruslanye.RankResolver;
}