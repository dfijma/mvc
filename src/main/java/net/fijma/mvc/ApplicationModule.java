package net.fijma.mvc;

import java.io.IOException;

public interface ApplicationModule {
    void start() throws IOException;
    void stop();
}
