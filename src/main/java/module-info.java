module toy.equivalence.verify.equivalenceverifytoy {
    requires javafx.controls;
    requires javafx.fxml;


    opens toy.equivalence.verify.equivalenceverifytoy to javafx.fxml;
    exports toy.equivalence.verify.equivalenceverifytoy;
}