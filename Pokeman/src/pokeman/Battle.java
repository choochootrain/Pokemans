
package pokeman;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * The back-end to the battle system. Each battle is instantiated, with 
 * either a pokemon and you, or a trainer and you.
 * @author Kunal
 */

public class Battle implements Serializable {
    private Pokemon yours;
    private Pokemon[] theirs;
    private Character player;
    
    //should this be an int?
    private Pokemon theirCurrent;
    private Trainer enemy;
    
    private BattleFrontEnd frontEnd;
    private Thread turnThread,pkmChangeThread;
    private boolean trainer,isOver,waitingForPkmThread;
    private ArrayList<Pokemon> pokemonUsed = new ArrayList<Pokemon>();

    
    
    public Battle(Character you, Pokemon enemy,JFrame frame) {
        Window.MUSIC.loadMusic("Music\\WildBattle.mid");
        Window.MUSIC.play(true);
        frontEnd = new BattleFrontEnd(frame,you);
        player = you;
        for(int i=0;i<6;i++){
            if(you.currentPokemon()[i]!=null && you.currentPokemon()[i].getCurrentHP()!=0)
            {
                yours = you.currentPokemon()[i];
                break;
            }
        }
        
        pokemonUsed.add(yours);
        
        theirs = new Pokemon[1];
        theirs[0] = enemy;
        theirCurrent = enemy;
        frontEnd.setPokemon(theirCurrent, false);
        frontEnd.setPokemon(yours,true);
        frontEnd.setText("A "+enemy.getName()+" has appeared!");
        isOver = false;
        waitingForPkmThread = false;
        trainer = false;
    }
    
    public Battle(Character you, Trainer enemy,JFrame frame) {
        Window.MUSIC.loadMusic("Music\\Trainer Battle.mid");
        Window.MUSIC.play(true);
        this.enemy = enemy;
        frontEnd = new BattleFrontEnd(frame,you);
        player = you;
        for(int i=0;i<6;i++){
            if(you.currentPokemon()[i]!=null && you.currentPokemon()[i].getCurrentHP()!=0)
            {
                yours = you.currentPokemon()[i];
                break;
            }
        }
        
        pokemonUsed.add(yours);
        
        theirs = new Pokemon[1];
        theirs = enemy.getPokemon();
        theirCurrent = theirs[0];
        frontEnd.setPokemon(theirCurrent, false);
        frontEnd.setPokemon(yours,true);
        frontEnd.setText(enemy.getName()+" wants to fight. "+enemy.getName()+" uses "+theirCurrent.getName());

        isOver = false;
        waitingForPkmThread = false;
        trainer = true;
    }
    

    
    /**
     * tells the front end to draw the stuff.
     * @param g2
     */
    public void draw(Graphics2D g2){
        boolean over = true;
        for(int i=0;i<theirs.length;i++){
            if(theirs[i].getCurrentHP()!=0)
                over = false;
        }
        
        if(!frontEnd.waiting() && theirCurrent!=null && theirCurrent.getCurrentHP()==0 && (turnThread==null || !turnThread.isAlive()) && (pkmChangeThread==null || !pkmChangeThread.isAlive())){
            
            for(int i=0;i<pokemonUsed.size();i++){
                if(pokemonUsed.get(i).getCurrentHP()==0){
                    i--;
                    pokemonUsed.remove(i);
                }                    
            }
            
            int b = theirCurrent.getBaseExp();
            int l = theirCurrent.getLevel();
            double mult;
            
            
            if(theirs.length == 1){
                mult = 1;
            } else {
                mult = 1.5;
            }
            
            int exp = (int) (mult * (b * l / 7))/pokemonUsed.size();
            
            if(!over){
                for(int i=0;i<theirs.length;i++){
                    if(theirs[i].getCurrentHP()!=0){
                        theirCurrent = theirs[i];
                        break;
                    }
                }
            }else{
                theirCurrent = null;
            }
            

            
            yours.addExperience(exp);
            frontEnd.setText(yours.getName()+" recieved "+exp+" experience!");
            for(Pokemon p:pokemonUsed){
                if(p!=yours){
                    p.addExperience(exp);
                    frontEnd.setText(p.getName()+" recieved "+exp+" experience!");
                }
            }
            pokemonUsed = new ArrayList<Pokemon>();
            pokemonUsed.add(yours);
            if(!over){
                frontEnd.setText(enemy.getName()+" uses "+theirCurrent.getName());
                frontEnd.setPokemon(theirCurrent, false);
            }
        }       

        
        isOver = over && !frontEnd.waitingForHPAndExp() && !frontEnd.waiting() && !turnThread.isAlive();
        
        if(yours.getCurrentHP()==0 && !frontEnd.waiting() && (turnThread==null || !turnThread.isAlive()) && (pkmChangeThread==null || !pkmChangeThread.isAlive()))
        {
            frontEnd.makeMenu(BattleFrontEnd.POKEMON);
        }
        
        frontEnd.draw(g2);
        
        if(!frontEnd.waitingForHPAndExp() && !frontEnd.waiting() && !over && (turnThread==null || !turnThread.isAlive()) && !waitingForPkmThread && yours.getCurrentHP()!=0 && theirCurrent.getCurrentHP()!=0)
            frontEnd.makeMenu(BattleFrontEnd.MAIN);
        
        Object result = frontEnd.getResult();
        if(result!=null){
            turnThread=null;
            if(result instanceof Move && (turnThread==null || !turnThread.isAlive()) && (pkmChangeThread==null || !pkmChangeThread.isAlive())){
                turnThread = new Thread(new Turn(player,(Move)result,theirCurrent,yours,frontEnd));
                turnThread.start();
            }
            if(result instanceof Pokemon && (pkmChangeThread==null || !pkmChangeThread.isAlive()) && (turnThread==null || !turnThread.isAlive())){
                pkmChangeThread = new Thread(new Change((Pokemon)result));
                pkmChangeThread.start();                
                waitingForPkmThread = true;
            }                
        }
        
        if(waitingForPkmThread && !pkmChangeThread.isAlive()){
            pkmChangeThread = null;
            waitingForPkmThread = false;
            turnThread = new Thread(new Turn(player,null,theirCurrent,yours,frontEnd));
            turnThread.start();
        }
        
        if(isOver){
            if(turnThread!=null)
                turnThread.interrupt();
            turnThread = null;
            if(pkmChangeThread!=null)
                pkmChangeThread.interrupt();
            pkmChangeThread = null;
        }
        
        

        
    }
    
    public boolean isOver(){
        return isOver;
    }
    
    public class Change implements Runnable{

        Pokemon p;
        
        public Change(Pokemon toSwitch){
            p = toSwitch;
        }

        public void run() {
            
            boolean stop = true;
            
            Pokemon[] pokemen = player.currentPokemon();
            int index=0;
            for(int i=0;i<6;i++)
                if(pokemen[i]==p)
                    index = i;
            
            frontEnd.setText("Thats enough "+yours.getName()+". Go "+pokemen[index].getName()+"!");  
            while(frontEnd.waiting() && stop)
                stop = !Thread.interrupted();
            yours = pokemen[index];
            if(!pokemonUsed.contains(yours))
                pokemonUsed.add(yours);
            frontEnd.setPokemon(yours,true);
            
        }
    }
    
}
