package net.fijma.mvc.example;

import net.fijma.mvc.AbstractController;
import net.fijma.mvc.Event;

import java.io.Console;
import java.io.IOException;
import java.util.logging.*;

public class Main {

    private volatile boolean run = true;
    private Event<Integer> keyAvailable = new Event<>();

    public static void main( String[] args ) throws Exception {
        configureLogger();
        new Main().run();
    }

    private static void configureLogger() throws Exception {
        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // remove default handler to remove standard handlers
        Logger l0 = Logger.getLogger("");
        l0.removeHandler(l0.getHandlers()[0]);

        // global logger: INFO + simple formatter to log.txt
        logger.setLevel(Level.INFO);
        FileHandler fileTxt = new FileHandler("log.txt");
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
    }

    public void run() {
        // setup model
        AppModel model = new AppModel();

        // Setup controller and view
        AbstractController controller = AppController.setup(model, keyAvailable);

        // start reading the keyboard
        Thread kb = new Thread(this::keyboardLoop);
        kb.start();

        // run controller on main thread until it terminates
        controller.run();

        // wait for keyboard loop to stop
        run = false;
        while (true) {
            try {
                kb.join();
                break;
            } catch (InterruptedException ignored) { }
        }
    }

    // http://develorium.com/2016/03/unbuffered-standard-input-in-java-console-applications/
    private void keyboardLoop() {
        Console console = System.console();
        while (run) {
            try {
                boolean a = console.reader().ready();
                if (!a) {
                    Thread.sleep(10);
                    continue;
                }
                keyAvailable.trigger(console.reader().read());
            } catch (IOException | InterruptedException ignored) {  }
        }
    }
}
