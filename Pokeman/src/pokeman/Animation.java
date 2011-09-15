
package pokeman;

import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 *
 * @author Mark Benjamin
 */
public class Animation implements Serializable {
    
    public static final int  NONE = -1,UP = 0,DOWN = 1,RIGHT = 2,LEFT = 3;
    private transient BufferedImage[][] animations;
    private int currentFrame,currentDirection,oldFrame;
    private int[] numberOfFrames = new int[4];
    private String name;
    
    
    /** Creates a new instance of Animation */
    public Animation(String name) throws IOException {
        
        for(int i=0;i<4;i++)
        {
            numberOfFrames[i] = 0;
        }
        
        animations = new BufferedImage[10][10];
        
        currentDirection = DOWN;
        
        this.name = name;
        
        load(name);
    }   
    
    private void load(String name) throws IOException{
        File f = new File("Animations\\"+name);
        File[] files = f.listFiles();
        if(files!=null){
            for(int i=0;i<files.length;i++)
            {

                String nameOfFile = files[i].getName();

                if(nameOfFile.contains(name) && (nameOfFile.contains(".PNG") ||  nameOfFile.contains(".png")))
                {             
                    String newName = nameOfFile.replace(name,"").replace(".PNG","").replace(".png","");
                    char firstLetter = newName.charAt(0);
                    newName = newName.substring(1,newName.length());

                        int number=-1;
                        switch(firstLetter)
                        {
                            case 'U':
                                number = UP;                            
                                break;
                            case 'D':
                                number = DOWN;
                                break;
                            case 'R':
                                number = RIGHT;
                                break;
                            case 'L':
                                number = LEFT;
                                break;                                    
                        }
                        numberOfFrames[number]++;
                        animations[number][Integer.parseInt(newName)-1] = ImageIO.read(files[i]);

                }
            }        
        }
    }
    
    
    
    public BufferedImage getFrame(){        
        if(currentDirection>=0 && numberOfFrames[currentDirection]!=0)
        {
            currentFrame %= numberOfFrames[currentDirection];
            return animations[currentDirection][currentFrame];
        }
        else
            return null;
    }
    
    public void nextFrame(int direction){
        
        if(currentFrame!=0)
        {
            oldFrame = currentFrame;
            currentFrame = 0;
            return;
        }
        else{
            int temp = oldFrame;
            oldFrame = currentFrame;
            currentFrame = temp;
        }
        
        
        if(currentDirection!=direction)
            currentFrame = 0;
        currentDirection = direction;
        currentFrame++;
        currentFrame %= numberOfFrames[direction];
        if(currentFrame==0)
            currentFrame = 1;

    } 
    
    public void setFrame(int direction,int frameNum){
        currentDirection = direction;
        currentFrame = frameNum;
    } 
    

    
    public void standingFrame(){
        currentFrame = 0;
    } 
    
    public int getFrameNumber(){
        return currentFrame;
    }
    
    public int getDirection(){
        return currentDirection;
    }
    
    private void wirteobject(ObjectOutputStream out) throws IOException{

        out.defaultWriteObject();

    }
    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{
            in.defaultReadObject();
            
            load(name);
    }
    
}

