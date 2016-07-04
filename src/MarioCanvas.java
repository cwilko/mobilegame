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

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;


class MarioCanvas
    extends GameCanvas
    implements Runnable
{
    // shared direction constants
    static final int NONE = -1;
    static final int UP = 0;
    static final int LEFT = 1;
    static final int DOWN = 2;
    static final int RIGHT = 3;

    private static final int MILLIS_PER_TICK = 40;
    
    private final SheepdogMIDlet midlet;
//   private final Handbag handbag;
    public static LayerManager layerManager;

    private final Graphics graphics;
    private long gameDuration;
    private long startTime;
    private volatile Thread animationThread = null;
    private Field field;
    private Mario mario;
    private int keyStates;
    private boolean upPressed = false;
    private boolean isDead;
    private Backdrop backdrop;
    private int oldCell;


    MarioCanvas(SheepdogMIDlet midlet)
    {

        super(true);   // suppress key events for game keys
        
        this.midlet = midlet;
        setFullScreenMode(true);
        graphics = getGraphics();

        layerManager = new LayerManager();
        try {
        field = new Field(this, layerManager);
        mario = new Mario(this, field);
        backdrop = new Backdrop(this);
        
        
        layerManager.append(mario);
        layerManager.append(field);
        layerManager.append(backdrop);
        

        init();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }


    public void keyPressed(int keyCode)
    {
        // The constructor suppresses key events for game keys, so we'll
        // only get key events for non-game keys. The number keys, * & #
        // have positive keyCodes, so negative keyCodes mean non-game
        // special keys like soft-keys. We'll use key-presses on special
        // keys to take us to the menu.
        if (keyCode < 0)
        {
            stop();
            midlet.sheepdogCanvasMenu();
        }
    }


    void init()
    {      
      isDead = false;
      oldCell = field.TRANSLATE_X;
    }
    

    public synchronized void start()
    {
        animationThread = new Thread(this);
        animationThread.start();

        startTime = System.currentTimeMillis() - gameDuration;
    }


    public synchronized void stop()
    {
        gameDuration = System.currentTimeMillis() - startTime;
        animationThread = null;
    }


    public void run()
    {
        Thread currentThread = Thread.currentThread();

        try
        {
            // This ends when animationThread is set to null, or when
            // it is subsequently set to a new thread; either way, the
            // current thread should terminate
            while (currentThread == animationThread)
            {
                long startTime = System.currentTimeMillis();
                // Don't advance game or draw if canvas is covered by
                // a system screen.
                if (isShown())
                {
                    if (isDead)
                      midlet.marioGameOver();                                        
                    
                    tick();
                    draw();
                    flushGraphics();
                }
                long timeTaken = System.currentTimeMillis() - startTime;
                if (timeTaken < MILLIS_PER_TICK)
                {
                    synchronized (this)
                    {
                        wait(MILLIS_PER_TICK - timeTaken);
                    }
                }
                else
                {
                    currentThread.yield();
                }
            }
        }
        catch (Exception e)
        {
          e.printStackTrace();
            // won't be thrown
        }
    }


    private void tick()
    {      
        // If player presses two or more direction buttons, we ignore them
        // all. But pressing fire is independent. The code below also ignores
        // direction buttons if GAME_A..GAME_D are pressed.
        keyStates = getKeyStates();
        // Update dy
        int dY = mario.updateY(!upPressed, (keyStates & UP_PRESSED) != 0); 
        
        // Cancel any repeating key-presses
        if ((keyStates & UP_PRESSED) != 0)
          upPressed = true;
        else
          upPressed = false;  
        
        // Update dx and scroll        
        int dX = mario.updateX(keyStates);
        
        dX = mario.checkBounds(dX, dY);                   
                
        // Scroll backgrounds      
        // TODO : Rework this
        if (field.getX() - oldCell >= 4) 
          {
            backdrop.move(1,0);
            oldCell = oldCell + 4;
          }
        if (field.getX() - oldCell <= -4) 
        {
          backdrop.move(-1,0);
          oldCell = oldCell - 4;
        }
        
        if (field.tick(-(dX), 0, upPressed))
          isDead = true;                                  
    }



    void vibrate(int millis)
    {
        midlet.vibrate(millis);
    }

    
    // draw game
    private void draw()
    {
        int width = getWidth();
        int height = getHeight();

        // clear screen to grey
        graphics.setColor(0x0000CCFF);
        graphics.fillRect(0, 0, width, height);


        // draw background and sprites
        layerManager.paint(graphics, 0, 0);


        // display time & score
        graphics.setColor(0x00FFFFFF); // white
       // graphics.drawString(""+graphics.getClipX()
       //     ,10,10,0);

    }
    

    
}
