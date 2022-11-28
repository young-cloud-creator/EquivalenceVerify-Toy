package toy.equivalence;

import toy.equivalence.judge.dataStructure.UFS;

import java.io.File;

public class JudgeResult {
    private final UFS equivalence;
    private final File[] files;
    private final File subDir;

    public JudgeResult(UFS equivalence, File[] files, File subDir) {
        this.equivalence = equivalence;
        this.files = files;
        this.subDir = subDir;
    }

    public UFS getEquivalence() {
        return equivalence;
    }

    public File[] getFiles() {
        return files;
    }

    public File getSubDir() {
        return subDir;
    }
}
