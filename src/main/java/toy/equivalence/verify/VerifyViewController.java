package toy.equivalence.verify;

import com.github.difflib.text.DiffRowGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;
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
    private WebView leftView;
    @FXML
    private WebView rightView;

    private LinkedList<JudgeResult> results;
    private JudgeResult curVerifyCollection = null;
    private int file1Idx = 0;
    private int file2Idx = 1;
    private UFS curEquivalence;
    private OutputStreamWriter generalEqualWriter;
    private OutputStreamWriter generalUnequalWriter;
    private OutputStreamWriter generalUnknownWriter;
    private List<List<Integer> > unknownList;
    private File dir;

    public void initController(List<JudgeResult> results, File dir) throws IOException {
        this.results = new LinkedList<>(results);
        this.dir = dir;

        File outputDir = new File(this.dir.getCanonicalPath()+"/output");
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
        curEquivalence.unionRoot(file1Idx, file2Idx-1);
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
            curVerifyCollection = results.poll();
            file1Idx = 0;
            file2Idx = 1;
            if (curVerifyCollection == null) {
                verifyComplete();
            }
            else {
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
                try {
                    output2csv();
                }
                catch (IOException e) {
                    showAlert("输出结果到.csv文件时发生错误", e.getMessage());
                }
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
            var processedString = processContent(fileContent1, fileContent2);
            leftView.getEngine().loadContent(processedString.getKey());
            rightView.getEngine().loadContent(processedString.getValue());
        }
        catch (IOException e) {
            showAlert("打开程序源文件时发生错误，已跳过文件", e.getMessage());
        }
    }

    private Pair<String, String> processContent(String content1, String content2) {
        Pair<String, String> diffResult = diffString(content1.lines().toList(), content2.lines().toList());
        content1 = diffResult.getKey();
        content2 = diffResult.getValue();
        content1 = "<html><head><style type=\"text/css\">" +
                "p{" +
                    "margin: 0px;" + "font-size: large;" +
                "}" +
                "</style></head>" +
                "<body>"+content1+"</body></html>";

        content2 = "<html><head><style type=\"text/css\">" +
                "p{" +
                "margin: 0px;" + "font-size: large;" +
                "}" +
                "</style></head>" +
                "<body>"+content2+"</body></html>";

        return new Pair<>(content1, content2);
    }

    private Pair<String, String> diffString(List<String> original, List<String> revised) {
        StringBuilder content1 = new StringBuilder();
        StringBuilder content2 = new StringBuilder();
        var builder = DiffRowGenerator.create();
        builder.showInlineDiffs(false);
        var generator = builder.build();
        var diffRows = generator.generateDiffRows(original, revised);

        final String DELETION = "<nobr><span style=\"background: rgba(0, 0, 0, 0.3);\">" +
                "<p style=\"background: rgba(0, 0, 0, 0.3);" +
                "${text}" +
                "</nobr>";
        final String INSERTION = "<nobr><span style=\"background: rgba(0, 128, 0, 0.3);\">" +
                "<p style=\"background: rgba(0, 128, 0, 0.3);" +
                "${text}" + "</nobr>";
        final String CHANGE = "<nobr><span style=\"background: rgba(0, 0, 128, 0.3);\">" +
                "<p style=\"background: rgba(0, 0, 128, 0.3);" +
                "${text}" + "</nobr>";
        final String EQUAL = "<nobr>" + "<p style=\"background: rgba(0, 0, 0, 0);" + "${text}</nobr>";

        for (var diffRow : diffRows) {
            switch (diffRow.getTag()) {
                case INSERT -> {
                    content1.append(processHTML("<br>", DELETION));
                    content2.append(processHTML(diffRow.getNewLine(), INSERTION));
                }
                case CHANGE -> {
                    content1.append(processHTML(diffRow.getOldLine(), CHANGE));
                    content2.append(processHTML(diffRow.getNewLine(), CHANGE));
                }
                case DELETE -> {
                    content1.append(processHTML(diffRow.getOldLine(), INSERTION));
                    content2.append(processHTML("<br>", DELETION));
                }
                case EQUAL -> {
                    content1.append(processHTML(diffRow.getOldLine(), EQUAL));
                    content2.append(processHTML(diffRow.getNewLine(), EQUAL));
                }
            }
        }

        return new Pair<>(content1.toString(), content2.toString());
    }

    private String processHTML(String s, String template) {
        if (s.isEmpty()) {
            s = "<br>";
        }

        if (s.startsWith("    ")) {
            s = s.replaceAll(" ", "&nbsp;");
            s = template.replace("${text}", " text-indent:20px;\">"+s);
        }
        else {
            s = s.replaceAll(" ", "&nbsp;");
            s = template.replace("${text}", "\">"+s);
        }
        s += "</p>";
        return s;
    }

    private void verifyComplete() {
        if (leftPath.getScene().getWindow() instanceof Stage stage) {
            try {
                generalUnequalWriter.close();
                generalEqualWriter.close();
                generalUnknownWriter.close();
                var completeViewLoader = new FXMLLoader(getClass().getResource("complete-view.fxml"));
                Parent parent = completeViewLoader.load();
                CompleteViewController completeViewController = completeViewLoader.getController();
                completeViewController.initController(dir);
                stage.setScene(new Scene(parent));
            }
            catch (IOException e) {
                showAlert("看上去有些不妙，重新打开软件可能会解决问题", e.getMessage());
            }
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
