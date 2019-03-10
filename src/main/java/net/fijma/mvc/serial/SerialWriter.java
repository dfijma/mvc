package net.fijma.mvc.serial;

import java.io.IOException;


public interface SerialWriter {

    void write(String s) throws IOException;

}
