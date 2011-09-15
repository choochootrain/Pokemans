/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.awt.Graphics2D;

/**
 *
 * @author Mark
 */
public class DynamicLooker extends Dynamic{

    public DynamicLooker(int x,int y){
        super("","",x,y,0,null);
    }
    
    @Override
    public void draw(Graphics2D g, int currentX, int currentY) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void talk() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
