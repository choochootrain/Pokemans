/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Mark
 */
public class SaveData implements Serializable{

    private int direction;
    private int x,y;
    private Pokemon[] pkmn = new Pokemon[6];
    private ArrayList<Pokemon> allPokemon = new ArrayList<Pokemon>();
    private boolean[] trainersBeaten = new boolean[1000];
    private boolean[] eventsComplete = new boolean[1000];
    
    public SaveData(int x,int y,int direction,Pokemon[] pkmn,ArrayList<Pokemon> allPokemon ,boolean[] trainersBeaten,boolean[] eventsComplete){
        this.trainersBeaten = trainersBeaten;
        this.eventsComplete = eventsComplete;
        this.allPokemon = allPokemon;
        this.pkmn = pkmn;
        this.direction = direction;
        this.y = y;
        this.x = x;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getDirection(){
        return direction;
    }
    
    public Pokemon[] getPkmn(){
        return pkmn;
    }
    
    public ArrayList<Pokemon> getAllPokemon(){
        return allPokemon;
    }
    
    public boolean[] getTrainersBeaten(){
        return trainersBeaten;
    }
    public boolean[] getEventsComplete(){    
        return eventsComplete;
    }
    
}
