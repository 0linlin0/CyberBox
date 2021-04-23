package csci4311.nc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Establishes a connection to the server on the given host name (or IP address) and port number and operates in one of
 * two modes: download and upload.
 *
 * @author tlmader.dev@gmail.com
 * @since 2017-02-27
 */
@SuppressWarnings("JavaDoc")
public class NetcatServer {

    private static Socket connectionSocket;
    private static BufferedReader inFromClient;

    /**
     * Creates welcome socket and starts update loop to handle arbitrary sequence of clients making requests.
     *
     * @param port a port number
     * @throws Exception
     */
    private static void start(int port) throws Exception {
        connectionSocket = null;
        ServerSocket serverSocket = new ServerSocket(port, 0);
        boolean complete;
        while (true) {
            if (connectionSocket == null) {
                connectionSocket = serverSocket.accept();
            }
            if (System.in.available() > 0) {
                complete = download();
            } else {
                complete = upload();
            }
            if (complete) {
                break;
            }
        }
        connectionSocket.close();
    }

    /**
     * In download mode, server reads data from the socket and writes it to standard output.
     *
     * @throws Exception
     */
    @SuppressWarnings("Duplicates")
    private static boolean download() throws Exception {
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        outToClient.writeBytes(new Scanner(System.in).useDelimiter("\\Z").next());
        return true;
    }

    /**
     * In upload mode, server reads data from its standard input device and writes it to the socket.
     *
     * @throws Exception
     */
    private static boolean upload() throws Exception {
        boolean complete = false;
        if (inFromClient == null) {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        }
        String line;
        while ((line = inFromClient.readLine()) != null) {
            System.out.println(line);
            complete = true;
        }
        return complete;
    }

    /**
     * Starts execution of the program, requiring a port number as an argument.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args[0] != null) {
            start(Integer.parseInt(args[0]));
        } else {
            System.out.println("usage:\ndownload: java main.java.nc.NetcatServer [port] < [original-file]\nupload: java main.java.nc.NetcatServer [port] > [uploaded-file]");
        }
    }
}
