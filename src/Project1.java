/** Author: Kian Aliwalas
 *  Project 1 DFA Construction
 *  Fall 2024 Theory of Computation
 *  Instructor: Dylan Strickley
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Project1 {
    private static final int DIST_SCALE_HOR = 200;
    private static final int DIST_SCALE_VERT = 60;
    private static ArrayList<State> statesList = new ArrayList<>();
    private static ArrayList<Transition> transitionsList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(new File("example.txt"));
        PrintWriter printWriter = new PrintWriter(new FileWriter("output.jff"));

        //Starter for .jff file
        head(printWriter);

        int currentID = 1;
        int transitionID = 1;
        int duplicateCharCount = 0;
        int currentHeight = 0;

        //Loops through whilst there is stuff in the scanner
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            char currChar = ' ';
            char prevChar = ' ';

            //Checks each char in scanned line
            for (int i = 0; i < line.length(); i++) {
                // Check if the current character is the end of the string
                boolean isFinal = (i + 1 >= line.length()) || (line.charAt(i + 1) == '\r') || (line.charAt(i + 1) == '\n');
                char symbol = line.charAt(i);

                //Check if character is already present in the list
                for (State state : statesList) {
                    currChar = state.getCurrChar();
                    prevChar = state.getPrevChar();

                    //If the currChar of the object is the same as the one scanned
                    //And it is the first Char of the scanned word
                    //It needs to skip this one and move on but needs to get the ID to transition properly
                    if (symbol == currChar && i == 0) {
                        transitionID = state.getID();
                        duplicateCharCount++;
                        break;
                    }

                    //If the currChar of the object is the same as the one scanned
                    //And the prevChar of the object is the same as the one scanned
                    //It needs to skip this state and move on but needs the ID to transition properly
                    if (symbol == currChar && prevChar == line.charAt(i - 1)) {
                        transitionID = state.getID();
                        duplicateCharCount++;
                        break;
                    }

                }

                //Creates state for current character
                State state = new State(currentID, i * DIST_SCALE_HOR, currentHeight, false, isFinal, printWriter, line.charAt(i), prevChar);
                statesList.add(state);

                //Adds transitions
                if(duplicateCharCount == 0) {
                    Transition transition = new Transition(currentID - 1, currentID, symbol, printWriter);
                    transitionsList.add(transition);
                    currentID++;
                }else{
                    Transition transition = new Transition(transitionID, currentID, symbol, printWriter);
                    transitionsList.add(transition);
                    duplicateCharCount = 0;
                    currentID++;
                }

                //Checks for final state
                if(isFinal){
                    break;
                }

                //Checks for invalid chars taken from example
                if (!Character.isLowerCase(symbol)) {
                    System.out.println("Symbol '"+ symbol +"' is invalid. All characters should be from a-z");
                            System.exit(1); // Exit with code 1
                }


            }
            //Moves current height
            currentHeight += DIST_SCALE_VERT;
        }

        //Creates first state and set it to the front
        State p0 = new State(0, -2 * DIST_SCALE_HOR, (currentHeight - DIST_SCALE_VERT) / 2, true, false, printWriter,' ', ' ');
        statesList.addFirst(p0);
        for(State state : statesList){
            state.start();
            state.join();
        }

        for(Transition transition : transitionsList){
            transition.start();
            transition.join();
        }

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
     * @param printWriter output
     */
    private static void tail(PrintWriter printWriter) {
        System.out.println("</automaton></structure>");
        printWriter.println("</automaton></structure>");
    }

}


class State extends Thread {
    private int id;
    private int x;
    private int y;
    private char currChar;
    private char prevChar;
    private boolean isInitial;
    private boolean isFinal;
    private PrintWriter printWriter;
    private char[] characterList;

    public State(int id, int x, int y, boolean isInitial, boolean isFinal, PrintWriter printWriter, char currChar, char prevChar){
        this.id = id;
        this.x = x;
        this.y = y;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        this.printWriter = printWriter;
        this.prevChar = prevChar;

    }

    @Override
    public void run(){
        //Prints out to terminal
        System.out.println("<state id=\"" + id + "\" name=\"q" + id + "\">");
        System.out.println("<x>" + x + "</x>");
        System.out.println("<y>" + y + "</y>");
        //Prints out to file
        printWriter.println("<state id=\"" + id + "\" name=\"q" + id + "\">");
        printWriter.println("<x>" + x + "</x>");
        printWriter.println("<y>" + y + "</y>");
        if (isInitial) {
            System.out.println("<initial/>");
            printWriter.println("<initial/>");
        }
        if (isFinal) {
            System.out.println("<final/>");
            printWriter.println("<final/>");
        }

        System.out.println("</state>");
        printWriter.println("</state>");
    }

    public char getCurrChar() {
        return currChar;
    }

    public char getPrevChar() {
        return prevChar;
    }

    public int getID(){
        return id;
    }

}


class Transition extends Thread{
    private int origin;
    private int dest;
    private char symbol;
    private PrintWriter printWriter;

    public Transition(int origin, int dest, char symbol, PrintWriter printWriter){
        this.origin = origin;
        this.dest = dest;
        this.symbol = symbol;
        this.printWriter = printWriter;

    }

    @Override
    public void run(){
        System.out.println("<transition>");
        System.out.println("<from>"+ origin+"</from>");
        System.out.println("<to>"+ dest +"</to>");
        System.out.println("<read>"+ symbol +"</read>");
        System.out.println("</transition>");
        printWriter.println("<transition>");
        printWriter.println("<from>"+ origin+"</from>");
        printWriter.println("<to>"+ dest +"</to>");
        printWriter.println("<read>"+ symbol +"</read>");
        printWriter.println("</transition>");
    }
}