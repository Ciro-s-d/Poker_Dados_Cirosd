module com.example.poker_dados_cirosd {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.poker_dados_cirosd to javafx.fxml;
    exports com.example.poker_dados_cirosd;
    exports com.example.poker_dados_cirosd.controlador;
    opens com.example.poker_dados_cirosd.controlador to javafx.fxml;

    opens com.example.poker_dados_cirosd.modelo to org.junit.platform.commons, org.junit.jupiter.api;
}