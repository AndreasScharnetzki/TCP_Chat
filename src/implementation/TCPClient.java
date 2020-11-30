package implementation;
import interfaces.ChatUI;
import interfaces.TCPPeer;

import java.io.*;
import java.net.Socket;

public class TCPClient implements TCPPeer, Runnable {

    //new constructor
    TCPClient (PrintStream _ps){
        this.ps = _ps;
    }

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private PrintStream ps;

    public void connect(String hostname, int port) throws IOException {
        socket = new Socket( hostname, port );
        this.is = this.socket.getInputStream();
        this.os = this.socket.getOutputStream();
    }

    @Override
    public void close() throws Exception {
        this.socket.close();
    }

    //Identical to TCPServer since it doesn't matter anymore once the connection is established who started as Server/Client
    public InputStream getInputStream() throws Exception {
        if(this.is == null){
            throw new Exception( TCPPeer.MSG_WAINTING );
        }
        return this.is;
    }

    public OutputStream getOutputStream() throws Exception {
        if(this.os == null){
            throw new Exception( TCPPeer.MSG_WAINTING );
        }
        return this.os;
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
            ps.println( "Server wrote: " + tempMessage);
        }
    }

    @Override
    public void run() {
        try {
            this.listen();
        }catch (Exception e){
            System.err.println( "ERROR in ClientThread, while listening" );
            e.printStackTrace();
        }
    }

    @Override
    public SocketStatus socketStatus(){
        if( this.socket.isClosed() )    {return SocketStatus.CLOSED;}
        if( this.socket.isConnected() ) {return SocketStatus.CONNECTED;}
        else                            {return SocketStatus.UNDEFINED;}
    }

    public void open(int port) throws IOException {////I only exist due to my creators laziness to write a 2nd Interface for the networkSubsystem
    }
}
