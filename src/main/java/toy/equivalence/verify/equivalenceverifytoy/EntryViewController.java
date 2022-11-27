package toy.equivalence.verify.equivalenceverifytoy;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class EntryViewController {

    @FXML
    private Button selectPath;

    private File targetDir;

    @FXML
    protected void onSelectPathClicked() {
        final String dirChooserTitle = "选择文件夹";

        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle(dirChooserTitle);
        this.targetDir = dirChooser.showDialog(selectPath.getScene().getWindow());
    }
}