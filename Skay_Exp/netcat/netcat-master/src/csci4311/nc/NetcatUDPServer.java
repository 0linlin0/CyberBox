package csci4311.nc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Establishes a connection to the server on the given host name (or IP address) and port number and operates in one of
 * two modes: download and upload.
 *
 * @author tlmader.dev@gmail.com
 * @since 2017-02-27
 */
@SuppressWarnings("JavaDoc")
public class NetcatUDPServer {

    private static DatagramSocket serverSocket;
    private static DatagramPacket pingPacket;
    private static boolean downloadMode;
    private static boolean pinged;

    /**
     * Creates welcome socket and starts update loop to handle arbitrary sequence of clients making requests.
     *
     * @param port a port number
     * @throws Exception
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private static void start(int port) throws Exception {
        serverSocket = new DatagramSocket(port);
        if (System.in.available() > 0) {
            downloadMode = true;
        }
        while (true) {
            if (downloadMode) {
                download();
            } else {
                upload();
            }
        }
    }

    /**
     * In download mode, server reads data from the socket and writes it to standard output.
     *
     * @throws Exception
     */
    private static void download() throws Exception {
        if (!pinged) {
            byte[] receiveData = new byte[1024];
            pingPacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(pingPacket);
            pinged = true;
        }
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            byte[] sendData = input.nextLine().getBytes();
            serverSocket.send(new DatagramPacket(sendData, sendData.length, pingPacket.getAddress(), pingPacket.getPort()));
        }
    }

    /**
     * In upload mode, server reads data from its standard input device and writes it to the socket.
     *
     * @throws Exception
     */
    private static void upload() throws Exception {
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        System.out.println(new String(receivePacket.getData()).trim());
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
