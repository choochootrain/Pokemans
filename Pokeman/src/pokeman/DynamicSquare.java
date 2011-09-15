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
public class DynamicSquare extends Dynamic {

    public DynamicSquare(String name,String speech,int x,int y,int number,Window w){
        super(name,speech,x,y,number,w);
        getWindow().addToCollision(new Collideable(this,x/Window.TILE_WIDTH,y/Window.TILE_HEIGHT,0,0,0));
    }
    
    @Override
    public void draw(Graphics2D g, int currentX, int currentY) {    
    }

    @Override
    public void talk() {
        getWindow().getPerson().allowUpdate(false);
        getWindow().startTextBox(new TextBox(getWindow().getFrame(),getSpeech(),0,475,800,101,true,false,Style.STANDARD_TEXT));
    }

}
