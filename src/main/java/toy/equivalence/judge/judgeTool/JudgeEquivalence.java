package toy.equivalence.judge.judgeTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import toy.equivalence.judge.dataStructure.UFS;

public class JudgeEquivalence {

    private final File dir;
    private final File[] files;
    private final File inFormat;
    private final UFS equivalence;

    public JudgeEquivalence(File dir) {
        this.dir = dir;
        this.files = dir.listFiles((File f)-> (getFileExtension(f).equalsIgnoreCase("cpp") 
                                            || getFileExtension(f).equalsIgnoreCase("c")));
        final String inFormatName = "stdin_format.txt";
        File[] inFormatList = this.dir.listFiles((File f)-> (f.getName().equalsIgnoreCase(inFormatName)));

        if(inFormatList.length > 0) {
            this.inFormat = inFormatList[0];
        }
        else {
            this.inFormat = null;
        }

        if(this.files.length > 0) {
            this.equivalence = new UFS(this.files.length);
        } 
        else {
            this.equivalence = null;
        }
    }

    public void judge() throws IOException {
        // judge the equivalence of every file pairs, files are list in this.files
        final int testcasesNum = 10;
        InputGenerator inputGenerator = new InputGenerator(this.inFormat);
        String[] testcases = new String[testcasesNum];
        Execute[] executables = new Execute[files.length];
        File outputDir = new File(this.dir.getPath()+"/temp");
        outputDir.mkdir();
        for(int i=0; i<testcasesNum; i++) {
            testcases[i] = inputGenerator.genInput();
        }
        for(int i=0; i<files.length; i++) {
            executables[i] = new Execute(this.files[i], this.dir, outputDir);
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for(Execute execute : executables) {
            pool.execute(()->{
                execute.compile();
                for(String testcase : testcases) {execute.exec(testcase);}});
        }
        pool.shutdown();
        try {
            pool.awaitTermination(8, TimeUnit.HOURS);
        }
        catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        doJudge(outputDir);
        Runtime.getRuntime().exec("rm -rf "+outputDir.getCanonicalPath());
    }

    public UFS getJudgeResult() {
        return this.equivalence;
    }

    public File[] getFiles() {
        return this.files;
    }

    private void doJudge(File outputDir) throws IOException {
        for(int i=0; i<files.length; i++) {
            for(int j=i+1; j<files.length; j++) {
                if(equivalence.isSameRoot(i, j)) {
                    continue;
                }
                else {
                    File f1 = new File(outputDir.getCanonicalPath()+"/"+
                                files[i].getName().substring(0, files[i].getName().lastIndexOf("."))+".txt");
                    File f2 = new File(outputDir.getCanonicalPath()+"/"+
                                files[j].getName().substring(0, files[j].getName().lastIndexOf("."))+".txt");
                    if(isSameContent(f1, f2)) {
                        equivalence.unionRoot(i, j);
                    }
                }
            }
        }
    }

    private boolean isSameContent(File f1, File f2) {
        try(FileInputStream f1InStream = new FileInputStream(f1);
            FileInputStream f2InStream = new FileInputStream(f2);) {
            int f1Char = 0;
            int f2Char = 0;

            while (true) {
                f1Char = f1InStream.read();
                f2Char = f2InStream.read();
                if (f1Char != -1 && f2Char != -1) {
                    if (f1Char != f2Char) {
                        return false;
                    }
                    else {
                        continue;
                    }
               } 
                else {
                    break;
                }
            }
        }
        catch(Exception e) {
            return false;
        } 

        return true;
    }
    
    private String getFileExtension(File f) {
        String extension = new String();
        int idx = f.getName().lastIndexOf(".");
        if(idx != -1) {
            extension = f.getName().substring(idx+1);
        }
        return extension;
    }
}
