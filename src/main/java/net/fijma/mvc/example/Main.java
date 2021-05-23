package net.fijma.mvc.example;

import net.fijma.mvc.serial.Serial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main( String[] args ) {
        try {
            // setup
            App app = new App();
            AppModel model = new AppModel();
            MainView view = new MainView(model);
            AppController controller = new AppController(app, model, view);
            // run
            Serial.probe();
            log.info("starting");
            app.run(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
