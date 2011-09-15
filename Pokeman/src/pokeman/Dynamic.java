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
public abstract class Dynamic implements Comparable<Dynamic> {

    private String name,speech;
    private int x,y;
    private Event event;
    private int number;
    private Window w;
    
    public Dynamic(String name,String speech,int x,int y,int number,Window w){
        this.name = name;
        this.speech = speech;
        this.x = x;
        this.y = y;
        this.number = number;
        this.w = w;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getSpeech(){
        return speech;
    }
    
    public void setSpeech(String s){
        speech = s;
    }    
    
    public int getX(){
        return x;
    }
    
    public void setX(int x){
        this.x = x;
    }
        
    public void setY(int y){
        this.y = y;
    }
    
    public int getY(){
        return y;
    }
    
    public void addEvent(Event e){
        event = e;
    }
    
    public int getNumber(){
        return number;
    }
    
    public Window getWindow(){
        return w;
    }
    
    public void updateEvent(){
        if(event!=null)
            event.update(w.getPerson(), this);
    }
    

    
    public abstract void draw(Graphics2D g,int currentX,int currentY);
    public abstract void talk();
    
    public int compareTo(Dynamic d) {
        if(this.getX()<d.getX())
            return -1;
        if(this.getX()==d.getX())
        {
            if(this.getY()<d.getY())
                return -1;
        }
        if(this.getX()==d.getX() && this.getY()==d.getY())
            return 0;
        
        return 1;
    }

    public void destroy() {
        getWindow().removeFromDynamic(this);
        event = null;
    }
}
