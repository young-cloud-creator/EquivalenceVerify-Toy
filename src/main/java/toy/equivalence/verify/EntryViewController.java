package toy.equivalence.verify;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class EntryViewController {

    @FXML
    private Button selectPath;

    @FXML
    private Button doVerify;

    private File targetDir;

    @FXML
    protected void onSelectPathClicked() {
        final String dirChooserTitle = "选择文件夹";

        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle(dirChooserTitle);
        this.targetDir = dirChooser.showDialog(selectPath.getScene().getWindow());
    }

    @FXML
    protected void onDoVerifyClicked() {

    }
}