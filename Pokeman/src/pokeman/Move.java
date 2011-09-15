
package pokeman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores information about the moves. 
 * @author Kunal
 */
public class Move implements Serializable {
    
    public static final int HP = 0,ATTACK=1,DEFENSE=2,SPECIAL=3,SPEED=4,
            ACCURACY=5,EVASION=6;
    
    private Element element;
    private int power;
    private String name;
    private Animation anim;
    private int accuracy;
    private int totalPP, currentPP;
    private Status effect;
    private int statusPercentage;
    private int attacksWhat;
    private boolean raises;
    
    //not yet fully implemented, needs more work and stuff
    private int moveSpeed;
    
    public Move(String n,Element e,int p,int what,boolean r,Animation a,int ac,int tot,Status ef,int percent){
        element = e;
        power = p;
        attacksWhat = what;
        raises = r;
        name = n;
        anim = a;
        accuracy = ac;
        totalPP = tot;
        currentPP = totalPP;
        effect = ef;
        statusPercentage = percent;
    }
    
    /**
     * Makes a move from a file
     * @param f The name of the directory with the move
     */
    public Move(String f){
        try {
            
            name = f;
            f = "Moves\\"+f+"\\";
            anim = new Animation(f);
            f += "Info.txt";
            Scanner input = new Scanner(new File(f));

            String ele = getInfo(input.nextLine(),"Element:");
            
            for(Element e : Element.values()){                
                if(e.name().equals(ele))
                    element = e;
            }
            power = Integer.parseInt(getInfo(input.nextLine(),"Power:"));
            accuracy = Integer.parseInt(getInfo(input.nextLine(),"Accuracy:"));
            totalPP = Integer.parseInt(getInfo(input.nextLine(),"PP:"));
            currentPP = totalPP;
            String line = input.nextLine();
            if(line.substring(0,line.indexOf(":")).equals("RAISES"))
                raises = true;
            else
                raises = false;
            line = getInfo(line,":");
            if(line.equals("HP"))
                attacksWhat = HP;
            if(line.equals("ATTACK"))
                attacksWhat = ATTACK;
            if(line.equals("DEFENSE"))
                attacksWhat = DEFENSE;
            if(line.equals("SPECIAL"))
                attacksWhat = SPECIAL;
            if(line.equals("SPEED"))
                attacksWhat = SPEED;
            if(line.equals("ACCURACY"))
                attacksWhat = ACCURACY;
            if(line.equals("EVASION"))
                attacksWhat = EVASION;
            line = getInfo(input.nextLine(),":");
            
            for(Status stat: Status.values()){
                if(stat.name().equals(line))
                    effect = stat;
            }
            statusPercentage = Integer.parseInt(getInfo(input.nextLine(),":"));
            input.close();
        } catch (IOException ex) {
            Logger.getLogger(Move.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getInfo(String s,String after){
        int index = s.indexOf(after)+after.length();
        int index2 = s.indexOf(",",index);
        if(index2==-1)
            return s.substring(index).trim();
        return s.substring(index,index2).trim();
    }
    
    
    public Element element()
    {
        return element;
    }
    
    public int power()
    {
        //FUCK?
        return power;
    }
    
    public String name(){
        return name;
    }
	
    public void draw(){
        
    }
    
    public int attacksWhat(){
        return attacksWhat;
    }
    
    public boolean raises(){
        return raises;
    }
    
    public Status statusEffect(){
        return effect;
    }
    
    public int accuracy(){
        return accuracy;
    }
    
    /**
     * This method is used to decrement PP. if there's not enough pp, it returns
     * false. This method should not be called if its not a human because comps
     * aren't affected by pp.
     * @return true if there's enough pp, false if there isn't
     */
    public boolean useMove(){
        if(currentPP > 0){
            currentPP--;
            return true;
        } else {
            return false;
        }
        
    }
    
    
    public int getPP() {
        return currentPP;
    }
    
    public int getTotalPP() {
        return totalPP;
    }
    // if PP for all moves is 0, add a move called Struggle (handled by pokemon
    //and the front end, not by move.
    
    public int refilPP(int amount){
        if (getPP() == getTotalPP()){
            return -1;
        } else {
            currentPP += amount;
            if (currentPP > totalPP)
                currentPP = totalPP;
            return 1;
        }
    }
}
