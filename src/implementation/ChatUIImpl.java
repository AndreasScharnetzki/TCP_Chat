package implementation;

import interfaces.ChatUI;
import java.io.*;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class ChatUIImpl implements ChatUI {

    private BufferedReader userInputBR;

    private TCPServer tcpServer = null;
    private DataOutputStream tcpServerDOS = null;
    private DataInputStream  tcpServerDIS = null;

    private TCPClient tcpClient = null;
    private DataOutputStream tcpClientDOS = null;
    private DataInputStream  tcpClientDIS = null;

    private boolean runsAsServer = false;
    private boolean runsAsClient = false;

    public void runUI(InputStream is, OutputStream os) throws Exception {

        this.userInputBR = new BufferedReader( new InputStreamReader( is ) );
        PrintStream ps = new PrintStream( os );
        System.setErr( ps ); //passing the System.err to the print stream used for UI-Interaction, mainly used for testing purpose

        ChatImpl chatMachine = new ChatImpl();

        StringTokenizer st = null;
        String userInput = null, cmd = null, payload = null;

        boolean reading = true;

        while (reading) {

            ps.print( ">" );

            try {
                userInput = userInputBR.readLine().trim();
                if (userInput.isEmpty()) {
                    continue;
                }
            } catch (IOException e) {
                System.err.println( ChatUI.ERR_Reading );
            } catch (NullPointerException NPExc) {
                break;
            }

            st = new StringTokenizer( userInput );
            cmd = st.nextToken();

            //parsing first word of userInput, calling associated methods
            switch (cmd) {

                case (CMD_WRITE):

                    try {
                        if (!st.hasMoreTokens()) {
                            throw new IllegalArgumentException();
                        }
                    } catch (IllegalArgumentException missing_paramString) {
                        System.err.println( this.ERR_INVALID_WRITE_CALL01 );
                        continue;
                    }

                    try {
                        payload = sT2String( st );
                        chatMachine.writeToFile( payload );
                    } catch (FileNotFoundException e) {
                        System.err.println( "ChatUI-ERROR: occurred whilst passing ParameterString of UserInput to Chat SubSystem.\n" );
                        e.printStackTrace();
                        continue;
                    } catch (Exception e){
                        System.err.println( "Chat-UI-ERROR: failed to send Message" );
                        e.printStackTrace();
                        continue;
                    }

                    try {
                        if      (runsAsServer){ tcpServerDOS.writeUTF( payload ); }
                        else if (runsAsClient){ tcpClientDOS.writeUTF( payload ); }
                    } catch (Exception e) {
                        System.err.println( "ERROR: unable to transmit message." );
                    }

                    break;

                case (CMD_DISPLAY):

                    try {
                        chatMachine.readFromFile( ps );
                    } catch (FileNotFoundException FNFExc) {
                        System.err.println( ChatUI.ERR_DISPLAY_FNF_EXC );
                    } catch (Exception e) {
                        System.err.println( ChatUI.ERR_DISPLAY_EXC );
                        e.getLocalizedMessage();
                    }
                    break;


                case (CMD_DELETE):

                    ps.println( "to be implemented soon" );
                /*ps.println("Are you sure you want to delete the complete ChatLog? [Y]/[any Key]");
                String answer = br.readLine().trim();
                StringTokenizer stAnswer = new StringTokenizer(answer);

                if (stAnswer.nextToken().equals("Y") || stAnswer.nextToken().equals("y")){
                    //chatMachine.delete(); + EXCEPTIONS
                }else{
                    break;
                }*/
                    break;

                case (CMD_OPEN):
                    int port;

                    try {
                        if (!st.hasMoreTokens()) {
                            throw new IllegalArgumentException();
                        } else {
                            try {
                                port = Integer.parseInt( st.nextToken() );
                                if (port < 0) {
                                    System.err.println( "ChatUI-ERROR: portNumber needs to be in range of [0, 65535],  although you might wanna try a number greater than 1024" );
                                    continue;
                                }
                            } catch (Exception e) {
                                System.err.println( "ChatUI-ERROR: unable to parse portNumber, make sure you follow this command structure: <<open> <port_number>>\n" +
                                        "the portNumber has to be none-negative, natural number in range of [0, 65535], although you might wanna try a number greater than 1024" );
                                continue;
                            }
                        }
                    } catch (IllegalArgumentException missing_paramString) {
                        System.err.println( "ChatUI-ERROR: please specify which port should be opened <<open> <port_number>>\n" );
                        continue;
                    }

                    try {
                        tcpServer = new TCPServer(ps);  //new constructor for passing ChatUI PrintStream
                        tcpServer.open( port );
                        tcpServerDOS = new DataOutputStream( tcpServer.getOutputStream());
                        //tcpServerDIS = new DataInputStream( tcpServer.getInputStream());

                        tcpServerDOS.writeUTF(MSG_SUCCESSFULL_CON );
                        ps.println( MSG_PORT_LOCATOR + port );
                    } catch (IOException IOEx) {
                        System.err.println( ChatUI.ERR_OPEN_IO_EXC );
                        tcpServerSetBack();
                        continue;
                    } catch (SecurityException SecExc) {
                        System.err.print( ChatUI.ERR_OPEN_SEC_EXC);
                        tcpServerSetBack();
                        continue;
                    } catch (IllegalArgumentException IAExc) {
                        System.err.println( ChatUI.ERR_OPEN_ILLEGAL_ARG_EXC );
                        tcpServerSetBack();
                        continue;
                    } catch (Exception e) {
                        System.err.print( ChatUI.ERR_OPEN_EXC );
                        e.getStackTrace();
                        tcpServerSetBack();
                        continue;
                    }
                    try{
                        Thread serverThread = new Thread( tcpServer );
                        serverThread.start(); //calls run() implemented in  TCPServer Class
                        runsAsServer = true;
                    } catch (Exception e){
                        System.err.println( "Error during initialisation of ServerThread in ChatUI-SubSystem" );
                        tcpServerSetBack();
                        continue;
                    }

                    break;

                case (CMD_CONNECT):
                    String serverName;

                    try {
                        if (st.countTokens() < 2) {
                            throw new IllegalArgumentException();
                        } else {
                            try {
                                serverName = st.nextToken();
                                port = Integer.parseInt( st.nextToken() );
                            } catch (Exception e) {
                                System.err.println( "ChatUI-ERROR: unable to assign UserInput to ServerName or parse portNumber;" );
                                continue;
                            }
                        }
                    } catch (IllegalArgumentException missing_paramString) {
                        System.err.println( "ChatUI-ERROR: please specify the connection command by providing " +
                                "the name of the server you want to connect to and its portNumber, " +
                                "following this structure: <<con> <ServerName> <port_number>>\n" );
                        continue;
                    }

                    try {
                        tcpClient = new TCPClient(ps);
                        tcpClient.connect( serverName, port );
                        tcpClientDOS = new DataOutputStream( tcpClient.getOutputStream() );
                        //tcpClientDIS = new DataInputStream( tcpClient.getInputStream() );

                    } catch (UnknownHostException UHExc) {
                        System.err.print( ERR_CON_UNKNOWN_HOST );
                    } catch (IOException IOEx) {
                        System.err.println( ERR_CON_IO_EXC );
                    } catch (SecurityException SecExc) {
                        System.err.print( "ChatUI-ERROR: security software has interfered with attempt to establish a connection" );
                    } catch (IllegalArgumentException IAExc) {
                        System.err.println( ERR_OPEN_SEC_EXC );
                    } catch (Exception e) {
                        System.err.print( "ChatUI-ERROR: Socket-related malfunction occurred whilst trying to use connect() form ChatUI SubSystem" );
                        e.printStackTrace();
                    }

                    try{
                        Thread clientThread = new Thread( tcpClient );
                        clientThread.start();
                        runsAsClient = true;
                    } catch (Exception e){
                        System.err.println( "Error during initialisation of ServerThread in ChatUI-SubSystem" );
                        tcpClientSetBack();
                        continue;
                    }
                    break;

                case (CMD_DISCONNECT):

                    if (!runsAsServer && !runsAsClient){
                        System.err.println( "No connection has been established yet." );
                        continue;
                    }

                    //https://stackoverflow.com/questions/10240694/java-socket-api-how-to-tell-if-a-connection-has-been-closed#10240832
                    try {
                        if (runsAsServer){
                            tcpServerDOS.writeUTF( "The Server has closed the connection, Socket will be closed" );
                            tcpServerDOS.writeUTF( CMD_DISCONNECT );
                            tcpServerSetBack();
                            ps.println("SocketStatus = " + tcpServer.socketStatus());
                        }
                        if (runsAsClient){
                            tcpClientDOS.writeUTF( "The Client has withdrawn from connection, Socket will be closed" );
                            tcpClientDOS.writeUTF( CMD_DISCONNECT );
                            tcpClientSetBack();
                            ps.println("SocketStatus = " + tcpClient.socketStatus());
                        }
                    } catch (Exception e) {
                        System.err.println( "ERROR occurred whilst trying to close open socket.\n" + e );
                    }
                    break;

                case (CMD_EXIT):
                    reading = false;
                    ps.println( ChatUI.MSG_SUCCESSFUL_SHUTDOWN );

                    try {//CLOSE ALL THE STREAMS
                        if (userInputBR != null) {
                            userInputBR.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                        if (runsAsServer){
                            tcpServerSetBack();
                        }
                        if (runsAsClient){
                            tcpClientSetBack();
                        }
                    } catch (Exception e) {
                        System.err.println( "ChatUI-ERROR: while closing streams of ChatUI SubSystem occurred\n" + e );
                    }
                    break;

                default:
                    try{
                        System.err.println( "unable to identify command, your Input: " + userInput );
                        throw new IllegalArgumentException();
                    } catch (IllegalArgumentException IAExc) {
                        ps.println( ChatUI.MSG_LIST_COMMANDS);
                        //continue;
                    }
                }
            }
        }

    private void tcpClientSetBack() {
        runsAsClient = false;
        try {
            tcpClient.close();
        } catch (Exception e) {
            System.err.println( "Error occurred whilst trying to close the ClientSocket or Streams related to it." );
            e.printStackTrace();
        }
    }

    private void tcpServerSetBack() {
            runsAsServer = false;
            try {
                tcpServer.close();
            } catch (Exception e) {
                System.err.println( "Error occurred whilst trying to close the ServerSocket or Streams related to it." );
                e.printStackTrace();
            }
        }

    private String sT2String(StringTokenizer userParameterInput){
        StringBuilder sb = new StringBuilder();

        //rebuild parameterString from what is remaining in StringTokenizer
        while (userParameterInput.hasMoreTokens()) {
            try {
                sb.append(userParameterInput.nextToken() + " ");
            } catch (Exception e) {
                System.err.println("ERROR: malfunction during building ParameterString in writeToFile() in ChatSubSystem.\n");
            }
        }
        return sb.toString();
    }
}


