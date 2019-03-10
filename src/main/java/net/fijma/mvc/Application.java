package net.fijma.mvc;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class Application {

    private static final Logger log = Logger.getGlobal();

    private final Map<Class<?>, ApplicationModule> modules = new HashMap<>();

    private volatile boolean run = true;
    private Queue<Msg> msgQueue = new ConcurrentLinkedQueue<>();

    protected Application() {
    }

    protected void registerModule(ApplicationModule m) {
        modules.put(m.getClass(), m);
    }

    protected <T extends ApplicationModule> T getModule(Class<T> key) {
        return key.cast(modules.get(key));
    }

    private void configureLogger() throws IOException {
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

    public void run(Controller controller) throws IOException {
        configureLogger();

        for (ApplicationModule m : modules.values()) {
            log.info("starting: " + m.getClass().getSimpleName());
            m.start();
        }

        log.info("starting keyboard loop");
        // start reading the keyboard
        Thread kb = new Thread(this::keyboardLoop);
        kb.start();

        // run controller on main thread until it terminates
        controller.run();

        log.info("waiting for keyboard loop to stop");
        // wait for keyboard loop to stop
        run = false;
        while (true) {
            try {
                kb.join();
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (ApplicationModule m : modules.values()) {
            log.info("stopping: " + m.getClass().getSimpleName());
            m.stop();
        }

        log.info("run loop finished");
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
                msgQueue.offer(new KeyMsg(console.reader().read()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.warning("error reading keyboard: " + e.getMessage());
            }
        }
    }

    static public class KeyMsg implements Msg {
        public final Integer key;
        private KeyMsg(Integer key) { this.key = key; }
    }

    Msg poll() { return msgQueue.poll(); }

    public void offer(Msg msg) { msgQueue.offer(msg); }


}
