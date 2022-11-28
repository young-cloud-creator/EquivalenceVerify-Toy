package toy.equivalence.judge.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainUI {

    private final Controller controller;
    private final Scanner in;

    private MainUI() {
        this.controller = new Controller(this);
        this.in = new Scanner(System.in);
    }

    public static void main(String[] args) {
        MainUI mainUI = new MainUI();
        mainUI.clearUI();
        boolean fine = false;

        while(!fine) {
            try {
                mainUI.showUI();
                mainUI.listenInput();
                fine = true;
            } catch (IOException e) {
                mainUI.clearUI();
                System.out.println(e.getMessage());
                fine = false;
            }
        }
    }

    private void showUI() {
        System.out.print("Please type your target path here: ");
    }

    private void clearUI() {
        System.out.print("\033\143");
        System.out.flush();
    }

    private void listenInput() throws IOException {
        String targetPath = in.nextLine();
        System.out.println("Working, please wait patiently...");
        controller.setDir(targetPath);
        controller.doJudge();
        in.close();
    }

    void outputResults(String pathName, ArrayList<ArrayList<String> > equivalence) {
        // pathName stores path to files, equivalence[i] stores files equivalence with each other
        // output the result to stdout
        System.out.println("------result of "+pathName+"------");
        System.out.println();
        for(ArrayList<String> files : equivalence) {
            System.out.println("------");
            for(String f : files) {
                System.out.println(f);
            }
            System.out.println("------");
        }
        System.out.println();
        System.out.println("------------------------------");
    }
}
