module toy.equivalence.verify {
    requires javafx.controls;
    requires javafx.fxml;

    opens toy.equivalence.verify to javafx.fxml;
    exports toy.equivalence.verify;
    exports toy.equivalence.judge.ui;
    exports toy.equivalence.judge.dataStructure;
    exports toy.equivalence;
}