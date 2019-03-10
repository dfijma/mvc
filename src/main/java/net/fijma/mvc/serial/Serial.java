package net.fijma.mvc.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import net.fijma.mvc.Application;
import net.fijma.mvc.ApplicationModule;
import net.fijma.mvc.Msg;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Serial implements ApplicationModule, SerialWriter, SerialPortDataListener {

    private static Logger log = Logger.getGlobal();

    private final String device;
    private final Application application;
    private SerialPort serial = null;

    static public class SerialMsg implements Msg {
        public final String line;
        private SerialMsg(String line) { this.line = line; }
    }

    public static void probe() {
        // list serial ports
        for (SerialPort s: SerialPort.getCommPorts()){
            System.out.println(s.getSystemPortName());
        }
    }

    public Serial(Application application, String device) {
        this.device = device;
        this.application = application;
    }

    @Override
    public void start() throws IOException {

        log.info("starting serial");
        // TODO: remove hardcoded stuff
        int baudRate = 57600; // 115200; //

        // open serial port and read data in asynchronous fashion
        serial = SerialPort.getCommPort(device);
        serial.setComPortParameters(baudRate, 8, 1, SerialPort.NO_PARITY);
        serial.addDataListener(this);
        if (!serial.openPort()) throw new IOException("cannot open serial port");
    }

    @Override
    public void stop() {
        log.info("waiting for serial to stop");
        if (serial != null) serial.closePort();
    }

    // @Override
    public void write(String s) throws IOException {
        byte[] bs = s.getBytes(Charset.forName("ASCII"));  // This is the eighties, we don't do UTF-8
        serial.writeBytes(bs, bs.length);
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
        byte[] newData = new byte[serial.bytesAvailable()];
        int numRead = serial.readBytes(newData, newData.length);
        for (int i=0; i<numRead; ++i) {
            this.onSerialByte(newData[i]);
        }
    }

    // keep incoming serial data and decode to string, display as msg on complete
    private StringBuilder serialString = new StringBuilder();

    private void onSerialByte(int b) {
        // decode incoming serial bytes as ASCII (yes, no fancy UTF-8 stuff expected)
        if (b == 10) return;
        if (b == 13) {

            // post available serial line to msg queue
            application.offer(new SerialMsg(serialString.toString()));

            serialString = new StringBuilder();
            return;
        }
        serialString.append((char)b);
    }

    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }
}

