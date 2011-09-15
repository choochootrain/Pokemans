/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.io.Serializable;

/**
 * This class is rather unimportant it allows you to store a location where there
 * is a collideable object. Ex. grass or tree.
 * @author Mark
 */
public class Collideable implements Comparable<Collideable>, Serializable {

    private int xTile,yTile;
    private int[] collisionNumber = new int[3];
    private Object maker;
    
    public Collideable(Object maker,int x,int y,int collisionNumber1,int collisionNumber2,int collisionNumber3){
        collisionNumber[0] = collisionNumber1;
        collisionNumber[1] = collisionNumber2;
        collisionNumber[2] = collisionNumber3;
        xTile = x;
        yTile = y;
        this.maker = maker;
    }
        
    /**
     * Returns the xTile of this collideable thing
     * @return the xTile value
     */
    public int getX(){
        return xTile;
    }
    
    /**
     * Returns the yTile of this collideable thing
     * @return the yTile value
     */
    public int getY(){
        return yTile;
    }
    
    /**
    * Gets the collision number
    * @return the CollisionNumber
    */   
    public int getNumber(int number){
        return collisionNumber[number];
    }

    public int compareTo(Collideable c) {
        if(this.getY()<c.getY())
            return -1;
        if(this.getY()==c.getY())
        {
            if(this.getX()<c.getX())
                return -1;
        }
        if(this.getY()==c.getY() && this.getX()==c.getX())
            return 0;
        
        return 1;
        
        
    }
    
    public Object getMaker(){
        return maker;
    }
        
}
