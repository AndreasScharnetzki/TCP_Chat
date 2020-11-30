package implementation;

import interfaces.Chat;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

public class ChatImpl implements Chat {

    private String logFilePath = null;
    TestRunApplication testRunApplication = new TestRunApplication();

    public void readFromFile(PrintStream ps) throws Exception {

        try{
            this.logFilePath = searchForFile("chatLog.txt");
        } catch (FileNotFoundException FNFExc){
            throw FNFExc; //Will get caught in ChatUIImpl.java
        }

        //passing LogFile to wrapped input streams
        BufferedReader br = null;
        String temp;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(logFilePath)));
        }catch (Exception e){
            System.err.println("ERROR: passing LogFile to FileInputStream to InputStreamReader to BufferedReader in readFromFile() in ChatSubSystem");
        }

        //reading from File line by line
        try {
            while ((temp = br.readLine()) != null) {
                ps.println(temp);
            }
        }catch (IOException IOExc){
            System.err.println("ERROR: malfunction in br.readLine() in readFromFile() in ChatSubSystem - BufferedReader reached EOF");
        }

        //Closing Stream
        try {
            if (br != null) {
                br.close();
            }
        } catch (Exception closeError) {
            System.err.println("ERROR: occurred whilst trying to close streams in readFromFile() of ChatSubSystem.\n");
        }
    }

    public void writeToFile(String payload) throws Exception {

        //this block checks if a logFile is already existent, if not it will try to create a new logFile in ProjectFolder/src
        try{
            if(logFilePath == null){
                this.logFilePath = searchForFile("chatLog.txt");
            }
            //TODO Check for WritePermission here
        } catch (FileNotFoundException FNFExc){
            try {
                this.logFilePath = createLogFile("chatLog.txt");
                System.out.println("A new chatLogFile has been successfully created, located at: " + logFilePath + "\n");
            }catch (Exception e){
                System.err.println("ERROR: unable to create new LogFile via createLogFile() called from writeToFile() in ChatSubSystem.");
            }
        }

        DataOutputStream dos = new DataOutputStream(new FileOutputStream(this.logFilePath, true));

        //Writing to the chatLogFile via DataOutPutStream via FileOutPutStream from StringBuilder
        try {
            //{dos.write(timeStamp());} //EyeCandy
            dos.writeUTF(payload + System.lineSeparator());
        } catch (Exception e) {
            System.err.println("ERROR: during usage of DataOutputStream in writeToFile() ChatSubSystem.\n" + e);
        }
        //Closing Streams
        try {
            if (dos != null) {
                dos.close();
            }
        } catch (Exception e) {
            System.err.println("ERROR: occurred whilst trying to close streams of ChatSubSystem.\n" + e);
        }
    }

    public byte[] timeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df;
        df = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
        String timeStamp = now.format(df);
        byte[] ts = timeStamp.getBytes();
        return ts;
    }

    public String createLogFile(String fileName) {

        String filePath = null;
        try {
            filePath = new File(fileName).getAbsolutePath();
        } catch (Exception e) {
            System.err.println("ERROR: unable to create a ChatLogFile in createLogFile () in ChatSubSystem.\n");
        }
        return filePath;
    }

    public String searchForFile(String fileName) throws FileNotFoundException, NullPointerException {
        File projectPath = new File(System.getProperty("user.dir"));
        for (File f : projectPath.listFiles()) {
            if (f.getName().contains(fileName) == true) {
                return f.getAbsolutePath();
            }
        }
        throw new FileNotFoundException();
    }
}

