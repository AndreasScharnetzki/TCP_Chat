package interfaces;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public interface Chat {

    /**triggered via SAVE_CMD as CommandString, used to write parameterString to ChatLogFile;
     *
     * !calls searchForFile to locate path of the ChatLogFile!
     *
     * @param payload = userInput without command
     * @throws Exception due to unauthorized or failed attempt to create or access or change the log.file ("chatLog.txt")
     *                   or whilst trying to close the Streams opened by this method
     */
    void writeToFile (String payload) throws Exception;

    /**triggered via DISPLAY_CMD as CommandString, used to read full content of ChatLogFile using PrintStream of ChatUI SubSystem for display;
     *
     * !calls searchForFile to locate path of the ChatLogFile!
     *
     * @param ps reference for the outputstream (sys.out) declared in ChatUI-SubSystem
     * @throws Exception couldn't access/read from Logfile
     */
    void readFromFile (PrintStream ps) throws Exception;

    /**!if boolean testing of TestRunApplication is true, this function won't be used!
     *
     * @return timeStamp meant to be put before every message written into the ChatLogFile
     * @throws Exception due to not being able to find/access/write to the file
     */
    byte[] timeStamp() throws Exception;

    /** creates a logfile in project folder/src
     *
     * @param fileName of logfile
     * @return location of the logfile.txt as String
     * @throws Exception due to not being able creating the Logfile (memory allocation problems, lack of write permission in project directory)
     */
    String createLogFile (String fileName) throws Exception;

    /**searching for a File with given name in project folder/src
     * @param fileName
     * @return reference to file as string
     * @throws FileNotFoundException
     */
    String searchForFile(String fileName) throws FileNotFoundException;

}
