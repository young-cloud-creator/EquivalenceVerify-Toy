package toy.equivalence.verify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import toy.equivalence.JudgeResult;
import toy.equivalence.judge.ui.Controller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EntryViewController {

    @FXML
    private Button selectPath;

    @FXML
    private Button doVerify;
    @FXML
    private TextField targetPath;

    private File targetDir;

    @FXML
    protected void onSelectPathClicked() {
        final String dirChooserTitle = "选择文件夹";
        final String errorTitle = "路径设置失败";
        final String errorTips = "路径设置失败，请重试";

        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle(dirChooserTitle);
        var selectedDir = dirChooser.showDialog(selectPath.getScene().getWindow());
        if (selectedDir != null) {
            try {
                this.targetPath.setText(selectedDir.getCanonicalPath());
                this.targetDir = selectedDir;
            } catch (IOException e) {
                showAlert(errorTitle, errorTips, e.getMessage());
            }
        }
    }

    @FXML
    protected void onDoVerifyClicked() {
        var processViewFxmlLoader = new FXMLLoader(getClass().getResource("process-view.fxml"));
        final String errorTitle = "出现了一些问题";
        final String errorTips = "出现了一些问题，请重试";

        if (doVerify.getScene().getWindow() instanceof Stage stage) {
            Controller controller = new Controller(this);
            try {
                controller.setDir(this.targetDir);
                stage.setScene(new Scene(processViewFxmlLoader.load()));
                new Thread(controller::doJudge).start();
            }
            catch (IOException e) {
                showAlert(errorTitle, errorTips, e.getMessage());
            }
        }
        else {
            showAlert(errorTitle, errorTips, "doVerify.getScene().getWindow() is not an instance of Stage");
        }
    }

    public void onJudgeComplete(ArrayList<JudgeResult> results) {

    }

    public void onJudgeTimeout() {
        showAlert("运行时间过长", "机器等价判断时间过长，可能出现了问题，已停止等价判断", ":(");
    }

    private void showAlert(String errorTitle, String errorTips, String errorContent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorTitle);
        alert.setHeaderText(errorTips);
        alert.setContentText(errorContent);
        alert.show();
    }
}