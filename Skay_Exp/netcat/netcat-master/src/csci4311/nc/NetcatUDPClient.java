package csci4311.nc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Binds to a specified port number on the local host and waits for a connection request from a client. Once a
 * connection is established, it operates in one of two modes: download and upload.
 *
 * @author tlmader.dev@gmail.com
 * @since 2017-02-27
 */
public class NetcatUDPClient {

    private static DatagramSocket clientSocket;
    private static InetAddress ipAddress;
    private static boolean uploadMode;
    private static boolean pinged;

    /**
     * Creates client socket makes request to the server.
     *
     * @throws Exception
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private static void start(String host, int port) throws Exception {
        clientSocket = new DatagramSocket();
        ipAddress = InetAddress.getByName(host);
        if (System.in.available() > 0) {
            uploadMode = true;
        }
        while (true) {
            if (uploadMode) {
                upload(port);
            } else {
                download(port);
            }
        }
    }

    /**
     * In download mode, client reads data from the socket and writes it to standard output.
     *
     * @throws Exception
     */
    private static void download(int port) throws Exception {
        if (!pinged) {
            byte[] sendData = "".getBytes();
            DatagramPacket pingPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
            clientSocket.send(pingPacket);
            pinged = true;
        }
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        System.out.println(new String(receivePacket.getData()).trim());
    }

    /**
     * In upload mode, client reads data from its standard input device and writes it to the socket.
     *
     * @throws Exception
     */
    @SuppressWarnings("Duplicates")
    private static void upload(int port) throws Exception {
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            byte[] sendData = input.nextLine().getBytes();
            clientSocket.send(new DatagramPacket(sendData, sendData.length, ipAddress, port));
        }
    }

    /**
     * Starts execution of the program, requiring a host name and port number as an argument.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args[0] != null && args[1] != null) {
            start(args[0], Integer.parseInt(args[1]));
        } else {
            System.out.println("usage:\ndownload: java main.java.nc.NetcatClient [host] [port] > [downloaded-file]\nupload: java main.java.nc.NetcatClient [host] [port] > [original-file]");
        }
    }
}
