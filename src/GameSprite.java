/*
 * Created on 24-May-2005
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
public interface GameSprite {

  public void tick(int x, int y);

  public void kill();

  public int getTopCell();
  
  public void init(int dX);

  public void setNode(SpriteList list);
  
}
