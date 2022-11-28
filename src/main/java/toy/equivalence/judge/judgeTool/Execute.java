package toy.equivalence.judge.judgeTool;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Execute {
    
    private File excutableFile;
    private final File codeFile;
    private final File outputFile;
    private final File outputDir;
    private final File codeDir;
    private final String shell;
    private boolean hadCompiled;

    Execute(File codeFile, File codeDir, File outputDir) throws IOException {
        this.shell = System.getenv("SHELL");
        this.outputFile = new File(outputDir.getCanonicalPath()+"/"+
                codeFile.getName().substring(0, codeFile.getName().lastIndexOf("."))+".txt");
        this.codeFile = codeFile;
        this.outputDir = outputDir;
        this.codeDir = codeDir;
        this.hadCompiled = false;
        this.outputFile.createNewFile();
    }

    void compile() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{this.shell, "-c",
                "g++ "+codeFile.getName()+" -o "+outputDir.getCanonicalPath()+"/"+
                codeFile.getName().substring(0, codeFile.getName().lastIndexOf("."))}, null, codeDir);

            if(p.waitFor() != 0) {
                excutableFile = null;
            }
            else {
                this.excutableFile = new File(outputDir.getPath()+"/"+
                codeFile.getName().substring(0, codeFile.getName().lastIndexOf(".")));
            }
            hadCompiled = true;
        }
        catch(InterruptedException e) {
            System.out.println("Unknown error: "+e);
            System.exit(-1);
        }
        catch(IOException e) {
            System.out.println("IOException: "+e.getMessage());
            System.exit(-1);
        }
    }

    void exec(String testcase) {
        if(!hadCompiled) {
            this.compile();
        }
        try {
            if(excutableFile == null) {
                Runtime.getRuntime().exec(new String[]{this.shell, "-c",
                    "echo \"can't compile\" >> "+outputFile.getCanonicalPath()});
            }
            else {
                Process p = Runtime.getRuntime().exec(new String[]{this.shell, "-c",
                    "echo \""+testcase+"\" | "+excutableFile.getCanonicalPath()+" >> "+outputFile.getCanonicalPath()
                    + " && " + "echo \"\" >> "+outputFile.getCanonicalPath()});
                try {
                    if(p.waitFor(1, TimeUnit.SECONDS)) {
                        if(p.exitValue() != 0) {
                            Runtime.getRuntime().exec(new String[]{this.shell, "-c",
                                "echo \"Nonzero Error\" >> "+outputFile.getCanonicalPath()});
                        }
                    }
                    else {
                        p.destroy();
                        Runtime.getRuntime().exec(new String[]{this.shell, "-c",
                            "echo \"Timeout\" >> "+outputFile.getCanonicalPath()});
                    }
                }
                catch(InterruptedException e) {
                    Runtime.getRuntime().exec(new String[]{this.shell, "-c",
                        "echo \"InterruptedException\" >> "+outputFile.getCanonicalPath()});
                }
            }
        }
        catch(IOException e) {
            System.out.println("IOException: "+e.getMessage());
            System.exit(-1);
        }
    }
}
