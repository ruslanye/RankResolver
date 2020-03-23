module com.github.ruslanye {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencsv;

    opens com.github.ruslanye.RankReslover to javafx.fxml;
    exports com.github.ruslanye.RankReslover;
}