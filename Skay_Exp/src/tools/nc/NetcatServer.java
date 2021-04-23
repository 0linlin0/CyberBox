package tools.nc;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
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
            download();
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

    private static String getcmd(Scanner sc) throws Exception {
        String cmd  = sc.nextLine();
        System.out.print("-------"+cmd);
        return cmd;
    }

    private static boolean getshell(int port) throws Exception {
        connectionSocket = null;
        ServerSocket serverSocket = new ServerSocket(port, 0);
        Scanner sc = new Scanner(System.in);
        boolean complete;
        boolean a =sc.hasNext();
        while (a){
            if (connectionSocket == null) {
                connectionSocket = serverSocket.accept();
            }
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            String cmd  = getcmd(sc);
            outToClient.writeBytes(cmd + "\n");
            upload();
            outToClient.writeBytes(cmd + "\n");
            upload();
        }
        connectionSocket.close();
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

        long startTime = System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String line = null;
                while (true) {
                    try {
                        if (!((line = inFromClient.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(line);
//                    complete = true;
                    long endTime = System.currentTimeMillis();
                    if(endTime-startTime > 3000){
                        break;
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        System.out.print("aaaaaaaaaaaaaaaaaaaaa");
        return complete;
    }

    /**
     * Starts execution of the program, requiring a port number as an argument.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//        if (args[0] != null) {
//        start(Integer.parseInt("9000"));
        getshell(9000);
//        } else {
//            System.out.println("usage:\ndownload: java main.java.nc.NetcatServer [port] < [original-file]\nupload: java main.java.nc.NetcatServer [port] > [uploaded-file]");
//        }
    }
}
