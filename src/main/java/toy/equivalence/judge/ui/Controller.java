package toy.equivalence.judge.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import toy.equivalence.JudgeResult;
import toy.equivalence.judge.dataStructure.UFS;
import toy.equivalence.judge.judgeTool.JudgeEquivalence;
import toy.equivalence.verify.EntryViewController;

public class Controller {

    private final EntryViewController ui;
    private File[] subDirs;
    private File dir;

    public Controller(EntryViewController ui) {
        this.ui = ui;
    }

    public void setDir(File dir) throws IOException {
        if (dir == null || !dir.isDirectory()) {
            throw new IOException("target path is not a directory!");
        }
        this.dir = dir;
        this.subDirs = dir.listFiles(File::isDirectory);
    }

    public void doJudge() {
        ExecutorService pool = Executors.newCachedThreadPool();
        ArrayList<JudgeResult> results = new ArrayList<>();
        for(File subDir : subDirs) {
            pool.execute(()->doThread(subDir, results));
        }
        pool.shutdown();
        try {
            if(pool.awaitTermination(8, TimeUnit.HOURS)) {
                ui.onJudgeComplete(results, dir);
            }
            else {
                ui.onJudgeTimeout();
            }
        }
        catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void doThread(File subDir, List<JudgeResult> results) {
        try {
            JudgeEquivalence judge = new JudgeEquivalence(subDir);
            judge.judge();
            UFS resultUFS = judge.getJudgeResult();
            File[] resultFiles = judge.getFiles();
            synchronized (this) {
                results.add(new JudgeResult(resultUFS, resultFiles, subDir));
            }
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
