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


class Field
    extends TiledLayer
{
    public static final int WIDTH_IN_TILES = 100;
    public static final int HEIGHT_IN_TILES = 12;
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    
    public final int TRANSLATE_X;
    public final int TRANSLATE_CELL_X;

    private byte[][] cellTiles =
        {
        {-1,},
        {-1,},
        {-1,},
        {-1,},
        {-1,},
        {9,1,10,1,0,17,4,1,0,13,4,1,0,5,4,1,0,2,4,1,0,2,4,1,0,17,4,1,0,27,4,1,-1,},
        {5,1,7,1,0,12,4,1,0,20,8,1,10,1,0,50,4,1,-1,},
        {5,1,7,1,0,11,16,1,0,6,1,1,3,1,0,3,4,1,0,3,16,1,0,1,8,1,9,4,5,1,10,1,0,10,2,1,0,3,16,1,0,3,2,1,0,4,4,1,0,18,16,1,-1,},
        {5,1,7,1,0,10,1,1,2,1,3,1,0,8,4,1,0,3,8,1,9,4,5,5,7,1,0,11,1,1,2,5,3,1,0,23,1,1,2,1,3,1,0,11,4,1,-1,},
        {5,1,7,1,0,6,1,1,2,1,3,1,0,6,4,1,0,4,8,1,5,1,10,1,0,2,6,1,5,9,7,1,0,3,4,1,0,17,4,1,-1,},
        {5,1,7,1,0,14,16,1,0,2,16,1,0,1,8,1,5,3,10,1,0,1,6,1,5,9,7,1,0,1,16,1,0,1,4,1,0,1,16,1,0,1,4,1,0,49,16,1,-1,},
        {5,1,7,1,2,97,-1,},
        };
    
    private boolean isDead;
    private int RHSpriteX;
    private int LHSpriteX;
    private SpriteList activeSprites;
    private SpriteList inactiveSprites;
    private SpriteList inactivePtr;
    private SpriteList activePtr;
    private Mario mainSprite;




    Field(MarioCanvas canvas, LayerManager lm)
    {
        super(WIDTH_IN_TILES + (2 * (((canvas.getWidth() / 2) >> 4) + 1)),
              HEIGHT_IN_TILES,
              SheepdogMIDlet.createImage("/tile6.png"),
              TILE_WIDTH,
              TILE_HEIGHT);
        
        // Setup empty lists;
        activeSprites = activePtr = new SpriteList(null,0,false);
        inactiveSprites = inactivePtr = new SpriteList(null,0,false);
        inactiveSprites.close(inactiveSprites);
        
        // Get half a screen of tiles
        TRANSLATE_CELL_X = ((canvas.getWidth() / 2) >> 4) + 1;      
        TRANSLATE_X = -(TILE_WIDTH * TRANSLATE_CELL_X);

        int column = 0;
        int tileX = 0;
        for (int row = 0; row < HEIGHT_IN_TILES; ++row)
        {
          tileX = 0;
          column = 0;
          
          // Fill start tile buffer
          if (cellTiles[row][column] >= 0)
            fillCells(tileX, row, TRANSLATE_CELL_X,1,cellTiles[row][column]);    

          tileX+=TRANSLATE_CELL_X;

          while (cellTiles[row][column] >= 0)
          {            
            if (cellTiles[row][column] == 16) // Koopa
            {
                inactiveSprites.insertOrdered(
                    new SpriteList(
                        new EnemySprite(canvas, this, tileX, row),tileX,true));
            }
            else
              fillCells(tileX, row, cellTiles[row][column+1],1,cellTiles[row][column]);
            tileX += cellTiles[row][column+1];
            column+=2;
          }
          
          // Fill end tile buffer

          if (column > 0)          
            fillCells(WIDTH_IN_TILES+TRANSLATE_CELL_X, row, TRANSLATE_CELL_X,1,getCell(WIDTH_IN_TILES+TRANSLATE_CELL_X-1,row));  

        }
                        
        setPosition(TRANSLATE_X,canvas.getHeight() - HEIGHT_IN_TILES*TILE_HEIGHT); 
        cellTiles = null;
    }




    public boolean tick(int x, int y, boolean upPressed)
    {      
      // Update 
      RHSpriteX-=x;
      LHSpriteX-=x;
      activeSprites.dx-=x;
      int tile;
      
      // New-up any new on screen sprites
      if (x<0 && (tile = (RHSpriteX>>4)) >= inactivePtr.initTileX)                       
      {      
        inactivePtr = inactivePtr.next;
        if (tile == inactivePtr.previous.initTileX)
        {
          activeSprites.addSprite(inactivePtr.previous.removeSprite());   
          activeSprites.next.sprite.init(activeSprites.dx);
        }        
      }
      else
      if (x>0 && (tile = (LHSpriteX>>4)) <= inactivePtr.initTileX)
      {
        inactivePtr = inactivePtr.previous; 
        if(tile == inactivePtr.next.initTileX)
        {
          activeSprites.addSprite(inactivePtr.next.removeSprite());  
          activeSprites.next.sprite.init(activeSprites.dx);
        }        
      }      
      
      
      isDead = false;
      activePtr = activeSprites.next;
      while (activePtr != null)
      {
        
        activePtr.sprite.tick(x,y);
        isDead |= (activePtr.isAlive && mainSprite.checkSpriteCollision(activePtr.sprite, upPressed));
        activePtr=activePtr.next;
      }
      
      // Scroll field
      move(x,0);
      
      return isDead;
    }


    /**
     * @param mario
     */
    public void setMainSprite(Mario mario) {
      this.mainSprite = mario;   
      
      // Close sprites list
      SpriteList lastSprite = inactiveSprites;
      while(lastSprite.next != inactiveSprites) lastSprite=lastSprite.next;
      inactiveSprites.previous = lastSprite;
      lastSprite.next = inactiveSprites;
    }




    /**
     * @param right
     */
    public void setSpriteX(int right) {
      RHSpriteX = right + TILE_WIDTH * 6;
      LHSpriteX = right - TILE_WIDTH * 6;
    }


    /**
     * @param spriteNode
     */
    public void deactivate(SpriteList spriteNode) {
      // Deactivate sprite
      activePtr=activePtr.previous;
      inactiveSprites.insertOrdered(spriteNode.removeSprite());      
    }


}
