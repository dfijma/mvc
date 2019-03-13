package net.fijma.mvc.example;

import net.fijma.mvc.serial.Serial;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main( String[] args ) {
        try {
            // setup
            App app = new App();
            AppModel model = new AppModel();
            MainView view = new MainView(model);
            AppController controller = new AppController(app, model, view);
            // run
            Serial.probe();
            LOGGER.info("starting");
            app.run(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
