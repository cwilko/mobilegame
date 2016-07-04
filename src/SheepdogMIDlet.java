// Copyright 2003 Nokia Corporation.
//
// THIS SOURCE CODE IS PROVIDED 'AS IS', WITH NO WARRANTIES WHATSOEVER,
// EXPRESS OR IMPLIED, INCLUDING ANY WARRANTY OF MERCHANTABILITY, FITNESS
// FOR ANY PARTICULAR PURPOSE, OR ARISING FROM A COURSE OF DEALING, USAGE
// OR TRADE PRACTICE, RELATING TO THE SOURCE CODE OR ANY WARRANTY OTHERWISE
// ARISING OUT OF ANY PROPOSAL, SPECIFICATION, OR SAMPLE AND WITH NO
// OBLIGATION OF NOKIA TO PROVIDE THE LICENSEE WITH ANY MAINTENANCE OR
// SUPPORT. FURTHERMORE, NOKIA MAKES NO WARRANTY THAT EXERCISE OF THE
// RIGHTS GRANTED HEREUNDER DOES NOT INFRINGE OR MAY NOT CAUSE INFRINGEMENT
// OF ANY PATENT OR OTHER INTELLECTUAL PROPERTY RIGHTS OWNED OR CONTROLLED
// BY THIRD PARTIES
//
// Furthermore, information provided in this source code is preliminary,
// and may be changed substantially prior to final release. Nokia Corporation
// retains the right to make changes to this source code at
// any time, without notice. This source code is provided for informational
// purposes only.
//
// Nokia and Nokia Connecting People are registered trademarks of Nokia
// Corporation.
// Java and all Java-based marks are trademarks or registered trademarks of
// Sun Microsystems, Inc.
// Other product and company names mentioned herein may be trademarks or
// trade names of their respective owners.
//
// A non-exclusive, non-transferable, worldwide, limited license is hereby
// granted to the Licensee to download, print, reproduce and modify the
// source code. The licensee has the right to market, sell, distribute and
// make available the source code in original or modified form only when
// incorporated into the programs developed by the Licensee. No other
// license, express or implied, by estoppel or otherwise, to any other
// intellectual property rights is granted herein.


// unnamed package

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.util.*;
import java.io.*;


public class SheepdogMIDlet
    extends MIDlet
    implements Runnable
{
    private static final String RS_NAME = "BESTTIME";
    private MenuList menuList;
    private MarioCanvas marioCanvas;
    private boolean initDone = false;
    private static final Random random = new Random();
    private boolean hasBestTime = false;
    private long bestTime;


    public SheepdogMIDlet()
    {
    }

    
    public void startApp()
    {
        Displayable current = Display.getDisplay(this).getCurrent();
        if (current == null)
        {
            // first time we've been called
            Display.getDisplay(this).setCurrent(new SplashScreen(this));
        }
        else
        {
            if (current == marioCanvas)
            {
                marioCanvas.start();   // start its animation thread
            }
            Display.getDisplay(this).setCurrent(current);
        }
    }


    public void pauseApp()
    {
        Displayable current = Display.getDisplay(this).getCurrent();
        if (current == marioCanvas)
        {
            marioCanvas.stop();   // kill its animation thread
        }
    }


    public void destroyApp(boolean unconditional)
    {
        if (marioCanvas != null)
        {
            marioCanvas.stop();   // kill its animation thread
        }
    }


    private void quit()
    {
        destroyApp(false);
        notifyDestroyed();
    }


    public void run()
    {
        init();
    }


    private synchronized void init()
    {
        if (!initDone)
        {
            readRecordStore();
            //SoundEffects.getInstance();
            menuList = new MenuList(this);
            marioCanvas = new MarioCanvas(this);
            initDone = true;
        }
    }


    void splashScreenPainted()
    {
        new Thread(this).start();   // start background initialization
    }


    void splashScreenDone()
    {
        init();   // if not already done
        Display.getDisplay(this).setCurrent(menuList);
    }


    void menuListContinue()
    {
        Display.getDisplay(this).setCurrent(marioCanvas);
        marioCanvas.start();
    }


    void menuListNewGame()
    {
        marioCanvas.init();
        Display.getDisplay(this).setCurrent(marioCanvas = new MarioCanvas(this));
        marioCanvas.start();
    }


    void menuListInstructions()
    {
        // create and discard a new Instructions screen each time, to
        // avoid keeping heap memory for it when it's not in use
        Display.getDisplay(this).setCurrent(new InstructionsScreen(this));
    }


    void menuListHighScore()
    {
        // create and discard a new High Score screen each time, to
        // avoid keeping heap memory for it when it's not in use
        Display.getDisplay(this).setCurrent(new HighScoreScreen(this));
    }


    void menuListQuit()
    {
        quit();
    }


    void sheepdogCanvasMenu()
    {
        marioCanvas.stop();
        menuList.setGameActive(true);
        Display.getDisplay(this).setCurrent(menuList);
    }


    void marioGameOver()
    {
        marioCanvas.stop();
        menuList.setGameActive(false);
        Display.getDisplay(this).setCurrent(new GameOverScreen(this));
    }


    void gameOverDone()
    {
        Display.getDisplay(this).setCurrent(menuList);
    }


    void instructionsBack()
    {
        Display.getDisplay(this).setCurrent(menuList);
    }


    void highScoreBack()
    {
        Display.getDisplay(this).setCurrent(menuList);
    }


    // method needed by lots of classes, shared by putting it here
    static Image createImage(String filename)
    {
        Image image = null;
        try
        {
            image = Image.createImage(filename);
        }
        catch (java.io.IOException ex)
        {
            ex.printStackTrace();
        }
        return image;
    }


    // method needed by lots of classes, shared by putting it here
    static int random(int size)
    {
        return (random.nextInt() & 0x7FFFFFFF) % size;
    }


    // only the MIDlet has access to the display, so put this method here
    void flashBacklight(int millis)
    {
        Display.getDisplay(this).flashBacklight(millis);
    }


    // only the MIDlet has access to the display, so put this method here
    void vibrate(int millis)
    {
        Display.getDisplay(this).vibrate(millis);
    }


    long getBestTime()
    {
        return hasBestTime ? bestTime : -1;
    }


    boolean checkBestTime(long time)
    {
        if (!hasBestTime || (time < bestTime))
        {
            hasBestTime = true;
            bestTime = time;
            writeRecordStore();
            return true;
        }
        else
        {
            return false;
        }
    }


    private void readRecordStore()
    {
        hasBestTime = false;
        RecordStore rs = null;
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try
        {
            rs = RecordStore.openRecordStore(RS_NAME, false);
            byte[] data = rs.getRecord(1);
            bais = new ByteArrayInputStream(data);
            dis = new DataInputStream(bais);
            bestTime = dis.readLong();
            hasBestTime = true;
        }
        catch (IOException ex)
        {
            // hasBestTime will still be false
        }
        catch (RecordStoreException ex)
        {
            // hasBestTime will still be false
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (bais != null)
            {
                try
                {
                    bais.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (rs != null)
            {
                try
                {
                    rs.closeRecordStore();
                }
                catch (RecordStoreException ex)
                {
                    // no error handling necessary here
                }
            }
        }
    }


    // this will only be called when we have a best time
    private void writeRecordStore()
    {
        RecordStore rs = null;
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try
        {
            rs = RecordStore.openRecordStore(RS_NAME, true);
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            dos.writeLong(bestTime);
            byte[] data = baos.toByteArray();
            if (rs.getNumRecords() == 0)
            {
                // new record store
                rs.addRecord(data, 0, data.length);
            }
            else
            {
                // existing record store: will have one record, id 1
                rs.setRecord(1, data, 0, data.length);
            }
        }
        catch (IOException ex)
        {
            // just leave the best time unrecorded
        }
        catch (RecordStoreException ex)
        {
            // just leave the best time unrecorded
        }
        finally
        {
            if (dos != null)
            {
                try
                {
                    dos.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (baos != null)
            {
                try
                {
                    baos.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (rs != null)
            {
                try
                {
                    rs.closeRecordStore();
                }
                catch (RecordStoreException ex)
                {
                    // no error handling necessary here
                }
            }
        }
    }
}

