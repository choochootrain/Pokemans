/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Mark
 */
public class DynamicItem extends Dynamic {

    private BufferedImage img;
    private TextBox txt;
    private boolean isPokemon;
    private Menu menu;
    
    
    public DynamicItem(String name,int x,int y,int number,boolean isPokemon,Window w){
        super(name,"Would you like to pick up a "+name+"?",x,y,number,w);
        if(getWindow()!=null){
            getWindow().addToCollision(new Collideable(this,x/Window.TILE_WIDTH,y/Window.TILE_HEIGHT,1,0,0));
        }
            
        this.isPokemon = isPokemon;
        try {
            img = ImageIO.read(new File("Images\\PokeBall.png"));
        } catch (IOException ex) {
            Logger.getLogger(DynamicItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void draw(Graphics2D g, int currentX, int currentY) {
        g.drawImage(img,null,getX()+currentX,getY()+currentY);
    }

    @Override
    public void talk(){
        String str = getSpeech();
        txt = new TextBox(getWindow().getFrame(),str,0,475,800,101,true,false,Style.STANDARD_TEXT);
        System.out.println("talk");
    }
    
    public void drawTextAndMenus(Graphics2D g){
        if(txt!=null){
            txt.draw(g);
            if(txt.isOver()){
                if(menu==null){
                    String[] str = {"yes","no"};
                    menu = new Menu(getWindow().getFrame(),str,50,400,100,100,Style.STANDARD_TEXT);
                    txt = new TextBox(getWindow().getFrame(),"",0,475,800,101,true,false,Style.STANDARD_TEXT); 
                    txt.removeKeyListener();
                }
            }
        }
        
        if(menu!=null){
            menu.draw(g);
            String ret = menu.result();
            if(ret!=null){
                String str = "";
                if(ret.equals("yes")){
                    if(isPokemon){
                        getWindow().getPerson().addPokemon(new Pokemon(getName().substring(0,getName().indexOf(" ")),Integer.parseInt(getName().substring(getName().indexOf("level")+"level".length()).trim())));
                        getWindow().removeFromDynamic(this);
                    }
                }
                txt = null;
                menu = null;
                getWindow().getPerson().allowUpdate(true);
                
            }
        }
    }
}
