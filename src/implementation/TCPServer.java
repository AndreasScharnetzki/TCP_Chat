package implementation;

import interfaces.ChatUI;
import interfaces.TCPPeer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPServer implements TCPPeer, Runnable {

    //new constructor, taking the PrintStream (OutputStream) of ChatUI Subsystem
    TCPServer (PrintStream _ps){this.ps = _ps;}
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private PrintStream ps;

    private static final int TIMEOUT = 50000;     //ms

    public void open(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket( port );

        try {
            // serverSocket.setSoTimeout( TIMEOUT );           //if there is no request incoming within the next 30 sec after connect() was called, the serverSocket will be closed
            this.socket = serverSocket.accept();            //represents the connectionPoint to which the Client connects to
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();

        } catch (SocketTimeoutException STOExc) {
            System.err.print( TCPPeer.ERR_TIMEOUT_SERVERSOCKET );
            serverSocket.close();
        }
    }

    //Identical to TCPClient since it doesn't matter anymore once the connection is established who started as Server/Client
    public InputStream getInputStream() throws Exception {
        if (this.is == null) {
            throw new Exception( TCPPeer.MSG_WAINTING );
        }
        return this.is;
    }

    public OutputStream getOutputStream() throws Exception {
        if (this.os == null) {
            throw new Exception( TCPPeer.MSG_WAINTING );
        }
        return this.os;
    }

    public void close() throws Exception {
        this.socket.close();
    }

    public void listen() throws IOException {
        String tempMessage = ".";
        DataInputStream tcpDIS = new DataInputStream( is );
        while(!tempMessage.equals( ChatUI.CMD_DISCONNECT )){
            tempMessage = tcpDIS.readUTF().trim();
            if(tempMessage.equals( ChatUI.CMD_DISCONNECT )){
                socket.close();
                break;
            }
            ps.println( "client wrote: " + tempMessage);
        }
    }

    @Override
    public SocketStatus socketStatus(){
        if( this.socket.isClosed() )    {return SocketStatus.CLOSED;}
        if( this.socket.isConnected() ) {return SocketStatus.CONNECTED;}
        else                            {return SocketStatus.UNDEFINED;}
    }

    @Override
    public void run() {
        try {
            this.listen();
        }catch (Exception e){
            System.err.println( "ERROR in ServerThread, while listening" );
            e.printStackTrace();
        }
    }

    public void connect(String hostName, int port) throws IOException {//I only exist due to my creators laziness to write a 2nd Interface for the networkSubsystem}}
    }
}
