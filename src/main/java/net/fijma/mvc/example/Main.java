package net.fijma.mvc.example;

import net.fijma.mvc.serial.Serial;

import java.io.IOException;

public class Main {

    public static void main( String[] args ) {
        try {
            // setup
            App app = new App();
            AppModel model = new AppModel();
            MainView view = new MainView(model);
            AppController controller = new AppController(app, model, view);
            // run
            Serial.probe();
            app.run(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
