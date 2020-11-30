package tests;

import implementation.ChatImpl;
import implementation.ChatUIImpl;
import interfaces.Chat;
import interfaces.ChatUI;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class UITest {

    FileInputStream fis;
    OutputStream os = System.out;

    // constructor
    ChatUI createChatUI() {return new ChatUIImpl();}
    Chat createChatMachine() {return new ChatImpl();}

    //paths
    private String pathTestFile_invalidCommands;
    private String pathTestFile_validCommandsWriteOnly;

    {
        try {
            pathTestFile_invalidCommands = createChatMachine().searchForFile("TestFile_invalidCommands.txt");
            pathTestFile_validCommandsWriteOnly = createChatMachine().searchForFile("TestFile_validCommandsWriteOnly.txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //##################################################################################################################
    //#                                  BAD TESTS                                                                     #
    //##################################################################################################################

    @Test(expected = IllegalArgumentException.class)
    public void badTest02_invalidCMD () throws Exception {
        ChatUI objectReference = this.createChatUI();

        fis = new FileInputStream(pathTestFile_invalidCommands);
        objectReference.runUI( fis, os );
    }

    @Test
    public void badTest03_missingParameter () throws Exception {
        ChatUI chatUI = this.createChatUI();

        String invalidWriteCMD =  "write";

        ByteArrayOutputStream baos = new ByteArrayOutputStream(  );
        DataOutputStream dos = new DataOutputStream( baos );
        dos.writeUTF( invalidWriteCMD );

        byte[] inputBytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
        InputStream myInputStream = bais;

        ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();


        chatUI.runUI( myInputStream, myOutputStream );
        Assert.assertEquals( ">" + ChatUI.ERR_INVALID_WRITE_CALL01 +
                            System.lineSeparator() +
                            ">", myOutputStream.toString() );

//======dragons from here onwards=======================================================================================
        bais.reset();
        baos.reset();
        inputBytes = null;
        myOutputStream.reset();

        dos.writeUTF( "read" );
        inputBytes = baos.toByteArray();
        bais = new ByteArrayInputStream(inputBytes);
        myInputStream = bais;

        chatUI.runUI( myInputStream, myOutputStream );
        Thread.sleep(2000);                 //refering to the testThread here?
        Assert.assertEquals( ">BLABLABLA"+System.lineSeparator()+">", myOutputStream.toString() );
    }

    //##################################################################################################################
    //#                                  GOOD TESTS                                                                    #
    //##################################################################################################################

    @Test
    public void goodTest01_emptyInput () throws Exception {
        ChatUI chatUI = this.createChatUI();

        String emptyCMD =  System.lineSeparator();
        String cmdSymbol = ">>";

        InputStream myInputStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(  );
        DataOutputStream dos = new DataOutputStream( baos );
        dos.writeUTF( emptyCMD );
        byte[] inputBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
        myInputStream = bais;

        ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();

        chatUI.runUI( myInputStream, myOutputStream );
        //expects two > symbols:  1st one for running the console, 2nd one due an empty command
        Assert.assertEquals( cmdSymbol, myOutputStream.toString() );
    }

    @Test
    public void goodTest02_splitInputCorrectly () throws Exception {
    }
}