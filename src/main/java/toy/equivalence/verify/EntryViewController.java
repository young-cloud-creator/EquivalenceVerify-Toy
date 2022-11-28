package toy.equivalence.verify;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import java.util.List;

public class EntryViewController {

    @FXML
    private Button selectPath;

    @FXML
    private Button doVerify;
    @FXML
    private TextField targetPath;

    private File targetDir;
    private Scene processScene;

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
        final String errorTitle = "出现了一些问题";
        final String errorTips = "出现了一些问题，请重试";

        if (doVerify.getScene().getWindow() instanceof Stage stage) {
            Controller controller = new Controller(this);
            try {
                controller.setDir(this.targetDir);
                stage.setScene(getProcessScene());
                new Thread(controller::doJudge).start();
            }
            catch (IOException e) {
                showAlert(errorTitle, errorTips, e.getMessage());
            }
        }
        else {
            showAlert(errorTitle, errorTips, "Window is not an instance of Stage");
        }
    }

    public void onJudgeComplete(List<JudgeResult> results, File dir) {
        Platform.runLater(() -> {
            if (getProcessScene().getWindow() instanceof Stage stage) {
                try {
                    var verifyViewLoader = new FXMLLoader(getClass().getResource("verify-view.fxml"));
                    Parent parent = verifyViewLoader.load();
                    VerifyViewController verifyViewController = verifyViewLoader.getController();
                    verifyViewController.initController(results, dir);
                    stage.setScene(new Scene(parent));
                }
                catch (IOException e) {
                    showAlert(":(", "看上去有些不妙，重新打开软件可能会解决问题", e.getMessage());
                }
            }
            else {
                showAlert(":(", "出现了一些问题", "Window is not an instance of Stage");
            }
        });
    }

    public void onJudgeTimeout() {
        Platform.runLater(() -> {
            if (getProcessScene().getWindow() instanceof Stage stage) {
                try {
                    var entryViewFxmlLoader = new FXMLLoader(getClass().getResource("entry-view.fxml"));
                    stage.setScene(new Scene(entryViewFxmlLoader.load()));
                } catch (IOException ignored) {}
            }
            else {
                showAlert(":(", "出现了一些问题", "Window is not an instance of Stage");
            }
            showAlert("运行时间过长", "机器等价判断时间过长，可能出现了问题，已停止等价判断", ":(");
        });
    }

    private void showAlert(String errorTitle, String errorTips, String errorContent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(errorTitle);
        alert.setHeaderText(errorTips);
        alert.setContentText(errorContent);
        alert.show();
    }

    private Scene getProcessScene() {
        if (this.processScene == null) {
            var processViewFxmlLoader = new FXMLLoader(getClass().getResource("process-view.fxml"));
            try {
                this.processScene = new Scene(processViewFxmlLoader.load());
            }
            catch (IOException ignored) {}
        }
        return this.processScene;
    }
}