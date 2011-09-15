package pokeman;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Person extends Dynamic{
    private String name;
    private int direction;
    private int xChange,yChange;
    private Animation walk;
    private boolean moving,allowedToUpdate;
    private int counter;
    private Window w;
    private Collideable lastEdition,newEdition;
    private TextBox txt;
    private boolean question;
    private boolean yes;
    private Menu menu;
    private boolean stationary;
    private Cinematic c;
    private boolean invisible = false;
    

    /**
     * Constructor for objects of class Person
     */
    public Person(String n,String s,int x,int y,int number,Window w)
    {
        super(n,s,x,y,number,w);
        this.w = w;
        name = n;
        direction = Animation.DOWN;



        
        allowedToUpdate = true;
        
        if(w!=null){
            lastEdition = new Collideable(this, x / Window.TILE_WIDTH, y / Window.TILE_HEIGHT + 1, 1, 0, 0);
            w.addToCollision(lastEdition);
            
            try {
                walk = new Animation(name);
            } catch (IOException ex) {
                Logger.getLogger(Person.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        

    }


    
    public void draw(Graphics2D g,int currentX,int currentY){
        counter++;


        
        if(allowedToUpdate)
            update();
        cinematicUpdate();
        if(!moving)
            walk.standingFrame();
        if(!invisible)
            g.drawImage(walk.getFrame(),null,getX()+currentX,getY()+currentY);
        
        if(counter%4==0 || walk.getDirection()!=direction){
            walk.nextFrame(direction);
        }
        
        if(moving && !stationary){   
            if(direction == Animation.RIGHT)
                setX(getX() + Window.TILE_WIDTH/Window.numberOfCounts);
            if(direction == Animation.LEFT)
                setX(getX() - Window.TILE_WIDTH/Window.numberOfCounts);
            if(direction == Animation.UP)
                setY(getY() - Window.TILE_WIDTH/Window.numberOfCounts);
            if(direction == Animation.DOWN)
                setY(getY() + Window.TILE_WIDTH/Window.numberOfCounts);


            if(counter>=(Window.numberOfCounts-1))
            {
                counter = 0;
                moving = false;
                if(lastEdition!=null)
                    w.removeFromCollision(lastEdition);
                lastEdition = newEdition;
                
            }
        }
    }
    
    public void drawTextAndMenus(Graphics2D g){
        if(txt!=null){
            txt.draw(g);
            if(txt.isOver()){
                if(!question){
                    this.allowUpdate(true);
                    getWindow().getPerson().allowUpdate(true);
                    txt = null;
                }else{
                    if(menu==null){
                        String[] str = {"yes","no"};
                        menu = new Menu(getWindow().getFrame(),str,50,400,100,100,Style.STANDARD_TEXT);
                        txt = new TextBox(getWindow().getFrame(),"",0,475,800,101,true,false,Style.STANDARD_TEXT); 
                        txt.removeKeyListener();
                        question = false;
                    }
                    
                }                
            }
        }
        
        if(menu!=null){
            menu.draw(g);
            String ret = menu.result();
            if(ret!=null){
                String str = "";
                if(ret.equals("yes")){
                    str = getSpeech().substring(getSpeech().indexOf("yes:")+"yes:".length(),getSpeech().indexOf("no:"));
                    yes = true;
                }else{
                    str = getSpeech().substring(getSpeech().indexOf("no:")+"no:".length());
                    yes = false;
                }
                txt = new TextBox(getWindow().getFrame(),str,0,475,800,101,true,false,Style.STANDARD_TEXT);
                menu = null;
                
            }
        }
    }
    
    public void cinematicUpdate() {
        if(getName().equals("ProfessorOak"))
        System.out.println(""+(c==null)+" "+isMoving());
        if(c!=null && !isMoving()){
            c.update();
            if(c!=null && c.over())
                c=null;
        }
    }
    
    /**
     * Override this method to make the person do what you want. This person 
     * just wanders around
     */
    protected void update(){
        if(c==null || isMoving()){
            if(counter>=50){
                if(Math.random()<.1){

                    int dir = (int)(Math.random()*4);
                    makeMove(dir);            
                }           
            }
        }
    }
    
    /**
     * Call this method in the direction you want to move, calls canMove to determine if it can move
     * so override that method instead
     * @param dir The direction to move
     */
    public void makeMove(int dir){
        
        if((!moving || stationary) && dir!=Animation.NONE){
            
            direction = dir;
            
            if(!stationary){

                xChange = 0;
                yChange = 0;

                switch(dir){
                    case Animation.UP:
                        yChange = -1;
                        break;
                    case Animation.DOWN:
                        yChange = 1;
                        break;
                    case Animation.RIGHT:
                        xChange = 1;
                        break;
                    case Animation.LEFT:
                        xChange = -1;
                        break;
                }



                if(canMove(dir)){

                    moving = true;

                    Collideable col = getCollision(dir);

                    newEdition = new Collideable(this,getX()/Window.TILE_WIDTH+xChange,getY()/Window.TILE_HEIGHT+1+yChange,1,0,0);
                    w.addToCollision(newEdition);
                }
            }else{
                moving = true;
            }
            counter = 0;
        }
    }
    
    public void warp(int levelX,int levelY){
                      
        int oldX = getX()/Window.WIDTH;
        int oldY = -getY()/Window.HEIGHT;
        
        if(getX()<0)
            oldX--;
        if(getY()<0)
            oldY++;

        
        
        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++)
                getWindow().unload(oldX+i,oldY+j);

        

        setX(levelX*Window.WIDTH+(int)(Window.COLUMNS/2)*Window.TILE_WIDTH);
        setY((-levelY)*Window.HEIGHT+(int)(Window.ROWS/2)*Window.TILE_HEIGHT);



        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++)
                getWindow().loadLevel(levelX+i,levelY+j,i,j);
        
        w.removeFromCollision(lastEdition);
        newEdition = new Collideable(this,getX()/Window.TILE_WIDTH+xChange,getY()/Window.TILE_HEIGHT+1,1,0,0);
        w.addToCollision(newEdition);
        lastEdition = newEdition;
    }
    
    /**
     * Override this method to determine when a person can move
     * @param dir The direction to move
     * @return true if they can move
     */
    protected boolean canMove(int dir){
        Collideable col = getCollision(dir);
        return (col==null || col.getNumber(0)==0); 
    }
    
    public void setCinematic(Cinematic c){
        this.c = c;
    }
    
    public void setStationary(boolean s){
        stationary = s;
    }
    
    public void allowUpdate(boolean allow){
        allowedToUpdate = allow;
    }
    
    public boolean allowedToUpdate(){
        return allowedToUpdate;
    }
    
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    
    public int getDirection(){
        return direction;
    }
    
    public boolean isMoving(){
        return moving;
    }
    
    public void makeInvisible(boolean b){

        if(lastEdition!=null){
            if(b==true && invisible==false)
                w.removeFromCollision(lastEdition);
            if(b==false && invisible==true)
                w.addToCollision(lastEdition);
        }
        
        invisible = b;
        allowedToUpdate = !b;
    }
    
    public void removeKeyListener(){
        if(txt!=null)
            txt.removeKeyListener();
    }
    
    public void addKeyListener(){
        if(txt!=null)
            txt.addKeyListener();
    }
    
    public boolean isTalking(){
        return txt!=null;
    }
    
    public int getHeight(){
        return walk.getFrame().getHeight();
    }
    
    public int getWidth(){
        return walk.getFrame().getWidth();
    }        

    public void setAnimation(Animation animation){
        walk = animation;
    }
    
    public void talk(){
        
        if(!moving){
            this.allowUpdate(false);
            String str = getSpeech();
            if(getSpeech().indexOf("?")!=-1){
                question = true;
                str = getSpeech().substring(0,getSpeech().indexOf("?"));
            }
            txt = new TextBox(getWindow().getFrame(),str,0,475,800,101,true,false,Style.STANDARD_TEXT);
        }
    }
    
    public boolean saidYes(){
        boolean hold = yes;
        yes = false;
        return hold;
    }
    
    protected Collideable getCollision(int dir){
        Collideable col = null;
        if(dir==Animation.UP){
            col = w.inCollision(new Collideable(this,getX()/Window.TILE_WIDTH,getY()/Window.TILE_HEIGHT-1+1,0,0,0),this);
        }
        if(dir==Animation.DOWN){
            col = w.inCollision(new Collideable(this,getX()/Window.TILE_WIDTH,getY()/Window.TILE_HEIGHT+1+1,0,0,0),this);
        }
        if(dir==Animation.RIGHT){
            col = w.inCollision(new Collideable(this,getX()/Window.TILE_WIDTH+1,getY()/Window.TILE_HEIGHT+1,0,0,0),this);
        }
        if(dir==Animation.LEFT){
            col = w.inCollision(new Collideable(this,getX()/Window.TILE_WIDTH-1,getY()/Window.TILE_HEIGHT+1,0,0,0),this);
        }
        if(dir==Animation.NONE){
            col = w.inCollision(new Collideable(this,getX()/Window.TILE_WIDTH,getY()/Window.TILE_HEIGHT+1,0,0,0),this);
        }
        if(col!=null && col.getMaker()!=this)
            return col;
        else
            return null;
    }
    
    protected Dynamic getDynamic(int dir){
        Dynamic d = null;
        if(dir==Animation.UP){
            d = w.inDynamic(new DynamicLooker(getX(),getY()), this);
        }
        if(dir==Animation.DOWN){
            d = w.inDynamic(new DynamicLooker(getX(),getY()+2*Window.TILE_HEIGHT), this);
        }
        if(dir==Animation.RIGHT){
            d = w.inDynamic(new DynamicLooker(getX()+Window.TILE_WIDTH,getY()+Window.TILE_HEIGHT), this);
        }
        if(dir==Animation.LEFT){
            d = w.inDynamic(new DynamicLooker(getX()-Window.TILE_WIDTH,getY()+Window.TILE_HEIGHT), this);
        }
        if(dir==Animation.NONE){
            d = w.inDynamic(new DynamicLooker(getX(),getY()+Window.TILE_HEIGHT), this);
        }
        return d;
    }

    @Override
    public void destroy(){
        super.destroy();
        if(lastEdition!=null)
            getWindow().removeFromCollision(lastEdition);
        if(newEdition!=null)
            getWindow().removeFromCollision(newEdition);
        c = null;
    }


    
}