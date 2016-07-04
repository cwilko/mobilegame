//Copyright 2003 Nokia Corporation.
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

import javax.microedition.lcdui.game.*;


class Mario
    extends Sprite
{
    static final int WIDTH = 12;
    static final int HEIGHT = 12;
    static final int TILE_WIDTH = Field.TILE_WIDTH;
    static final int TILE_HEIGHT = Field.TILE_HEIGHT;
    static final int SHIFTx4 = 4;
    static final int VIBRATION_MILLIS = 200;
    static final int NO_ACCEL = 9;
    static final int BOTH_PRESSED = GameCanvas.LEFT_PRESSED | GameCanvas.RIGHT_PRESSED;
    static final int INVINCIBLE_TICKS = 100;
    
    static final int [] SpeedStates = {-3,-3,-3,-3,-2,-2,-1,-1,-1,0,1,1,1,2,2,3,3,3,3};
    public static final int [] JumpStates = {-6,-6,-6,-5,-4,-4,-3,-2,-1,-1,0,0,0,1,1,2,3,4,4,5,6,6,6};
    static final int MAX_JUMP = JumpStates.length - 2;
    
    private int RIGHT;
    private int LEFT;
    private int TOP;
    private int BOTTOM;
    private int SPEED = 0;
    private int accel = NO_ACCEL;

    private final Field field;
    private int anim = 1;
    private int jumpStep = JumpStates.length - 1;
    private boolean facingFwd = true;
    private boolean bothPressed = false;
    private int frameSpeed;
    private boolean inAir = true;
    private int actualDx;
    private int actualDy;
    private boolean hitTopWall;
    private boolean hitSide;
    private boolean hasWeapon = true;
    private int deathTicks = 0;


    Mario(MarioCanvas canvas, Field field)
    {
        super(SheepdogMIDlet.createImage("/mario2.png"), WIDTH, HEIGHT);
        defineCollisionRectangle(2, 2, WIDTH-4, HEIGHT-2);
        
        // Define mario boundary
        RIGHT = TILE_WIDTH * (4 + field.TRANSLATE_CELL_X) - 4;
        LEFT  = TILE_WIDTH * (3 + field.TRANSLATE_CELL_X) + 4;
        TOP   = TILE_HEIGHT * 2;
        BOTTOM = TILE_HEIGHT * 2 + HEIGHT;
 
        field.setSpriteX(RIGHT);
        
        setPosition(LEFT - 2 + field.TRANSLATE_X,TOP + (canvas.getHeight() - Field.HEIGHT_IN_TILES * TILE_HEIGHT));
        defineReferencePixel(WIDTH/2, HEIGHT/2);
        this.field = field;
        field.setMainSprite(this);
    }


    public int updateX(int keyStates)
    {        
      int moveX = 0;  
      
      if ((keyStates & BOTH_PRESSED) == 0)
      {
        setFrame(anim = 0);
        if (accel != NO_ACCEL) 
          decrSpeed();         
      }
      
      if ((keyStates & BOTH_PRESSED) == BOTH_PRESSED)
      {
        if (!bothPressed)
        {
          if (facingFwd)
            keyStates = GameCanvas.LEFT_PRESSED;
          else
            keyStates = GameCanvas.RIGHT_PRESSED;
          bothPressed = true;
        }
        else
        {
          if (facingFwd)
            keyStates = GameCanvas.RIGHT_PRESSED;
          else
            keyStates = GameCanvas.LEFT_PRESSED;
        }
      }
      else
        bothPressed = false;
      
      if (((keyStates & GameCanvas.RIGHT_PRESSED) != 0 && incSpeed(true)) ||
                ((keyStates & GameCanvas.LEFT_PRESSED) == 0 && accel != NO_ACCEL && facingFwd))
      {       
        if (!facingFwd)
        {
          facingFwd = true;
          setTransform(TRANS_NONE);
        }
        moveX = SPEED;        
      }
      else if (((keyStates & GameCanvas.LEFT_PRESSED) != 0 && incSpeed(false)) ||
               ((keyStates & GameCanvas.RIGHT_PRESSED) == 0 && accel != NO_ACCEL && !facingFwd))
      {      
        if (facingFwd)
        {
          facingFwd = false;
          setTransform(TRANS_MIRROR);
        }
        moveX = SPEED;
      }
      
      if ( moveX != 0 )
      {
        RIGHT+=moveX;
        LEFT+=moveX;
        
        // Update frames
        if (frameSpeed-- == 0) {
          frameSpeed = 4-SPEED;
          setFrame(anim = (anim == 0 ? 1 : 0));
        }
      }
      else
        setFrame(anim = 0);
      
      if (deathTicks == 0) return moveX;
      
      // Invincible
      setVisible(isVisible()^true); // TODO : use boolean
      deathTicks--;
      
      return moveX;        
    }


    /**
     * 
     */
    private boolean incSpeed(boolean forward) { 
      if (forward)
      {
        if (accel < SpeedStates.length - 1) 
          SPEED = SpeedStates[accel++];
      }
      else
      {
        if (accel > 0) 
          SPEED = SpeedStates[accel--];
      }
      return true;
    }
    
    /**
     * 
     */
    private void decrSpeed() {   
      if (accel > NO_ACCEL)
        SPEED = SpeedStates[accel--];
      else if (accel < NO_ACCEL)
        SPEED = SpeedStates[accel++];
    }


    /**
     * @return
     */
    public int getBottomCell() {
      return (BOTTOM-1) >> SHIFTx4;
    }


    /**
     * @returnb
     */
    public int getRightCell() {
      // TODO Auto-generated method stub
      return (RIGHT-1) >> 4;
    }
    
    /**
     * @returnb
     */
    public int getLeftCell() {
      // TODO Auto-generated method stub
      return (LEFT) >> 4;
    }
    
    /**
     * @returnb
     */
    public int getTopCell() {
      // TODO Auto-generated method stub
      return (TOP) >> 4;
    }


    /**
     * @return
     */
    public int getRightTileDx() {
      // Distance to next tile in pixels
      return RIGHT - ((RIGHT >> SHIFTx4) << SHIFTx4);
    }
    
    /**
     * @return
     */
    public int getLeftTileDx() {
      // Distance to previous tile in pixels
      return LEFT - ((LEFT >> SHIFTx4) << SHIFTx4);
    }
    
    /**
     * @return
     */
    public int getBottomTileDx() {
      // Distance to previous tile in pixels
      return BOTTOM - ((BOTTOM >> SHIFTx4) << SHIFTx4);
    }
    
    /**
     * @return
     */
    public int getTopTileDx() {
      // Distance to previous tile in pixels
      return TOP - ((TOP >> SHIFTx4) << SHIFTx4);
    }

    /**
     * @param i
     */
    public int updateY(boolean upChanged, boolean upPressed) {
      int moveY = 0;
            
      if (!inAir & upChanged & upPressed)
      {
        jumpStep = 0;
        moveY = JumpStates[jumpStep];
        inAir = true;
      }     
      else // Hit a wall from beneath
        if (hitTopWall)
        {
          moveY = JumpStates[jumpStep=11];
          hitTopWall = false;
        }
      else
        moveY = JumpStates[jumpStep];      
      
      // Analogue jumping
      if (jumpStep < MAX_JUMP )
      {
        if (!upPressed) jumpStep++;
        jumpStep++;
      }
      
      TOP+=moveY;
      BOTTOM+=moveY;
      
      return moveY;
    }

    /**
     * @param dx
     * @param dy
     * @return
     */
    public int checkBounds(int dx, int dy) {
      
      actualDx = 0;
      actualDy = 0;
      
      // Check Y boundaries
      if (dy > 0) // check below
      { 
        if (field.getCell(getRightCell() ,getBottomCell()) != 0 || 
            field.getCell(getLeftCell()  ,getBottomCell()) != 0)
        {
          actualDy=getBottomTileDx();
          if (actualDy > dy) 
            actualDy = 0;
          else
          if ((field.getCell(getRightCell() ,getTopCell()) != 0 && field.getCell(getLeftCell()  ,getBottomCell()) == 0) ||
              (field.getCell(getLeftCell()  ,getTopCell()) != 0 && field.getCell(getRightCell() ,getBottomCell()) == 0))
          {
            actualDy = 0;
          }
          else
          {
            TOP-=actualDy;                     
            BOTTOM-=actualDy;
            inAir = false;
          }
        }
        else
          inAir = true;
      }
      else if (dy < 0) // check above
      {
        if (field.getCell(getRightCell() ,getTopCell()) != 0 || 
            field.getCell(getLeftCell()  ,getTopCell()) != 0)
        {
          actualDy=getTopTileDx()-TILE_HEIGHT;
          if (actualDy < dy || hitSide) 
            actualDy = 0;
          else
          {
            hitTopWall = true;
            TOP-=actualDy;            
            BOTTOM-=actualDy;
          }
        }
        else
          hitTopWall = false;
      }
      
      // Check X boundaries
      if (dx > 0) // check right
      { 
        if (field.getCell(getRightCell() ,getBottomCell()) != 0 || 
            field.getCell(getRightCell()  ,getTopCell()) != 0)
        {
          actualDx=getRightTileDx();
          RIGHT-=actualDx;
          LEFT-=actualDx;
          hitSide = true;
        }    
        else
          hitSide = false;
      }
      else if (dx < 0) // check left
      {
        if (field.getCell(getLeftCell() ,getBottomCell()) != 0 || 
            field.getCell(getLeftCell()  ,getTopCell()) != 0)
        {
          actualDx=getLeftTileDx()-TILE_WIDTH;
          RIGHT-=actualDx;
          LEFT-=actualDx;
          hitSide = true;
        }
        else
          hitSide = false;
      }
                  
      move(0,dy-actualDy);        
            
      return dx-actualDx;
    }


    /**
     * @param cat
     * @return
     */
    public boolean checkSpriteCollision(GameSprite sprite, boolean upPressed) {
      // TODO : Can we do our own collision detection?
      // TODO : Remove cast
      if (deathTicks>0 || !collidesWith((Sprite)sprite, false))
        return false;
      else
      {
        if (getTopCell() != sprite.getTopCell())
        {
          sprite.kill();
          // Jump
          if (upPressed) jumpStep = 0; 
          else jumpStep = 3;
          inAir = true; 
          return false;
        }
        else  
        {
          deathTicks = INVINCIBLE_TICKS;
          return hasWeapon ^= true;
        }
      }
    }


    /* (non-Javadoc)
     * @see SpriteList#tick(int, int)
     */
    public void tick(int x, int y) {
      // no-op      
    }


    /* (non-Javadoc)
     * @see SpriteList#kill()
     */
    public void kill() {
      // no-op      
    }


    /* (non-Javadoc)
     * @see SpriteList#init(int)
     */
    public void init(int dX) {
      // no-op      
    }


    /* (non-Javadoc)
     * @see GameSprite#setNode(SpriteList)
     */
    public void setNode(SpriteList list) {
      // TODO Auto-generated method stub      
    }

}
