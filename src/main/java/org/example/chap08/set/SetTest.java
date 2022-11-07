package org.example.chap08.set;

import java.util.HashSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetTest {
    public static void main(String[] args) {
        Logger.getLogger("com.horstmann").setLevel(Level.FINEST);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        Logger.getLogger("com.horstmann").addHandler(handler);

        HashSet parts = new HashSet<Item>();
        parts.add(new Item("Toaster",1279));
        parts.add(new Item("Microwave",4104));
        parts.add(new Item("Toaster",1279));
        System.out.println(parts);
    }
}
