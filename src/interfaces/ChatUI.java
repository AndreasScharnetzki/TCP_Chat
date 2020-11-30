package interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ChatUI {

    //C o m m a n d L i s t
        String CMD_EXIT         = "exit";
        String CMD_WRITE        = "write";
        String CMD_DISPLAY      = "read";
        String CMD_CONNECT      = "con";     //opens a ClientSocket,
        String CMD_OPEN         = "open";    //opens a ServerSocket, awaiting incoming connection requests
        String CMD_DELETE       = "del";
        String CMD_DISCONNECT   = "disco";    //leaving chat loop

    //E r r o r s    &    M e s s a g e s

    //reading
        String ERR_Reading = "ChatUI-ERROR: Couldn't read InputStream in ChatUISubSystem.\n";

    //EXIT
        String MSG_SUCCESSFUL_SHUTDOWN = "The Chat application will shut down now.\n";
    //SAVE
        String ERR_INVALID_WRITE_CALL01 = "ChatUI-ERROR: write command needs to be followed by a parameter, <<write> <parameter>>";
    //DISPLAY
        String ERR_DISPLAY_EXC = "ChatUI-ERROR: Malfunction whilst calling chatMachine's readFromFile() in ChatUISubSystem";
        String ERR_DISPLAY_FNF_EXC =    "ChatUI-ERROR: Couldn't locate chatLogFile in src-folder.\n" +
                                        "Make sure file is existent and located at project folder/src or check for read permission.\n" +
                                        "Consider using <<write> <input>> which will create a new Logfile.\n";

    //CONNECT
        String ERR_CON_UNKNOWN_HOST = "ChatUI-ERROR: the serverName you were trying to connect to turned out to be unknown, make sure ServerName is the correct reference";
        String ERR_CON_IO_EXC = "ChatUI-ERROR: I/O-Exception occurred whilst trying to connect to Server" +
                                "\nindicating there is a problem creating or accessing the socket described above.";

    //OPEN
        String MSG_SUCCESSFULL_CON = "Server accepted your request, connection established";
        String MSG_PORT_LOCATOR = "Accepted incoming request @port: ";

        String ERR_OPEN_IO_EXC  = "ChatUI-ERROR: I/O-Exception occurred whilst ServerSocket was waiting for incoming requests" ;
        String ERR_OPEN_SEC_EXC = "ChatUI-ERROR: security software has interfered with attempt to establish a connection"  ;
        String ERR_OPEN_EXC     = "ChatUI-ERROR: ServerSocket-related malfunction occurred whilst trying to use open() form ChatUI SubSystem";
        String ERR_OPEN_ILLEGAL_ARG_EXC =   "ChatUI-ERROR: port parameter is outside the specified range of valid port values, " +
                                            "which is between [0, 65535], although you might wanna try a number greater than 1024";
    //DELETE
    //DEFAULT
        String MSG_LIST_COMMANDS = "please choose from valid commands: "
                                    //+ "<" + CMD_DELETE + "> "
                                    + "<" + CMD_EXIT + "> "
                                    + "<" + CMD_WRITE + "> "
                                    + "<" + CMD_DISPLAY + "> "
                                    + "<" + CMD_OPEN + "> "
                                    + "<" + CMD_CONNECT + "> ";

    /**
     *
     * Initiates an I/O stream connection to interact with User
     *
     * Splits UserInput into CommandString and ParameterString
     * crawls for valid command, passes ParameterString to ChatSubSystem
     *
     * @param is KeyboardInput/FileInput for Testing purpose
     * @param os PrintStream (accessible from ChatSubSystem to pass method returns)
     * @throws IllegalArgumentException - if Input was empty or write-command lacks of parameters
     * @throws IOException - if BufferedReader cannot parse UserInput
     *         NullPointerException - while testing, if while(reading) has parsed all input and Buffered Reader reaches EOF
     *         Exception - malfunction during closing streams opened by ChatUISubSystem
     *
     */
    void runUI(InputStream is, OutputStream os) throws Exception;
}
