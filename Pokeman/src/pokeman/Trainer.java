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
public class Trainer extends Person {

    private Pokemon[] p;
    private boolean battle,start;
    private String after;
    private transient BufferedImage img;
    
    public Trainer(String n,String before,String after,int x,int y,Window w,Pokemon[] p,int number,int direction){
        super(n,before,x,y,number,w);
        try {
            this.setDirection(direction);
            this.p = p;
            battle = false;
            this.after = after;
            img = ImageIO.read(new File("Images\\Exlimation Mark.png"));
        } catch (IOException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void draw(Graphics2D g,int currentX,int currentY){
        if(start && !battle)
            g.drawImage(img, null,getX()+currentX,getY()+currentY-img.getHeight());
        super.draw(g,currentX,currentY);
    }
    
    public void update(){

        if(this.isTalking() && getWindow().getPerson().hasBeaten(getNumber())){
            if(battle){
                getWindow().startBattle(new Battle(getWindow().getPerson(),this,getWindow().getFrame()));
                this.allowUpdate(false);
                this.getWindow().getPerson().allowUpdate(false);
            }
        }  
        
        if(!getWindow().getPerson().hasBeaten(getNumber())){
            if(start){
                if(this.getCollision(this.getDirection())!=null && this.getCollision(this.getDirection()).getMaker() instanceof Character){
                    talk((Character)this.getCollision(this.getDirection()).getMaker());
                    start = false;
                }else
                    this.makeMove(this.getDirection());
            }else{
                Character character = getWindow().getPerson();
                if(this.getDirection()==Animation.DOWN && character.getY()-this.getY()>0 && character.getY()-this.getY()<Window.TILE_HEIGHT*6 && character.getX()==this.getX() ||
                        this.getDirection()==Animation.UP && character.getY()-this.getY()<0 && character.getY()-this.getY()>-Window.TILE_HEIGHT*6 && character.getX()==this.getX() ||
                        this.getDirection()==Animation.RIGHT && character.getX()-this.getX()>0 && character.getX()-this.getX()<Window.TILE_HEIGHT*6 && character.getY()==this.getY() ||
                        this.getDirection()==Animation.LEFT && character.getX()-this.getX()<0 && character.getX()-this.getX()>-Window.TILE_HEIGHT*6 && character.getY()==this.getY()){
                    start = true;
                    if(!battle){
                        Window.MUSIC.loadMusic("Music\\Trainer Confrontation.mid");
                        Window.MUSIC.play(false);
                        character.allowUpdate(false);
                    }
                }
            }
        }
        

        
    }
    
    public void talk(Character c){
        boolean dead = true;
        for(int i=0;i<p.length;i++){
            if(p[i].getCurrentHP()!=0)
                dead = false;
        }
        if(dead){
            this.getWindow().getPerson().beaten(getNumber());
            this.allowUpdate(false);
            setSpeech(after);
        }
        if(c.hasBeaten(getNumber()))
            setSpeech(after);
        else{
            if(!Window.MUSIC.getName().equals("Music\\Trainer Confrontation.mid")){
                Window.MUSIC.loadMusic("Music\\Trainer Confrontation.mid");
                Window.MUSIC.play(false);
            }
        }
        super.talk();
        battle = true;
    }
    
    public Pokemon[] getPokemon(){
        return p;
    }
    

    
}
