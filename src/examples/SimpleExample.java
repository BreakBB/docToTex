package examples;

import com.sun.org.glassfish.gmbal.Description;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a very basic description of an outer Java-class. It contains absolutely no special characters,
 * except for these: {} () [] © ²³ \[T]/.
 * Those special characters are escaped and don't result in a LaTex conflict.
 *
 * @author Lutz Winkelmann, Björn Böing
 * @see examples.SimpleExample.My22Class#getx text
 * @version 1.0-nightlybuild
 */
public class SimpleExample extends AnotherClass implements Runnable{

    /**
     * This ArrayList holds all the current names.
     */
    private ArrayList<String> allNames;
    /**
     * This string stores the favorite name.
     */
    private String favoriteName;
    private static int counter; // No Javadoc needed


    SimpleExample(String name){
        allNames = new ArrayList<>();
        favoriteName = name;
    }

    /**
     * This method is here to have an "implements" in the class signature
     * and doesn't do anything else.
     */
    public void run() {
        // Dummy
    }

    protected ArrayList<String> getAllNames(){
        allNames.add("Dieter");

        return allNames;
    }

    /**
     * This method can be used to set your favorite name
     * @param name Your new favorite
     */
    public void setFavoriteName(String name){
        favoriteName = name;
        counter++;
    }

    /*
    Here is a multiline
    block comment, which is skipped from the compiler since it doesn't include
    useful information.
     */
    /* And this is an inline block comment just to show something more */

    /**
     * Here is our main method which doesn't make much sense, but includes a parameter.
     * @param args An array of arguments
     */
    public static void main(String[] args){
        counter = 0;
    }

    @Description(key = "description", value = "Some fancy text to show this strange description annotation which is" +
            "added to Javadoc")
    public static abstract class My22Class {
        abstract int getx(String a);

        /**
         *
         * @throws IOException asdasd
         */
        My22Class() throws IOException {
            throw new IOException();
        }

        /**
         * This method is to show a long list of params in combination with an annotation
         * following the Javadoc
         * @param x The best value
         * @param y The worth value
         * @param a A cool string
         * @param map A map with really important information
         */
        @com.sun.org.glassfish.gmbal.Description("Here my value" +
                "over multiple lines to show an even stranger annotation")
        public void coolMethod(int x, int y, String a, HashMap<Integer, String> map) {
            // Do some stuff
        }
    }

    /**
     * This method has some more "logic" included to show that everything inside of a method
     * is skipped. Moreover there is the parameter "notDocumented" which isn't listed in the
     * Javadoc params.
     * @param a A really huge and abstract objec
     * @param b Some useless number
     */
    public static void myMethod(ArrayList<HashMap<My22Class, Integer[]>> a, int b, String notDocumented) {
        int x = 0;

        {
            int y = 123;
            System.out.println(x + y);
        }
        x = b;
        x += 3;
        notDocumented = "Bad!";
    }

    /*
    Here is an undocumented method with really strange coding style.
     */
    public ArrayList    <String> anotherMethod(String[    ] a, int b) {
        int d;
        int     e =     5   + 2;

        String   [] f ;

        return new ArrayList<>();
    }
}

class AnotherClass {

}