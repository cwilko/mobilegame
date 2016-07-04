import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/*
 * Created on 19-May-2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author cwilkin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SpriteList {
  
  public SpriteList next = null;
  public SpriteList previous = null;
  public GameSprite sprite;
  
  public boolean isAlive;  
  public final int initTileX;
  
  public int dx;
  
  public SpriteList(GameSprite sprite, int tileX, boolean isAlive) {
    this.sprite = sprite;
    initTileX = tileX;
    if (sprite != null) sprite.setNode(this);
    this.isAlive = isAlive;
  }
  
  public SpriteList removeSprite()
  {
    if (previous!=null) previous.next = next;
    if (next!=null) next.previous = previous;
    return this;
  }
  
  public void addSprite(SpriteList sprite)
  {
    if (next!=null) next.previous = sprite;
    sprite.next = next;
    sprite.previous = this;
    next = sprite;
  }
  
  public void append(SpriteList sprite)
  {
    next = sprite;
    sprite.previous = this;
  }
  
  public void insertOrdered(SpriteList sprite)
  {
    // if(sprite.initTileX > initTileX)
    // We know the main sprite will always be the head (initTileX < sprite.initTileX)

      SpriteList s = this;
      while(s.next != this && s.next.initTileX < sprite.initTileX) s=s.next;
      s.addSprite(sprite);
  }
  
  public void close(SpriteList sprite)
  {
    previous = sprite;
    sprite.next = this;
  }


}
