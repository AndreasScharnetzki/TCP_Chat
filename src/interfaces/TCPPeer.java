package interfaces;

import implementation.SocketStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TCPPeer {

    //E r r o r & M e s s a g e s

    //TCP SERVER
        String ERR_TIMEOUT_SERVERSOCKET = "SeverSocket received no incoming Connection-Requests for 30 seconds and was therefor closed.\n";
    //CONNECTING
        String MSG_WAINTING = "Still waiting for connection";

    /**
     * used to create a reference of the class' InputStream
     * @return reference of private class member InputStream
     * @throws Exception unable to access classMember variable
     */
    InputStream getInputStream () throws Exception;

    /**
     * used to create a reference of the class' InputStream
     * @return reference of private class member InputStream
     * @throws Exception unable to access classMember variable
     */
    OutputStream getOutputStream () throws Exception;

    /**
     * to be used if TCPPeer is a SERVER, will make the Chat accept an incoming connection request,
     * if connected successfully a message will be displayed
     * @param port portNumber which will accept an incoming request
     * @throws IOException
     */
    void open (int port) throws IOException;

    /**
     * to be used if TCPPeer is a CLIENT, if connected successfully a message is displayed
     * @param hostName Name of the server one intends to connect to
     * @param port portNumber which should accept incoming request(s)
     * @throws IOException
     */
    void connect (String hostName, int port) throws IOException;

    /**
     * closes the socket used
     * @throws Exception called before socket was opened
     */
    void close() throws Exception;

    /**
     * waits for and displays incoming messages (thread-based)
     * @throws Exception malfunction while reading incoming message
     */
    void listen() throws Exception;

    /**
     * returns status of the socket (open/close/connected)
     * @throws Exception due to invalid method call
     * @return
     */
    SocketStatus socketStatus() throws Exception;
}
