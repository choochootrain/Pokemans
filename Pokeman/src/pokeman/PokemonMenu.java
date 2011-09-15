/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import javax.swing.JFrame;

/**
 *
 * @author Mark
 */
public class PokemonMenu implements Serializable {

    private Character character;
    private int selected;
    private TextBox txt,switchingTxt;
    private Menu menu;
    private boolean over;
    private JFrame frame;
    private boolean switching;
    private int switchIndex;
    private boolean inBattle;
    private boolean refreshZ;
    private boolean hasCancel;
    private int totalButtons;
    
    private static final int X_SHIFT = 50,Y_SHIFT=150,WIDTH1=200,HEIGHT1=120,Y_SHIFT2 = 100,X_SHIFT2 = X_SHIFT+WIDTH1+20,WIDTH2 = Window.WIDTH -X_SHIFT2-20,HEIGHT2 = 50,WIDTH3=150,HEIGHT3=50;
    private static final Color SELECTED_OUT_LINE = new Color(247,144,47),NOT_SELECTED_OUT_LINE = new Color(58,76,98),SELECTED_UP = new Color(248,185,144),SELECTED_DOWN = new Color(253,208,203),
            NOT_SELECTED_UP = new Color(56,144,216), NOT_SELECTED_DOWN = new Color(133,191,215);
    
    public PokemonMenu(Character c,JFrame frame,boolean inBattle,boolean hasCancel){
        frame.addKeyListener(new Key());
        this.frame = frame;
        this.inBattle = inBattle;
        this.hasCancel = hasCancel;
        character = c;
        selected = 0;
        txt = new TextBox(frame,"Choose a Pokemon.",0,Window.HEIGHT-80,500,80,false,false,Style.POKEMON_MENU);
        switchingTxt = new TextBox(frame,"Switch with...",0,Window.HEIGHT-80,500,80,false,false,Style.POKEMON_MENU);
        txt.removeKeyListener();
        switchingTxt.removeKeyListener();
        over = false;
        switching = false;
        switchIndex = -1;
        refreshZ = false;
        totalButtons = hasCancel?7:6;
        for(Pokemon p:c.currentPokemon())
            if(p==null)
                totalButtons--;
    }
    
    public void draw(Graphics2D g2){

        g2.setColor(new Color(33,104,96));
        g2.fill(new Rectangle2D.Double(0,0,Window.WIDTH,Window.HEIGHT));
        
        bigPkm(selected==0,g2,character.currentPokemon()[0]);
        for(int i=1;i<6;i++){
            smallPkm(selected==i,g2,character.currentPokemon()[i],i-1);
        }
        if(hasCancel)
            cancelButton(selected==totalButtons-1, g2,Window.WIDTH-WIDTH3-20,Window.HEIGHT-HEIGHT3-20);
        if(switching)
            switchingTxt.draw(g2);
        else
            txt.draw(g2);
        
        if(menu!=null){
            menu.draw(g2);
            if(menu.result()!=null){
                if(menu.result().equals("Switch")){
                    if(inBattle){                        
                        switchIndex = selected;
                        over = true;
                    }else{
                        switchIndex = selected;
                        switching = true;
                    }
                }
                menu = null;
            }
        }
    }
    
    public Pokemon getResult(){
        if(switchIndex>=totalButtons || switchIndex<0)
            return null;
        Pokemon p = character.currentPokemon()[switchIndex];
        return p;
    }
    
    public void cancelButton(boolean selected,Graphics2D g2,int x,int y){
        
        Font f = Window.FONT;
        f = f.deriveFont(Font.PLAIN, 20);
        g2.setFont(f);
        
        

        Color color1 = new Color(115,88,167),color3 = NOT_SELECTED_OUT_LINE;
        
        if(selected)
        {
            color1 = SELECTED_UP;
            color3 = SELECTED_OUT_LINE;
        }    
        
        g2.setColor(color3);
        g2.fill(new RoundRectangle2D.Double(x,y,WIDTH3,HEIGHT3,50,50));
        g2.setColor(color1);
        g2.fill(new RoundRectangle2D.Double(x+5,y+5,WIDTH3-10,HEIGHT3-10,50,50));
        g2.setColor(Color.WHITE);
        String str = "CANCEL";
        g2.drawString(str, (int)(x+WIDTH3/2-f.getStringBounds(str, new FontRenderContext(null,true,true)).getWidth()/2), (int)(y+HEIGHT3/2+f.getStringBounds(str, new FontRenderContext(null,true,true)).getHeight()/2-8));

    }
    
    public void bigPkm(boolean selected, Graphics2D g2,Pokemon p){
        
        if(p==null)
            return;
        
        Font f = Window.FONT;
        f = f.deriveFont(Font.PLAIN, 20);
        g2.setFont(f);
        
        

        Color color1 = NOT_SELECTED_UP,color2 = NOT_SELECTED_DOWN,color3 = NOT_SELECTED_OUT_LINE;
        
        if(selected)
        {
            color1 = SELECTED_UP;
            color2 = SELECTED_DOWN;
            color3 = SELECTED_OUT_LINE;
        }
        
        g2.setColor(color3);
        g2.fill(new RoundRectangle2D.Double(X_SHIFT-5,Y_SHIFT-5,WIDTH1+10,HEIGHT1+10,5,5));
        
        g2.setColor(color1);
        g2.fill(new Rectangle2D.Double(X_SHIFT,Y_SHIFT,WIDTH1,HEIGHT1));
        g2.setColor(color2);
        g2.fill(new Rectangle2D.Double(X_SHIFT,Y_SHIFT+HEIGHT1*2/3,WIDTH1,HEIGHT1/3));
        
        BattleFrontEnd.drawHpBar(g2, X_SHIFT+40, Y_SHIFT+HEIGHT1*2/3, p.getCurrentHP(), p.getMaxHP(), p.getCurrentHP()/(double)p.getMaxHP());
        
        String hp = p.getCurrentHP() + "/" + p.getMaxHP();
        
        g2.setColor(Color.BLACK);
        g2.drawString(hp, X_SHIFT+100, (int)(Y_SHIFT+HEIGHT1*2/3+f.getStringBounds(hp, new FontRenderContext(null,true,true)).getHeight()));
        String name = p.getName() + " Lvl: "+p.getLevel();
        g2.drawString(name, (int)(X_SHIFT+WIDTH1/2-f.getStringBounds(name, new FontRenderContext(null,true,true)).getWidth()/2), (int)(Y_SHIFT+30+f.getStringBounds(name, new FontRenderContext(null,true,true)).getHeight()));
        g2.drawImage(p.getFront().getScaledInstance(40, 40, Image.SCALE_DEFAULT), X_SHIFT+5, Y_SHIFT+5,null);
    }
    
    public void smallPkm(boolean selected, Graphics2D g2,Pokemon p,int shift){
          
        if(p==null)
            return;
        
        Font f = Window.FONT;
        f = f.deriveFont(Font.PLAIN, 20);
        g2.setFont(f);
        
        int y = Y_SHIFT2+shift*(HEIGHT2+15);
        

        Color color1 = NOT_SELECTED_UP,color2 = NOT_SELECTED_DOWN,color3 = NOT_SELECTED_OUT_LINE;
        
        if(selected)
        {
            color1 = SELECTED_UP;
            color2 = SELECTED_DOWN;
            color3 = SELECTED_OUT_LINE;
        }
        
        g2.setColor(color3);
        g2.fill(new RoundRectangle2D.Double(X_SHIFT2-5,y-5,WIDTH2+10,HEIGHT2+10,5,5));
        
        g2.setColor(color1);
        g2.fill(new Rectangle2D.Double(X_SHIFT2,y,WIDTH2,HEIGHT2));
        g2.setColor(color2);
        g2.fill(new Rectangle2D.Double(X_SHIFT2,y+HEIGHT2*2/3,WIDTH2,HEIGHT2/3));
        
        BattleFrontEnd.drawHpBar(g2, X_SHIFT2+WIDTH2*2/3, y+10, p.getCurrentHP(), p.getMaxHP(), p.getCurrentHP()/(double)p.getMaxHP());
        
        String hp = p.getCurrentHP() + "/" + p.getMaxHP();
        
        g2.setColor(Color.BLACK);
        g2.drawString(hp, X_SHIFT2+WIDTH2*2/3+50,(int)(y+HEIGHT2*1/3+f.getStringBounds(hp, new FontRenderContext(null,true,true)).getHeight()));
        String name = p.getName() + " Lvl: "+p.getLevel();
        g2.drawString(name, (int)(X_SHIFT2+WIDTH2/5), (int)(y-5+f.getStringBounds(name, new FontRenderContext(null,true,true)).getHeight()));
        g2.drawImage(p.getFront().getScaledInstance(40, 40, Image.SCALE_DEFAULT), X_SHIFT2+5, y+5,null);
    }
    
    public boolean isOver(){
        return over;
    }
    
    public int getIndex(){
        return selected;
    }
    
    public class Key implements KeyListener{

        public void keyTyped(KeyEvent e) {
            if(menu==null){
                if(e.getKeyChar()=='z' && !refreshZ){
                    refreshZ = true;
                    if(!switching){
                        if(selected == totalButtons-1 && hasCancel){
                            over = true;
                            return;
                        }
                        int x = 0,y = 0;
                        if(selected==0){
                            x=X_SHIFT;
                            y=Y_SHIFT;
                        }else{
                            x = X_SHIFT2;
                            y = Y_SHIFT2+(selected-1)*(HEIGHT2+15);
                        }
                        String[] str = null;
                        if(character.currentPokemon()[selected].getCurrentHP()==0){
                            str = new String[2];
                            str[0] = "Stats";
                            str[1] = "Cancel";
                        }else{
                            str = new String[3];
                            str[0] = "Stats";
                            str[1] = "Switch";
                            str[2] = "Cancel";
                        }
                        menu = new Menu(frame,str,x,y,150,150,Style.POKEMON_MENU);
                    }else{
                        character.switchPokemon(switchIndex, selected);
                        switching = false;
                        switchIndex = -1;
                    }
                }
                if(e.getKeyChar()=='x' && hasCancel){
                    selected = totalButtons-1;
                }
            }
        }

        public void keyPressed(KeyEvent e) {
            if(menu==null){
                if(e.getKeyCode()==KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_DOWN){
                    System.out.println(selected);
                    selected+=1;
                    selected%=totalButtons;
                }
                if(e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_UP){
                    selected-=1;
                    if(selected<0)
                        selected+=totalButtons;
                    selected%=totalButtons;
                }
            }
        }

        public void keyReleased(KeyEvent e) {
            if(e.getKeyChar()=='z'){
                refreshZ = false;
            }
        }
        
    }
    
}
