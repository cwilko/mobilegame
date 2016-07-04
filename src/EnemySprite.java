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


class EnemySprite
    extends Sprite implements GameSprite
{
    static final int WIDTH = 12;
    static final int HEIGHT = 12;
    static final int VIBRATION_MILLIS = 200;
    
    private final int MIN_X_DEACTIVATE = Field.TILE_WIDTH * -20;
    private final int MAX_X_DEACTIVATE = Field.TILE_WIDTH * 20;

    private int RIGHT;
    private int LEFT;
    private int frameSpeed;
    private int anim = 0;
    private Field field;
    private int dx = 1;
    private int TOP;
    private int dieStep = 4;
    private int initY;
    private int initX;
    private boolean init;
    private SpriteList spriteNode;
    private int initLEFT;
    private int initRIGHT;
    private int initTOP;
    private int range;

    EnemySprite(MarioCanvas canvas, Field field, int x, int y)
    {
      super(SheepdogMIDlet.createImage("/koopa2.png"), WIDTH, HEIGHT);              
      this.field = field;
      initLEFT = LEFT = x<<4;
      initRIGHT = RIGHT = LEFT+WIDTH;
      initTOP = TOP = y;
      initY = canvas.getHeight() - (Field.HEIGHT_IN_TILES<<4) + ((y+1)<<4)-HEIGHT;
      initX = LEFT + field.TRANSLATE_X;
            
      defineReferencePixel(WIDTH/2,HEIGHT/2);     

    }

    public void tick(int x, int y)
    {        
      
      if (spriteNode.isAlive)
      {
        // Update frames
        if (frameSpeed-- == 0) {
          frameSpeed = 3;
          setFrame(anim ^= 1);
        }           
              
        // TODO : Only detect in the direction we are travelling
        if (field.getCell(getRightCell(), TOP) != 0 ||
            field.getCell(getLeftCell(), TOP) != 0 ||
            field.getCell(getRightCell(), TOP+1) == 0 || 
            field.getCell(getLeftCell(), TOP+1) == 0)
        {
          dx = -(dx);
          if (dx < 0) setTransform(TRANS_MIRROR);
          else
            setTransform(TRANS_NONE);
        }
        
        int totMove = dx+x;        
        move(totMove,y);
        RIGHT+=dx;
        LEFT+=dx;
        range+=totMove;
        
        if (range > MIN_X_DEACTIVATE & range < MAX_X_DEACTIVATE) return;
        spriteNode.isAlive = false;
      }
      else  // Dead
      {
        move(dx+2, y+Mario.JumpStates[dieStep++]);
        if (dieStep < Mario.JumpStates.length) return;
        field.deactivate(spriteNode);
        MarioCanvas.layerManager.remove(this);

        // Reset object for next time
        dieStep = 4;
        dx = 1;
        setTransform(TRANS_NONE);
        LEFT = initLEFT;
        RIGHT = initRIGHT;
        TOP = initTOP;
        range = 0;
      }

    }

    // TODO : INLINE THESE CALLS
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
      return TOP;
    }

    /**
     * 
     */
    public void kill() {
      spriteNode.isAlive = false;
      setTransform(TRANS_ROT180);
      
    }
    
    public void init(int dX)
    {
      // TODO : move the init back into calling method
      spriteNode.isAlive = true;
      range = initX - dX;
      setPosition(range, initY);
      MarioCanvas.layerManager.insert(this,0);      
    }

    /* (non-Javadoc)
     * @see GameSprite#setNode(SpriteList)
     */
    public void setNode(SpriteList node) {
      this.spriteNode = node;      
    }


}
