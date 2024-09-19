/**
 * Author: Kian Aliwalas
 * Project 1 DFA Construction
 * Fall 2024 Theory of Computation
 * Instructor: Dylan Strickley
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;

public class Project1 {
    private static final int DIST_SCALE_HOR = 200;
    private static final int DIST_SCALE_VERT = 60;


    public static void main(String[] args) throws IOException, InterruptedException {
        //Variables
        ArrayList<States> statesList = new ArrayList<>();
        ArrayList<Transition> transitionsList = new ArrayList<>();
        ArrayList<String> wordList = new ArrayList<>();

        //Input/Output
        Scanner scanner = new Scanner(new File("example.txt"));
        PrintWriter printWriter = new PrintWriter(new FileWriter("output.jff"));

        //Puts every line in an ArrayList
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            wordList.add(line);

        }
        //Sorts wordList
        Collections.sort(wordList);

        for(String word : wordList) {
            System.out.println(word);
        }

        //Starter for .jff file
        head(printWriter);

        int currentHeight = 0;

        int stateCounter = 1;

        //Goes over each word in wordList
        for (String word : wordList) {
            int origin = 0; //Track working state

            for (int i = 0; i < word.length(); i++) {
                char symbol = word.charAt(i);
                int dest = 0; //Tracks destination state

                //Checks for any non-lower case characters
                //Taken from example
                if(!Character.isLowerCase(symbol)){
                    System.out.printf("Symbol '%c' is invalid. All characters should be from a-z%n", symbol);
                            System.exit(1); // Exit with code 1
                }

                //Checks if a transition from currentState with the input symbol already exists
                for (Transition t : transitionsList) {
                    if ((t.getOrigin() == origin) && (t.getSymbol() == symbol)) {
                        dest = t.getDest() ;
                        break;
                    }
                }

                //If the transition does not exist, create a new state and add a transition
                if (dest == 0) {
                    dest = stateCounter;
                    statesList.add(new States(dest, i * DIST_SCALE_HOR, currentHeight ,printWriter));
                    transitionsList.add(new Transition(origin, dest, symbol,printWriter));
                    stateCounter++;
                }

                origin = dest;  //Moves to the next state
            }

            //Sets if the state is final
            // 0/null = default
            // 1 = initial
            // 2 = final
            for (States s : statesList) {
                if (s.getID() == (origin)) {
                    s.setIsFinal(2);
                    break;
                }
            }
            currentHeight += 75;//Moves height down after each word
        }
        

        //Creates first state and set it to the front
        States p0 = new States(0, -2 * DIST_SCALE_HOR, (currentHeight - DIST_SCALE_VERT) / 3, printWriter);
        p0.setIsFinal(1);
        statesList.addFirst(p0);

        //Starts and waits for all the stored State object/threads
        for (States state : statesList) {
            state.start();
            state.join();
        }

        //Starts and waits for all the stored Transition object/threads
        for (Transition transition : transitionsList) {
            transition.start();
            transition.join();
        }

        //Ends JFLAP file and closes all Input/Output
        tail(printWriter);
        printWriter.close();
        scanner.close();

    }

    /**
     * Starter for .jff file taken from example
     */
    private static void head(PrintWriter printWriter) {
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><structure><type>fa</type><automaton>");
        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><structure><type>fa</type><automaton>");
    }

    /**
     * End for .jff file taken from example
     *
     * @param printWriter output
     */
    private static void tail(PrintWriter printWriter) {
        System.out.println("</automaton></structure>");
        printWriter.println("</automaton></structure>");
    }

}

class States extends Thread {
    private final int id;
    private final int x;
    private final int y;
    private int isFinal;
    private final PrintWriter printWriter;

    public States(int id, int x, int y, PrintWriter printWriter) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.printWriter = printWriter;
        isFinal = 0;
    }

    public int getID(){
        return id;
    }

    public void setIsFinal(int isFinal){
        this.isFinal = isFinal;
    }

    @Override
    public void run() {
        //Prints out to terminal
        System.out.println("<state id=\"" + id + "\" name=\"q" + id + "\">");
        System.out.println("<x>" + x + "</x>");
        System.out.println("<y>" + y + "</y>");
        //Prints out to file
        printWriter.println("<state id=\"" + id + "\" name=\"q" + id + "\">");
        printWriter.println("<x>" + x + "</x>");
        printWriter.println("<y>" + y + "</y>");
        switch (isFinal){
            case (1) :
            System.out.println("<initial/>");
            printWriter.println("<initial/>");
                break;
            case (2) :
            System.out.println("<final/>");
            printWriter.println("<final/>");
                break;
            default:
                break;
        }

        System.out.println("</state>");
        printWriter.println("</state>");
    }

}

class Transition extends Thread {
    private final int origin;
    private final int dest;
    private final char symbol;
    private final PrintWriter printWriter;

    public Transition(int origin, int dest, char symbol, PrintWriter printWriter) {
        this.origin = origin;
        this.dest = dest;
        this.symbol = symbol;
        this.printWriter = printWriter;

    }

    public int getDest() {
        return dest;
    }

    public int getOrigin() {
        return origin;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public void run() {
        System.out.println("<transition>");
        System.out.println("<from>" + origin + "</from>");
        System.out.println("<to>" + dest + "</to>");
        System.out.println("<read>" + symbol + "</read>");
        System.out.println("</transition>");
        printWriter.println("<transition>");
        printWriter.println("<from>" + origin + "</from>");
        printWriter.println("<to>" + dest + "</to>");
        printWriter.println("<read>" + symbol + "</read>");
        printWriter.println("</transition>");
    }
}
