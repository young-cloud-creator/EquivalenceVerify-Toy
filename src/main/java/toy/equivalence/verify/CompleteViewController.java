package toy.equivalence.verify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class CompleteViewController {

    @FXML
    private Label infoLabel;

    public void initController(File dir) throws IOException {
        infoLabel.setText("最终结果已经被保存在了 "+dir.getCanonicalPath()+"/output/ 目录下。");
    }

    @FXML
    protected void onBackButtonClicked() {
        if (infoLabel.getScene().getWindow() instanceof Stage stage) {
            try {
                var entryViewLoader = new FXMLLoader(getClass().getResource("entry-view.fxml"));
                stage.setScene(new Scene(entryViewLoader.load()));
            }
            catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("出错了");
                alert.setHeaderText("不知道发生了什么:(，重新打开软件可能会有用");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("出错了");
            alert.setHeaderText("不知道发生了什么:(，重新打开软件可能会有用");
            alert.setContentText("Window is not an instance of Stage");
            alert.show();
        }
    }
}
