package at.pwimmer.sm.gateway.tasks;

import io.micrometer.core.annotation.Timed;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

@Timed(longTask = true)
public class SmartReader implements Runnable {
    private static final int DEFAULT_SO_TIMEOUT = 15000;

    private final String host;
    private final int port;
    private final int timeout;

    private volatile boolean running = true;

    public SmartReader(String smHost, int smPort, int smSoTimeout) {
        this.host = smHost;
        this.port = smPort;
        this.timeout = smSoTimeout;
    }

    public SmartReader(String smHost, int smPort) {
        this.host = smHost;
        this.port = smPort;
        this.timeout = DEFAULT_SO_TIMEOUT;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName("Smart-Reader ["+host+"::"+port+"]");

            while(running && !Thread.currentThread().isInterrupted()) {
                System.out.println("Starting the Smart-Reader for Smart-Meter [" + host + "::" + port + "]");

                try(Socket socket = new Socket(host, port); InputStream input = socket.getInputStream()) {
                    socket.setSoTimeout(timeout);

                    System.out.println("Created TCP-Connection to Smart-Meter via " + socket.toString());

                    while(!socket.isClosed() && running && !Thread.currentThread().isInterrupted()) {
                        byte[] data = receiveDataFrom(socket, input);
                        // TODO: process the received byte-buffer from the Smart-Meter.
                    }

                    System.out.println("TCP-Connection to Smart-Meter has been closed!");
                }
                catch(IOException ex) {
                    System.err.println("Failed to initialize TCP-Socket for Smart-Meter on [" + host+"::"+port+"] <Error: " + ex.getMessage() + ">");
                }
            }
        }
        finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private byte[] receiveDataFrom(final Socket socket, final InputStream input) throws IOException {
        try {
            final byte[] buff = new byte[socket.getReceiveBufferSize()];
            final int read = input.read(buff);

            if(read != -1) {
                System.out.println("Received data-block from Smart-Meter of " + read + " bytes");
                return Arrays.copyOf(buff, read);
            }
            else {
                socket.close();
                System.out.println("End-Of-Stream has been reached on TCP-Connection - Closing socket");
            }
        }
        catch(SocketTimeoutException ex) {
            System.out.println("Socket-Timeout has been reached!");
        }

        return null;
    }
}
