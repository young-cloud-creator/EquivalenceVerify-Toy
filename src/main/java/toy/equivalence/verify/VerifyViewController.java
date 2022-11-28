package toy.equivalence.verify;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import toy.equivalence.JudgeResult;
import toy.equivalence.judge.dataStructure.UFS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VerifyViewController {

    @FXML
    private Label leftPath;
    @FXML
    private Label rightPath;
    @FXML
    private TextArea leftText;
    @FXML
    private TextArea rightText;

    private LinkedList<JudgeResult> results;
    private JudgeResult curVerifyCollection = null;
    private int file1Idx = 0;
    private int file2Idx = 1;
    private UFS curEquivalence;
    private boolean isFirstVerify = true;
    private OutputStreamWriter generalEqualWriter;
    private OutputStreamWriter generalUnequalWriter;
    private OutputStreamWriter generalUnknownWriter;
    private List<List<Integer> > unknownList;

    public void initController(List<JudgeResult> results, File dir) throws IOException {
        this.results = new LinkedList<>(results);

        File outputDir = new File(dir.getCanonicalPath()+"/output");
        File outputEqual = new File(outputDir.getCanonicalPath()+"/equal.csv");
        File outputUnequal = new File(outputDir.getCanonicalPath()+"/unequal.csv");
        File outputUnknown = new File(outputDir.getCanonicalPath()+"/unknown.csv");
        outputDir.mkdir();
        outputEqual.createNewFile();
        outputUnequal.createNewFile();
        outputUnknown.createNewFile();
        FileOutputStream generalEqualFos = new FileOutputStream(outputEqual);
        FileOutputStream generalUnequalFos = new FileOutputStream(outputUnequal);
        FileOutputStream generalUnknownFos = new FileOutputStream(outputUnknown);
        this.generalEqualWriter = new OutputStreamWriter(generalEqualFos);
        this.generalUnequalWriter = new OutputStreamWriter(generalUnequalFos);
        this.generalUnknownWriter = new OutputStreamWriter(generalUnknownFos);
        this.generalEqualWriter.write("file1,file2\n");
        this.generalUnequalWriter.write("file1,file2\n");
        this.generalUnknownWriter.write("file1,file2\n");

        showNextItem();
    }

    @FXML
    protected void onEqualClicked() {
        curEquivalence.unionRoot(file1Idx, file2Idx);
        showNextItem();
    }

    @FXML
    protected void onUnequalClicked() {
        showNextItem();
    }

    @FXML
    protected void onUnknownClicked() {
        unknownList.get(file1Idx).add(file2Idx-1);
        showNextItem();
    }

    private void showNextItem() {
        if (curVerifyCollection == null) {
            if(isFirstVerify) {
                isFirstVerify = false;
            }
            else {
                try {
                    output2csv();
                }
                catch (IOException e) {
                    showAlert("输出结果到.csv文件时发生错误", e.getMessage());
                }
            }

            if (results.isEmpty()) {
                verifyComplete();
            }
            else {
                curVerifyCollection = results.poll();
                int fileNum = curVerifyCollection.getFiles().length;
                curEquivalence = new UFS(fileNum);
                unknownList = new ArrayList<>();
                for (int i=0; i<fileNum; i++) {
                    unknownList.add(new ArrayList<>());
                }
                showNextItem();
            }
        }
        else {
            if (file2Idx < curVerifyCollection.getFiles().length) {
                if (curVerifyCollection.getEquivalence().isSameRoot(file1Idx, file2Idx) && !curEquivalence.isSameRoot(file1Idx, file2Idx)) {
                    showCode(file1Idx, file2Idx);
                    file2Idx += 1;
                }
                else {
                    file2Idx += 1;
                    showNextItem();
                }
            }
            else if (file1Idx+1 < curVerifyCollection.getFiles().length) {
                file1Idx += 1;
                file2Idx = file1Idx+1;
                showNextItem();
            }
            else {
                curVerifyCollection = null;
                showNextItem();
            }
        }
    }

    private void showCode(int f1Idx, int f2Idx) {
        File file1 = curVerifyCollection.getFiles()[f1Idx];
        File file2 = curVerifyCollection.getFiles()[f2Idx];
        try {
            leftPath.setText(file1.getCanonicalPath());
            rightPath.setText(file2.getCanonicalPath());
            String fileContent1 = Files.readString(Paths.get(file1.getCanonicalPath()));
            String fileContent2 = Files.readString(Paths.get(file2.getCanonicalPath()));
            leftText.setText(fileContent1);
            rightText.setText(fileContent2);
        }
        catch (IOException e) {
            showAlert("打开程序源文件时发生错误，已跳过文件", e.getMessage());
        }
    }

    private void verifyComplete() {
        if (leftText.getScene().getWindow() instanceof Stage stage) {

        }
        else {
            showAlert( "出现了一些问题", "Window is not an instance of Stage");
        }
    }

    private void output2csv() throws IOException {
        int fileNum = curVerifyCollection.getFiles().length;
        for (int i=0; i<fileNum; i++) {
            for (int j=i+1; j<fileNum; j++) {
                if (curEquivalence.isSameRoot(i, j)) {
                    this.generalEqualWriter.write(curVerifyCollection.getFiles()[i].getCanonicalPath()
                            +","+curVerifyCollection.getFiles()[j].getCanonicalPath()+"\n");
                }
                else if (unknownList.get(i).contains(j)) {
                    this.generalUnknownWriter.write(curVerifyCollection.getFiles()[i].getCanonicalPath()
                            +","+curVerifyCollection.getFiles()[j].getCanonicalPath()+"\n");
                }
                else {
                    this.generalUnequalWriter.write(curVerifyCollection.getFiles()[i].getCanonicalPath()
                            +","+curVerifyCollection.getFiles()[j].getCanonicalPath()+"\n");
                }
            }
        }
    }

    private void showAlert(String errorDescription, String errorContent) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("错误");
        alert.setHeaderText(errorDescription);
        alert.setContentText(errorContent);
        alert.show();
    }
}
