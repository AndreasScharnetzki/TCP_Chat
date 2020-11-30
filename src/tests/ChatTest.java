package tests;

import implementation.ChatImpl;
import implementation.ChatUIImpl;
import implementation.TestRunApplication;
import interfaces.Chat;
import interfaces.ChatUI;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class ChatTest {

    //constructor
    Chat createChatMachine() {return new ChatImpl();}
    ChatUI createChatUI() {return new ChatUIImpl();}

    FileInputStream fis;
    OutputStream os = System.out;

    //##################################################################################################################
    //#                                  BAD TESTS                                                                     #
    //##################################################################################################################

    @Test(expected = FileNotFoundException.class)
    public void badTest01_searchForNoneExistingFile () throws FileNotFoundException {
        Chat cm = this.createChatMachine();
        cm.searchForFile("noneExistentFile.txt");
    }

    @Test(expected = FileNotFoundException.class)
    public void badTest02_callingReadFirst () throws Exception {
        Chat cm = this.createChatMachine();
        ChatUI chatUI = this.createChatUI();

        try {
            File fileToBeDeleted = new File(cm.searchForFile("chatLog.txt"));
            fileToBeDeleted.delete();
        } catch (Exception e){
            System.err.println("Unable to delete or locate chatLogFile");
        }
        String readCMD =  "read";
        ByteArrayInputStream bais = new ByteArrayInputStream(readCMD.getBytes());

        chatUI.runUI( bais, os );
    }

    //Checks for Exception due to logfile related access denial (Admin Rights/Wrong Password ect.)
    @Test(expected = Exception.class)
    public void badTest03_tryToWriteToProtectedFile () throws Exception {
        Chat cm = this.createChatMachine();

        //delete oldChatLog to start with a file that will be created just this instant
        try {
            File fileToBeDeleted = new File(cm.searchForFile("chatLog.txt"));
            fileToBeDeleted.delete();
        } catch (FileNotFoundException e){
            System.err.println("ERROR Unable to delete or locate chatLogFile");
            e.printStackTrace();
        }

        File readOnly = new File("chatLog.txt.txt");
        readOnly.setReadOnly();

        ChatUI chatUI = this.createChatUI();

        String writeCMD =  "write something";
        ByteArrayInputStream bais = new ByteArrayInputStream(writeCMD.getBytes());
        try {
            chatUI.runUI( bais, os );
        } catch (NullPointerException NPExc){}

    }

    //TODO--------------------------------------------------------------------------------------------------------------
    //Checks for Exception due to missing write-permission
    @Test(expected = Exception.class)
    public void badTest0x_writeToLogFileNoPermission  (){

    }

    @Test(expected = Exception.class)
    public void badTest0x_deleteLogFileNoPermission (){
        //check for throw of exception if logfile is protected
    }

    //##################################################################################################################
    //#                                  GOOD TESTS                                                                    #
    //##################################################################################################################

    @Test //TODO be aware, this function only checks if the logfile contains at least the message written in validCommandsWriteOnly, it does not exclude the possibility of additional content!
    public void goodTest01_validCommandWrite_Content () throws Exception {

        Chat cm = createChatMachine();
        ChatUI objectReference = this.createChatUI();

        //delete oldChatLog to start with a file that will be created just this instant
        try {
            File fileToBeDeleted = new File(cm.searchForFile("chatLog.txt"));
            fileToBeDeleted.delete();
        } catch (FileNotFoundException e){
            System.err.println("ERROR Unable to delete or locate chatLogFile");
            e.printStackTrace();
        }

        fis = new FileInputStream(cm.searchForFile("TestFile_validCommandsWriteOnly.txt"));
        try {
            objectReference.runUI( fis, os );
        }catch (NullPointerException NPExc){
            //The program needs to be terminated this way so file content can be checked
        }

        FileInputStream logFileReference = new FileInputStream(cm.searchForFile("chatLog.txt"));
        //Check if content of Logfile equals parameter string
        Assert.assertTrue(logFileReference.toString().contains("hello" + System.lineSeparator() + "world"));
    }

    //FIXME readAllBytes
    /*
    @Test
    public void goodTest02_validCommandWrite_CharSum () throws Exception {
        Chat cm = createChatMachine();
        ChatUI objectReference = this.createChatUI();

        //delete oldChatLog to start with a file that will be created just this instant
        try {
            File fileToBeDeleted = new File(cm.searchForFile("chatLog.txt"));
            fileToBeDeleted.delete();
        } catch (FileNotFoundException e){
            System.err.println("ERROR Unable to delete or locate chatLogFile");
            e.printStackTrace();
        }

        fis = new FileInputStream(cm.searchForFile("TestFile_42Chars.txt"));

        try {
            objectReference.runUI( fis, os );
        }catch (Exception NPExc){
            //The program needs to be terminated this way so file content can be checked
        }

        String stringOf42Chars = "wowwhowouldhavethoughtthatihavetocheckthis";
        byte[] testStringAsBytes = stringOf42Chars.getBytes();

        FileInputStream chatLog = new FileInputStream(cm.searchForFile("chatLog.txt"));
        byte[] contentOfLogFileAsBytes = chatLog.readAllBytes();
        //Check if number of characters matches with input
        Assert.assertEquals(testStringAsBytes.length, contentOfLogFileAsBytes.length);
    }
    */

    //TODO---------------------not yet implemented----------------------------------------------------------------------
    @Test(expected = FileNotFoundException.class)
    public void goodTest0x_deletingChatLogFileSuccessfully () throws Exception {
        Chat cm = createChatMachine();
        ChatUI chatUI = this.createChatUI();

        try {
            fis = new FileInputStream(cm.searchForFile("TestFile_validCommandsWriteOnly.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            chatUI.runUI( fis, os );
        }catch (Exception NPExc){
            //The program needs to be terminated this way so file content can be checked
        }

        String deleteCMD =  "del Y";
        ByteArrayInputStream bais = new ByteArrayInputStream(deleteCMD.getBytes());

        chatUI.runUI( bais, os );

        cm.searchForFile( "chatLog.txt" );
    }
}
