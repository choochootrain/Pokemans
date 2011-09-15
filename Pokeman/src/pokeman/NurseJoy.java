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
public class NurseJoy extends Person{
    
    private int counter;
    
    public NurseJoy(int x,int y,Window w){
        super("NurseJoy","Welcome to the pokemon center. Would you like me to heal your pokemon? yes:Alright no:Ok come again",x,y,-5,w);
        setStationary(true);
    }
    
    @Override
    public void draw(Graphics2D g,int currentX,int currentY){
        Character player = getWindow().getPerson();
        if(saidYes()){
            for(int i=0;i<player.currentPokemon().length;i++){
                if(player.currentPokemon()[i]!=null){
                    player.currentPokemon()[i].reset();
                    player.currentPokemon()[i].heal(player.currentPokemon()[i].getMaxHP());
                }
            }
            Window.MUSIC.loadMusic("Music\\heal.mid");
            Window.MUSIC.play(false);
            removeKeyListener();
            makeMove(Animation.LEFT);
        }
   
        if(Window.MUSIC.getName().equals("Music\\heal.mid")){
            if(Window.MUSIC.playing()){
                
                //if(counter%8==0)
                    //makeMove(Animation.LEFT);
                counter++;
            }else{
                counter = 0;
                makeMove(Animation.DOWN);
                addKeyListener();
            }
        }

        super.draw(g,currentX,currentY);
    }
    
    @Override
    protected void update(){
    }
}
