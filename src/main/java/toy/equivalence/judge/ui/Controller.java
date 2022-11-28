package toy.equivalence.judge.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import toy.equivalence.judge.dataStructure.UFS;
import toy.equivalence.judge.judgeTool.JudgeEquivalence;

public class Controller {

    private final MainUI ui;
    private File dir;
    private File[] subDirs;
    private OutputStreamWriter generalEqualWriter;
    private OutputStreamWriter generalInequalWriter;

    Controller(MainUI ui) {
        this.ui = ui;
    }

    void setDir(String dir) throws IOException {
        this.dir = new File(dir);
        if (!this.dir.isDirectory()) {
            throw new IOException("path '"+dir+"' is not a directory!");
        }

        this.subDirs = this.dir.listFiles((File f)->f.isDirectory());
    }

    void doJudge() throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();
        File outputDir = new File(dir.getCanonicalPath()+"/output");
        File outputEqual = new File(outputDir.getCanonicalPath()+"/equal.csv");
        File outputInequal = new File(outputDir.getCanonicalPath()+"/inequal.csv");
        outputDir.mkdir();
        outputEqual.createNewFile();
        outputInequal.createNewFile();
        FileOutputStream generalEqualFos = new FileOutputStream(outputEqual);
        FileOutputStream generalInequalFos = new FileOutputStream(outputInequal);
        this.generalEqualWriter = new OutputStreamWriter(generalEqualFos);
        this.generalInequalWriter = new OutputStreamWriter(generalInequalFos);
        this.generalEqualWriter.write("file1,file2\n");
        this.generalInequalWriter.write("file1,file2\n");
        for(File subDir : subDirs) {
            pool.execute(()->doThread(subDir));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(8, TimeUnit.HOURS);
        }
        catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        this.generalEqualWriter.close();
        this.generalInequalWriter.close();
    }

    private void doThread(File subDir) {
        try {
            JudgeEquivalence judge = new JudgeEquivalence(subDir);
            judge.judge();
            UFS resultUFS = judge.getJudgeResult();
            File[] resultFiles = judge.getFiles();
            processResult(resultUFS, resultFiles, subDir);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    } 

    private void processResult(UFS equivalence, File[] files, File subDir) throws IOException {
        // process the UFS and write file pairs to .csv files
        // convert UFS to ArrayList so that it can be output by MainUI
        output2csv(equivalence, files, subDir);
        output2ui(equivalence, files, subDir);
    }

    private void output2csv(UFS equivalence, File[] files, File subDir) throws IOException {
        //File outputDir = new File(subDir.getCanonicalPath()+"/output/");
        //File equalCSV = new File(outputDir.getCanonicalPath()+"/"+"equal.csv");
        //File inequalCSV = new File(outputDir.getCanonicalPath()+"/"+"inequal.csv");
        //outputDir.mkdir();
        //equalCSV.createNewFile();
        //inequalCSV.createNewFile();
        //FileOutputStream equalFos = new FileOutputStream(equalCSV);
        //FileOutputStream inequalFos = new FileOutputStream(inequalCSV);
        //OutputStreamWriter equalWriter = new OutputStreamWriter(equalFos);
        //OutputStreamWriter inequalWriter = new OutputStreamWriter(inequalFos);

        for(int i=0; i<files.length; i++) {
            for(int j=i+1; j<files.length; j++) {
                if(equivalence.isSameRoot(i, j)) {
                    //equalWriter.write(files[i].getCanonicalPath()+","+files[j].getCanonicalPath()+"\n");
                    this.generalEqualWriter.write(files[i].getCanonicalPath()+","+files[j].getCanonicalPath()+"\n");
                }
                else {
                    //inequalWriter.write(files[i].getCanonicalPath()+","+files[j].getCanonicalPath()+"\n");
                    this.generalInequalWriter.write(files[i].getCanonicalPath()+","+files[j].getCanonicalPath()+"\n");
                }
            }
        }

        //equalWriter.close();
        //inequalWriter.close();
    }

    private void output2ui(UFS equivalence, File[] files, File subDir) throws IOException {
        ArrayList<ArrayList<String> > result = new ArrayList<>();
        boolean[] visited = new boolean[files.length];
        for(int i=0; i<visited.length; i++) {
            visited[i] = false;
        }
        for(int i=0; i<files.length; i++) {
            if(!visited[i]) {
                visited[i] = true;
                ArrayList<String> temp = new ArrayList<>();
                temp.add(files[i].getName());
                for(int j=i+1; j<files.length; j++) {
                    if(!visited[j] && equivalence.isSameRoot(i, j)) {
                        visited[j] = true;
                        temp.add(files[j].getName());
                    }
                }
                result.add(temp);
            }
        }

        synchronized(this) {
            ui.outputResults(subDir.getCanonicalPath(), result);
        }
    }
}
