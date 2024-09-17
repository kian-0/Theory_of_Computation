/** Author: Kian Aliwalas
 *  Project 1 DFA Construction
 *  Fall 2024 Theory of Computation
 *  Instructor: Dylan Strickley
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;

public class Project1 {
    private static final int DIST_SCALE_HOR = 200;
    private static final int DIST_SCALE_VERT = 60;
    private static ArrayList<States> statesList = new ArrayList<>();
    private static ArrayList<Transition> transitionsList = new ArrayList<>();
    private static ArrayList<String> wordList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(new File("example.txt"));
        PrintWriter printWriter = new PrintWriter(new FileWriter("output.jff"));

        //Puts every line in an ArrayList
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            wordList.add(line);
        }

        //Sorts wordList
        Collections.sort(wordList);


        //Starter for .jff file
        head(printWriter);

        int currentID = 1;
        int transitionID = 1;
        int duplicateCharCount = 0;
        int currentHeight = 0;
        int charPos = 0;
        char prevChar = ' ';
        char[] previousChars = new char[wordList.size()];

        //Attempting to make a list of chars and then make all the states afterwards
        for(int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i);
            char[] chars = word.toCharArray();


            for(int j = 0; j < chars.length; j++) {
                if(previousChars[j] == chars[j]){

                }



            }

            previousChars = word.toCharArray();
        }

        //Loops through whilst there is stuff in the scanner
        while (!wordList.isEmpty()){
            String line = wordList.removeFirst();

            //Checks each char in scanned line
            for (int i = 0; i < line.length(); i++) {
                // Check if the current character is the end of the string
                boolean isFinal = (i + 1 >= line.length()) || (line.charAt(i + 1) == '\r') || (line.charAt(i + 1) == '\n');
                char symbol = line.charAt(i);

                //Creates state for current character
                States state = new States(currentID, i * DIST_SCALE_HOR, currentHeight, false, isFinal, printWriter, symbol);
                statesList.add(state);

                //Adds transitions
                if(i==0) {
                    Transition transition = new Transition(0, currentID, symbol, prevChar, i, printWriter);
                    transitionsList.add(transition);
                    currentID++;
                } else{
                    Transition transition = new Transition(currentID - 1, currentID, symbol, prevChar, i, printWriter);
                    transitionsList.add(transition);
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
                if(prevChar != line.charAt(i)) {
                    prevChar = line.charAt(i);
                    transitionID = i;
                }
            }

            //Moves current height
            currentHeight += DIST_SCALE_VERT;
        }

        //Creates first state and set it to the front
        States p0 = new States(0, -2 * DIST_SCALE_HOR, (currentHeight - DIST_SCALE_VERT) / 2, true, false, printWriter,' ');
        statesList.addFirst(p0);
        for(States state : statesList){
            state.start();
            state.join();
        }

        DFA dfa = new DFA(transitionsList, statesList);
        dfa.start();
        dfa.join();


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

class CharOrg{

}

class States extends Thread {
    private int id;
    private int x;
    private int y;
    private char currChar;
    private char prevChar;
    private boolean isInitial;
    private boolean isFinal;
    private PrintWriter printWriter;
    private char[] characterList;

    public States(int id, int x, int y, boolean isInitial, boolean isFinal, PrintWriter printWriter, char currChar){
        this.id = id;
        this.x = x;
        this.y = y;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        this.printWriter = printWriter;
        this.currChar = currChar;

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
    private char prevChar;
    private int pos;
    private PrintWriter printWriter;

    public Transition(int origin, int dest, char symbol, char prevChar, int pos, PrintWriter printWriter){
        this.origin = origin;
        this.dest = dest;
        this.symbol = symbol;
        this.prevChar = prevChar;
        this.pos = pos;
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

    public char getPrevChar() {
        return prevChar;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
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

class DFA extends Thread{
    private static ArrayList<Transition> transitions;
    private static ArrayList<States> states;

    public DFA(ArrayList<Transition> transitions, ArrayList<States> states){
        this.transitions = transitions;
        this.states = states;

    }

    @Override
    public void run() {
        for(int i = 0; i < transitions.size(); i++){
            Transition tI = transitions.get(i);
            for(int j = 1; j < transitions.size(); j++){
                Transition tJ = transitions.get(j);
                if(tI.getOrigin() == tJ.getOrigin() && tI.getDest() != tJ.getDest() && tI.getSymbol() == tJ.getSymbol()){
                    tJ.setDest(tI.getDest());
                }
            }
        }

        for(int i = 0; i < states.size(); i++){
            States sI = states.get(i);
            for(int j = 1; j < states.size(); j++){
                States sJ = states.get(j);
                if(sI.getCurrChar() == sJ.getCurrChar() && sI.getID() != sJ.getID()){

                }
            }
        }

    }
}