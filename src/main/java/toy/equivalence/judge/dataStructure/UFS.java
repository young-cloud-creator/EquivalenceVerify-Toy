package toy.equivalence.judge.dataStructure;

public class UFS {
    private int[] ufs;

    public UFS(int size) {
        ufs = new int[size];
        for(int i=0; i<ufs.length; i++) {
            ufs[i] = i;
        }
    }

    public int findRoot(int i) {
        if(ufs[i] != i) {
            ufs[i] = findRoot(ufs[i]);
        }
        return ufs[i];
    }

    public void unionRoot(int i, int j) {
        ufs[findRoot(i)] = findRoot(j);
    }

    public boolean isSameRoot(int i, int j) {
        return findRoot(i) == findRoot(j);
    }
}
