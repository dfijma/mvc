package net.fijma.mvc.application;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class MainView extends AbstractView {

    Logger log = Logger.getGlobal();

    final Event<Void> up = new Event<>();
    final Event<Void> down = new Event<>();

    MainView(Model model) {
        super(model);
    }

    void somethingChanged(int i) {
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
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean key(int k) {
        switch (k) {
            case 88: // X, quit
            case 120:
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
