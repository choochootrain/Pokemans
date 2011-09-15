/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Mark
 */
public class Event implements Comparable<Event>{
    private String before,after;
    private int event;
    private String doBefore,doAfter,doToCharacter;
    private boolean complete;
    private int number;
    private boolean actedOnce = false;
    

    public Event(int number,String before,String after,String doBefore,String doAfter,String doToCharacter,int event){
        this.before = before;
        this.after = after;
        this.event = event;
        this.doBefore = doBefore;
        this.doAfter = doAfter;
        this.doToCharacter = doToCharacter;
        this.number = number;
        
    }
    
    public Event(String string){
        number = Integer.parseInt(string.substring(0,string.indexOf(" ")));
        before = string.substring(string.indexOf(" ")+" ".length(), string.indexOf("after:"));
        after = string.substring(string.indexOf("after:") + "after:".length(), string.indexOf("dobefore:"));

        doBefore = string.substring(string.indexOf("dobefore:") + "dobefore:".length(), string.indexOf("dotocharacter:"));
        
        doToCharacter = string.substring(string.indexOf("dotocharacter:") + "dotocharacter:".length(), string.indexOf("event:"));

        event = Integer.parseInt(string.substring(string.indexOf("event:") + "event:".length(), string.indexOf("doafter:")).trim());
        doAfter = string.substring(string.indexOf("doafter:") + "doafter:".length());
    }
    
    public void update(Character c,Dynamic d){
        complete = hasCompleten(c,d);
        if(!complete){
            d.setSpeech(before);
            act(d,doBefore);
            complete = hasCompleten(c,d);
            if(c.getCollision(Animation.NONE)!=null){
                if(c.getCollision(Animation.NONE).getMaker()==d){
                    this.doToCharacter(c, d);
                }
            }
        }else{
            d.setSpeech(after);
            act(d,doAfter);
        }      
    }
    
    public void act(Dynamic d,String what){
        
        int[] ints = getInts(what);
        
   
        
        if(ints.length>0){
            if(ints[0]!=2)
                if(d instanceof Person)
                    ((Person)d).makeInvisible(false);  
            if(ints[0]!=-1)
                if(d instanceof Person)
                    ((Person)d).allowUpdate(true);
                     
            switch(ints[0]){
                case -1:
                    if(d instanceof Person)
                        ((Person)d).allowUpdate(false);
                    break;                    
                case 0:
                    break;
                case 1:
                    d.destroy();
                    break;
                case 2:
                    if(d instanceof Person)
                        ((Person)d).makeInvisible(true);
                    break;
                case 3:
                    if(d instanceof Person && !actedOnce){
                        System.out.println("here");
                        try {
                            Scanner s = new Scanner(new File("Trainers\\Cinematics.txt"));
                            for (int i = 0; i < ints[1] - 1; i++) {
                                s.nextLine();
                            }
                            s.next();
                            ((Person) d).setCinematic(new Cinematic((Person) d, s.nextLine().trim()));
                        } catch (FileNotFoundException ex) {
                            System.out.println("cannot load cinematic");
                        }
                    }
                    break;
                case 4:
                    d.addEvent(new Event(doAfter.substring(doAfter.indexOf(":")+1)));
                    break;
                default:
              
            }
        }
        actedOnce = true;
    }
   
    
    public void doToCharacter(Character c,Dynamic d){
        int[] ints = getInts(doToCharacter);

        if(ints.length>0){
            switch(ints[0]){
                case -1:
                    break;
                case 0:                    
                    c.warp(ints[1], ints[2]);
                    break;
                    
            }
        }
        d.talk();        
    }
    
    public int[] getInts(String str){
        int counter = 0;
        int index = 0;
        while(index!=-1){
            index = str.indexOf(":",index+1);
            counter++;
        }
        
        int[] ints = new int[counter];
        
        int lastIndex = -1;
        index = str.indexOf(":");
        counter = 0;
        while(index!=-1){
            try{
                ints[counter] = Integer.parseInt(str.substring(lastIndex+1,index).trim());
            }catch(NumberFormatException e){
                ints[counter] = 0;
            }
            lastIndex = index;
            counter++;
            index = str.indexOf(":",index+1);
        }

        ints[counter] = Integer.parseInt(str.substring(lastIndex+1).trim());
        
        return ints;
    }
    
    private boolean hasCompleten(Character c,Dynamic d){
        boolean result = false;
        boolean hold;
        switch(event){
            case 0:
                hold = false;
                for(Pokemon p:c.currentPokemon())
                    if(p!=null)
                        hold = true;
                result = hold;
                break;
            case 1:
                hold = false;
                for(Pokemon p:c.currentPokemon())
                    if(p!=null)
                        hold = true;
                result = hold && !c.isComplete(0);
                break;
            case 2:
                result = c.isComplete(0);
                break;
            case 3:
                result = c.isComplete(1);
                break;
                
        }
        if(result)
            actedOnce = false;
        return result;
    }
    
    public int getNumber(){
        return number;
    }
    
    public int compareTo(Event e) {
        return this.getNumber()-e.getNumber();
    }
}
