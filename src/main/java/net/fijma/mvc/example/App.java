package net.fijma.mvc.example;

import net.fijma.mvc.Application;
import net.fijma.mvc.serial.Serial;

public class App extends Application {

    App() {
        super();
        // register optional module(s)
        registerModule(new Serial(this,"/dev/cu.Bluetooth-Incoming-Port"));
    }

    public void probe() {
        Serial.probe();
    }
}
