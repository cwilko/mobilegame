
import javax.microedition.lcdui.game.TiledLayer;

/*
 * Created on 10-May-2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


class Backdrop extends TiledLayer {
  
  public static final int WIDTH_IN_TILES = 10;
  public static final int HEIGHT_IN_TILES = 1;
  public static final int TILE_WIDTH = 128;
  public static final int TILE_HEIGHT = 128;

  public Backdrop(MarioCanvas canvas) {
    
    super(WIDTH_IN_TILES,
        HEIGHT_IN_TILES,
        SheepdogMIDlet.createImage("/backdrop.png"),
        TILE_WIDTH,
        TILE_HEIGHT);
    
    for (int cell=0; cell < 10;)
    {
      setCell(cell++,0,1);
      setCell(cell++,0,2);
    }
    
    setPosition(0,canvas.getHeight() - TILE_HEIGHT * HEIGHT_IN_TILES); 
  }

}
