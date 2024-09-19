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
        ArrayList<Character> charList = new ArrayList<>();
        ArrayList<Integer> originList = new ArrayList<>();
        ArrayList<Integer> destinList = new ArrayList<>();
        ArrayList<Integer> isFinal = new ArrayList<>();
        ArrayList<Character> charTemp = new ArrayList<>();
        ArrayList<Integer> currID = new ArrayList<>();

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

        //Starter for .jff file
        head(printWriter);

        int currentID = 1;
        int currentHeight = 0;
        int currentWidth = 0;
        char[] previousChars = {' '};
        boolean branching = false;
        int branches =0;

        //Attempting to make a list of chars and then make all the states afterward
        for (String word : wordList) {
            System.out.println(word);
            char[] chars = word.toCharArray();

            for (int j = 0; j < chars.length ; j++) {

                //final == 0
                //start of word == 1
                //neither == 2
                //3 used for initial p0
                if (j == chars.length - 1) {

                    isFinal.add(0);
                } else if (j == 0) {
                    isFinal.add(1);
                } else {
                    isFinal.add(2);
                }

                //Makes sure that the program doesn't blow up
                //By making sure that it is only trying to compare in bounds
                //Can be turned to try catch
                if (j < previousChars.length - 1) {

                        //Checks to see if the letter was already scanned before
                        //If it did it would add the original and destin
                        if (previousChars[j] == chars[j]) {
                            charList.add(chars[j]);
                            originList.add(originList.get(charList.size() - previousChars.length - j - 1));
                            destinList.add(currentID);
                            branching = true;

                            //Checks to see if it is a new sequence
                            //By checking to see if it is a different starting letter
                            //and is at the beginning of a word
                        } else if (previousChars[j] != chars[j] && j == 0) {
                            charList.add(chars[j]);
                            originList.add(0);
                            destinList.add(currentID);

                            //defaults adds any normal states
                        } else {
                            charList.add(chars[j]);
                            originList.add(currentID-1);
                            destinList.add(currentID);
                            currentID++;
                        }

                        //Adds any normal states
                } else {
                    charList.add(chars[j]);
                    originList.add(currentID-1);
                    destinList.add(currentID);
                    currentID++;

                }

                //Checks for concurrent letters
                /*if(j!=0){
                    if(chars[j] == chars[j-1]){
                        charList.add(chars[j]);
                        originList.add(currentID-1);
                        destinList.add(currentID);
                        currentID++;
                    }
                }*/


            }
            previousChars = word.toCharArray();

        }

        for (char c : charList) {
            System.out.print(c);

        }
        System.out.println();

        for (Integer origin : originList) {
            System.out.print(origin + " ");
        }
        System.out.println(originList.size());

        for (Integer i : destinList) {
            System.out.print(i + " ");

        }
        System.out.println(destinList.size());

        for (Integer i : isFinal) {
            System.out.print(i + " ");
        }
        System.out.println(isFinal.size());

        boolean start = true;
        currentID = 1;

        //Loops through charList array whilst stuff in array
        while (!charList.isEmpty()) {
            Character c = charList.removeFirst();
            Integer origin = originList.removeFirst();
            Integer dest = destinList.removeFirst();
            int isFin = isFinal.removeFirst();

            //When currentId equals the state it will create a new state
            //Otherwise it should only create a transition
            if (origin > 0 || start) {
                start = false;
                States state = new States(currentID, currentWidth, currentHeight, isFin, printWriter, c);
                statesList.add(state);
                currentID++;
            }

            switch (isFin) {
                case (0): //When final
                    Transition transition = new Transition(origin, dest, c, printWriter);
                    transitionsList.add(transition);
                    currentHeight += DIST_SCALE_VERT;
                    currentWidth = 0;
                    break;

                case (1): //When Initial it will branch off centre
                    transition = new Transition(origin, dest, c, printWriter);
                    transitionsList.add(transition);
                    currentWidth += DIST_SCALE_HOR;
                    break;

                case (2): //When in middle
                    transition = new Transition(origin, dest, c, printWriter);
                    transitionsList.add(transition);
                    currentWidth += DIST_SCALE_HOR;
                    break;

            }


        }

        //Loops through whilst there is stuff in the scanner
        /*while (!wordList.isEmpty()){
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
        }*/

        //Creates first state and set it to the front
        States p0 = new States(0, -2 * DIST_SCALE_HOR, (currentHeight - DIST_SCALE_VERT) / 2, 3, printWriter, ' ');
        statesList.addFirst(p0);
        for (States state : statesList) {
            state.start();
            state.join();
        }

        //DFA dfa = new DFA(transitionsList, statesList);
        //dfa.start();
        //dfa.join();


        for (Transition transition : transitionsList) {
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
    private final char currChar;
    private final int isFinal;
    private final PrintWriter printWriter;

    public States(int id, int x, int y, int isFinal, PrintWriter printWriter, char currChar) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isFinal = isFinal;
        this.printWriter = printWriter;
        this.currChar = currChar;

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
            case (3) :
            System.out.println("<initial/>");
            printWriter.println("<initial/>");
                break;
            case (0) :
            System.out.println("<final/>");
            printWriter.println("<final/>");
                break;
            default:
                break;
        }

        System.out.println("</state>");
        printWriter.println("</state>");
    }

    public char getCurrChar() {
        return currChar;
    }

    public int getID() {
        return id;
    }

}

class Transition extends Thread {
    private int origin;
    private int dest;
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

    public void setDest(int dest) {
        this.dest = dest;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
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

class DFA extends Thread {
    private static ArrayList<Transition> transitions;
    private static ArrayList<States> states;

    public DFA(ArrayList<Transition> transitions, ArrayList<States> states) {
        DFA.transitions = transitions;
        DFA.states = states;

    }

    @Override
    public void run() {
        for (int i = 0; i < transitions.size(); i++) {
            Transition tI = transitions.get(i);
            for (int j = 1; j < transitions.size(); j++) {
                Transition tJ = transitions.get(j);
                if (tI.getOrigin() == tJ.getOrigin() && tI.getDest() != tJ.getDest() && tI.getSymbol() == tJ.getSymbol()) {
                    tJ.setDest(tI.getDest());
                }
            }
        }

        for (int i = 0; i < states.size(); i++) {
            States sI = states.get(i);
            for (int j = 1; j < states.size(); j++) {
                States sJ = states.get(j);
                if (sI.getCurrChar() == sJ.getCurrChar() && sI.getID() != sJ.getID()) {

                }
            }
        }

    }
}