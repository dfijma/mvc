package net.fijma.test;

import org.junit.Test;

import java.util.logging.*;

public class LogTest {

    @Test
    public void logTest() {

        try {
            // remove default handler to remove standard handlers
            Logger root = Logger.getLogger("");
            root.removeHandler(root.getHandlers()[0]);
            root.setLevel(Level.WARNING);

            FileHandler fileTxt = new FileHandler("log.txt");
            SimpleFormatter formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            root.addHandler(fileTxt);

            Logger specific = Logger.getLogger(LogTest.class.getName());

            specific.warning("test");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



}
