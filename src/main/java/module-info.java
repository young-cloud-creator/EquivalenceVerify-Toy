module toy.equivalence.verify.equivalenceverifytoy {
    requires javafx.controls;
    requires javafx.fxml;


    opens toy.equivalence.verify to javafx.fxml;
    exports toy.equivalence.verify;
}