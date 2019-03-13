package net.fijma.mvc.example;

import net.fijma.mvc.View;
import net.fijma.mvc.Event;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class MainView extends View<AppModel> {

    private static final Logger LOGGER = Logger.getLogger(MainView.class.getName());

    final Event<Void> up = new Event<>();
    final Event<Void> down = new Event<>();

    MainView(AppModel model) {
        super(model);
    }

    void somethingChanged(int i) {
        assert (model.value == i); // we do have access to the model
        setRC(10,10);
        System.out.print((i < 0 ? red() : green()) + String.format("%10s", i));
    }

    @Override
    public void draw() {
        red();
        try {
            List<String> lines = Files.readAllLines(Paths.get("screen.txt"), Charset.forName("UTF-8"));
            for (int i=0; i<Math.min(24, lines.size()); ++i) {
                setRC(i+1, 1);
                System.out.print(lines.get(i).replace("\n", ""));
            }
        } catch (IOException e) {
            LOGGER.severe("cannot read screen.txt: " + e.getMessage());
        }
    }

    @Override
    public boolean key(int k) {
        switch (k) {
            case 88: // X, quit
            case 120:
                reset();
                return false;
            case 77: // M
            case 109:
                up.trigger(null);
                break;
            case 78: // N
            case 110:
                down.trigger(null);
                break;
            default:
                ; // ?
        }
        return true;
    }
}
