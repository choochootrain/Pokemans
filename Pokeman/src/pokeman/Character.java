
package pokeman;

import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Mark
 */
public class Character extends Person{

    private int direction;
    private int press,jumping;
    private Pokemon[] pkmn = new Pokemon[6];
    private int justUsed;
    private Dynamic currentlyReading;
    private ArrayList<Pokemon> allPokemon = new ArrayList<Pokemon>();
    private static final int MAX_NUMBER_OF_POKEMON = 200;
    private boolean[] trainersBeaten = new boolean[1000];
    private boolean[] beaten = new boolean[1000];
    
    public Character(Window w){
        super("Mark","",(int)(Window.COLUMNS/2)*Window.TILE_WIDTH,(int)(Window.ROWS/2)*Window.TILE_HEIGHT,0,w);
        try {
            this.setAnimation(new Animation("Walking"));
            press = 0;
            jumping = 0;
            int levelX = -502;
            int levelY = -502;
            setX(levelX * Window.WIDTH + (int) (Window.COLUMNS/2) * Window.TILE_WIDTH);
            setY((-levelY) * Window.HEIGHT + (int) (Window.ROWS/2) * Window.TILE_HEIGHT);
            getWindow().setLevel(levelX, levelY);

        } catch (IOException ex) {
            Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
        }

        //addPokemon(new Pokemon("Meh",17));
        //addPokemon(new Pokemon("Rattata",8));
        //addPokemon(new Pokemon("Bulbasaur",7));
        //addPokemon(new Pokemon("Friger",6));
        //addPokemon(new Pokemon("Charmander",16));
        //addPokemon(new Pokemon("Magikarp",16));

    }
    
    public void save(String file){
    
        ObjectOutputStream out = null;
        SaveData save = new SaveData(getX(),getY(),getDirection(),pkmn,allPokemon,trainersBeaten,beaten);
        
        try {        
            out = new ObjectOutputStream(new FileOutputStream(file+".pkm"));
            out.writeObject(save);
            } catch (IOException ex) {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void load(String file){
        ObjectInputStream in = null;
        SaveData s = null;
        try {

            in = new ObjectInputStream(new FileInputStream(file + ".pkm"));
            s = (SaveData) in.readObject();

            

            for(int i=-1;i<=1;i++)
                for(int j=-1;j<=1;j++)
                    getWindow().unload(getWindow().getX()+i,getWindow().getY()+j);

            setX(s.getX());
            setY(s.getY());

            int levelX = (s.getX()-(int)(Window.COLUMNS/2)*Window.TILE_WIDTH)/Window.WIDTH;
            int levelY = (int)(((s.getY()-(int)(Window.ROWS/2)*Window.TILE_HEIGHT)/-Window.HEIGHT));

            for(int i=-1;i<=1;i++)
                for(int j=-1;j<=1;j++)
                    getWindow().loadLevel(levelX+i,levelY+j,i,j);
            
            getWindow().setLevel(levelX,levelY);
            
            setDirection(s.getDirection());
            trainersBeaten = s.getTrainersBeaten();
            beaten = s.getEventsComplete();
            pkmn = s.getPkmn();
            allPokemon = s.getAllPokemon();
            
            
        } catch (IOException ex) {
            Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public void draw(Graphics2D g,int currentX,int currentY){

        
        int moreHeight = 0;
        if(jumping<Window.numberOfCounts+1){
            if(jumping>=Window.numberOfCounts/2)
                moreHeight = (jumping-Window.numberOfCounts)*4;
            if(jumping<Window.numberOfCounts/2)
                moreHeight = (jumping)*-4;
        }
        if(jumping>0){            
            jumping--;
            if(jumping==1)
                direction(getDirection());

        }
        super.draw(g,currentX,currentY+moreHeight);
        
        if(moreHeight != 0)
        {
            g.setColor(Color.BLACK);
            g.fillOval(Window.TILE_WIDTH * 12, (int)(Window.TILE_HEIGHT * 10.6), Window.TILE_WIDTH,Window.TILE_HEIGHT / 2);
        }
    }
        
    
    
    @Override
    protected void update(){

        
        
        Collideable col = getCollision(direction);

        if(col!=null && ((col.getNumber(0)==2 && direction == Animation.DOWN) || (col.getNumber(0)==3 && direction == Animation.RIGHT) || (col.getNumber(0)==4 && direction == Animation.LEFT))){
            jump();
        }


        boolean isDoor = false;
        
        

        if(justUsed==0 && col!=null && col.getNumber(0)>=-4 && col.getNumber(0)<=-1){

            direction = -(col.getNumber(0)+1);

            int oldX = getX()/Window.WIDTH;
            int oldY = -getY()/Window.HEIGHT;
            
            if(getX()<0)
                oldX--;
            if(getY()<0)
                oldY++;
            
            int levelX = col.getNumber(1);
            int levelY = col.getNumber(2);
            
            warp(levelX,levelY);
            


            for(Collideable door:getWindow().getCollision()){
                if(door.getNumber(0)>=-4 && door.getNumber(0)<=-1 && Math.abs(door.getNumber(1)-oldX)<=0 && Math.abs(door.getNumber(2)-oldY)<=0){


                                     
                    setY((door.getY()-1)*Window.TILE_HEIGHT);

                    setX(door.getX()*Window.TILE_WIDTH);                
                }
            }

            getWindow().setLevel(levelX,levelY);
            isDoor = true;

        }

        if(isDoor)
            justUsed = 4;
        else
            if(justUsed>0)
                justUsed--;   

         col = getCollision(Animation.NONE);
        
        if(direction!=Animation.NONE && !isMoving() && col!=null && col.getNumber(0)>=50 && col.getNumber(0)<150 && Math.random()<.1){
            try {
                ArrayList<String> s = new ArrayList<String>();
                Scanner input = new Scanner(new File("Areas\\" + col.getNumber(0)+".txt"));
                while (input.hasNextLine()) {
                    s.add(input.nextLine());
                }
                String actual = s.get((int)(Math.random()*s.size()));
                int firstIndex = actual.indexOf(":");
                String name = actual.substring(0,firstIndex);
                int level = Integer.parseInt(actual.substring(firstIndex+1));
                getWindow().startBattle(new Battle(this, new Pokemon(name, level), getWindow().getFrame()));
            } catch (FileNotFoundException ex) {
                System.out.println("area file doesn't exist");
            }
        }else
             makeMove(direction);
        

        
        direction = Animation.NONE;

    }
    

    
    public void read(){
        if(!this.allowedToUpdate())
            return;
        Dynamic d = getDynamic(getDirection());
        Collideable c = getCollision(getDirection());
        if(d==null && c==null)
            return;
        
        if(c!=null){
            if(c.getMaker() instanceof Person){

                Person p = (Person)c.getMaker();

                if(getDirection() == Animation.UP)
                    p.setDirection(Animation.DOWN);
                if(getDirection() == Animation.DOWN)
                    p.setDirection(Animation.UP);
                if(getDirection() == Animation.RIGHT)
                    p.setDirection(Animation.LEFT);
                if(getDirection() == Animation.LEFT)
                    p.setDirection(Animation.RIGHT);
                p.talk();
                this.allowUpdate(false);
                currentlyReading = p;
                return;
            }

            if(c.getNumber(0)==-5){
                for(Collideable col:getWindow().getCollision()){
                    if(col.getMaker() instanceof NurseJoy){
                        NurseJoy p = (NurseJoy)col.getMaker();
                        this.allowUpdate(false);
                        currentlyReading = p;
                        p.talk();
                    }
                }
            }   

        }
    
        if(d!=null){
            if(d instanceof DynamicItem){
                DynamicItem di = (DynamicItem)d;
                di.talk();
                this.allowUpdate(false);
                currentlyReading = di;
                return;
            }
        }
        


    } 
    
    public void unRead(){
        if(currentlyReading!=null && currentlyReading instanceof Person)
            ((Person)currentlyReading).allowUpdate(true);
        this.allowUpdate(true);
    }
    

    
    public void direction(int dir){
        direction = dir;
    }
    
    @Override
    protected boolean canMove(int dir){
        Collideable col = getCollision(dir);
        
        boolean lower = (col==null || ((col.getNumber(0)==0 || col.getNumber(0)>4) || (col.getNumber(0)==2 && dir == Animation.DOWN) || (col.getNumber(0)==3 && dir == Animation.RIGHT) || (col.getNumber(0)==4 && dir == Animation.LEFT)));
        return (lower);      
    }
    
    public void jump(){
        jumping = Window.numberOfCounts+1;
    }
    
    public void addPokemon(Pokemon p){
        for(int i=0;i<pkmn.length;i++)
            if(pkmn[i] == null){
                pkmn[i] = p;
                break;
            }
    }
    
    public Pokemon[] currentPokemon(){
        return pkmn;
    }
    
    public void beaten(int number){
        trainersBeaten[number] = true;
    }
    
    public boolean hasBeaten(int number){
        return trainersBeaten[number];
    }
    
    public void complete(int number){
        beaten[number] = true;
    }
    
    public boolean isComplete(int number){
        return beaten[number];
    }
    
    public void switchPokemon(int index1,int index2){
        Pokemon hold = pkmn[index1];
        pkmn[index1] = pkmn[index2];
        pkmn[index2] = hold;
    }
    
    //takes pokemon in the index of current pkmn and adds to pc.
    public boolean leavePokemon(int index){
        if(index >= 0 && index <= 5 && allPokemon.size() < MAX_NUMBER_OF_POKEMON)
        {
            Pokemon transfer = pkmn[index];
            pkmn[index] = null;
            allPokemon.add(transfer);
            return true;
        }
        else
            return false;
    }
    
    //takes pokemon in the index of pc and adds to pokemon if there is space.
    public boolean takePokemon(int index){
        if(index < allPokemon.size() && index >= 0)
        {
            for(int i = 0; i < pkmn.length; i++)
            {
                if(pkmn[i] == null)
                {
                    Pokemon transfer = allPokemon.remove(index);
                    addPokemon(transfer);
                    return true;
                }
            }
            return false;
        }
        else
            return false;
    }
}
