
package pokeman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author Mark
 */
public class Window extends JComponent implements Serializable{
    
    private transient ArrayList<BufferedImage> img = new ArrayList<BufferedImage>(20); 
    private ArrayList<String> imgNames = new ArrayList<String>(20); 
    private ArrayList<Dynamic> dynamic = new ArrayList<Dynamic>();
    
    public static final int COLUMNS = 25,ROWS=18,WIDTH = 800,HEIGHT = 576,TILE_WIDTH = WIDTH/COLUMNS,TILE_HEIGHT = HEIGHT/ROWS,
            BACKGROUND = 0,STATIC = 1,DYNAMIC = 2;
    public static final int numberOfCounts = 4;

    public static final MusicSystem MUSIC = new MusicSystem();
    
    private static final int TXT_HEIGHT = 101;
    public static Font FONT;

    
    private static final String levelName = "Levels\\level";
    
    private transient BufferedImage[][] background = new BufferedImage[3][3];
    private String[][] music = new String[3][3];
    
    private int levelX,levelY;
    private int x,y;
    private ArrayList<Collideable> collision = new ArrayList<Collideable>();
    
    private boolean upPressed,downPressed,leftPressed,rightPressed;
    private boolean stopUp,stopDown,stopRight,stopLeft;
    private int pressBuffer;
    private int jump;
    private JFrame frame;
    
    private Character player;
    
    private int timerCounter;
    private Collideable special;
    
    private int control = 4;
    private TextBox txt;
    private TextBox area;
    private int areaTime;
    private int times;
    private Intro intro;
    private Menu menu;
    private List list;
    private Battle battle;
    private PokemonMenu pkmMenu;
    private String name;
    
    private int currentFilledBox,maximumBoxes = COLUMNS*ROWS;
    

    
    private ArrayList<String> trainerSayings = new ArrayList<String>();
    private ArrayList<String> peopleSayings = new ArrayList<String>();
    private ArrayList<String> items = new ArrayList<String>();
    private ArrayList<Event> events = new ArrayList<Event>();
    
    /**
     * Makes a new window that draws all the specified stuff on
     * @param frame The frame that this window is in
     */
    public Window(JFrame frame){
        try {

            FONT = Font.createFont(Font.TRUETYPE_FONT, new File("Pokemon RS part B.ttf"));
            
            //MUSIC.loadMusic("Music\\Pallet Town.mid");
            //MUSIC.play(true);
            //MUSIC.stop();
            
            File file = new File("Images\\Pokeball.gif");
            BufferedImage p = null;
            try {
                p = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toolkit tools = Toolkit.getDefaultToolkit();
            setCursor(tools.createCustomCursor(p, new Point(0, 0), "Pokeball"));

            //pkmMenu = new PokemonMenu(player,frame,false);
            //battle = new Battle(player,new Pokemon("Meh",10),frame);
                    
            //String[] str = {"Attack","Pokemon","Item","Run"};
            //menu = new Menu(frame,str,0,475,600,101);
            //player.allowUpdate(false);


            player = new Character(this);
            
            
            this.frame = frame;
            /*String[] f = new String[5];
            f[0] = "a";
            f[1] = "b";
            f[2] = "c";
            f[3] = "d";
            f[4] = "e";
            list = new List(frame ,f ,0 ,0 ,100 ,150 ,Style.STANDARD_TEXT);*/
            intro = new Intro(frame) ;
            MUSIC.loadMusic("Music\\Intro.mid");
            MUSIC.play(true);
            frame.add(this);
            this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            frame.pack();
            frame.addKeyListener(new KeyListen());


            loadTrainerSayings(peopleSayings, "Trainers\\People.txt");
            loadTrainerSayings(trainerSayings, "Trainers\\Trainers.txt");
            loadTrainerSayings(items, "Trainers\\Items.txt");
            loadEvents(events,"Trainers\\Events.txt");
            
            //levelX = 0;
            //levelY = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    loadLevel(levelX + i, levelY + j, i, j);
                }
            }
            repaint();

            frame.pack();
            repaint();

            frame.pack();

            Timer t = new Timer(160 / numberOfCounts, new Action());
            t.start();

            timerCounter = 0;
            pressBuffer = Animation.NONE;

            
            name = "Mark";
            
            dynamic.add(player);
        } catch (FontFormatException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
    /**
     * This method paints the world and everything in it
     * @param g Dont worry about this
     */
    @Override
    public void paintComponent(Graphics g){

        Graphics2D g2 = (Graphics2D)g;
        if(control==4 && intro != null && intro.isAlive())
        {
            intro.draw(g2);
            return;
        }
        else
        {
            if(intro!=null){
                if(intro.result().equals("Continue"))
                    player.load("Slot1");
                intro = null;
                control = 0;
            }
            
        }
        if(control==0 || control==3){
            g2.setColor(Color.BLACK);
            g2.fill(new Rectangle(0,0,WIDTH,HEIGHT));

            if(control==0 && player.allowedToUpdate() && control==0 && music[1][1]!=null && !MUSIC.getName().equals(music[1][1])){
                MUSIC.loadMusic(music[1][1]);
                MUSIC.play(true);
                area = new TextBox(frame,music[1][1].substring(6,music[1][1].length()-4),0,500,800,76,false,true,Style.SIGN);
                area.removeKeyListener();
                areaTime = 0;
            }
            areaTime++;
            if(areaTime>50)
                area = null;
            
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(background[i][j]!=null){
                        int xPos = (levelX-(i-1))*WIDTH+x;
                        int yPos = (-levelY-(j-1))*HEIGHT+y;
                        BufferedImage hold = background[i][j];
                        if(!(Math.abs(xPos)>=hold.getWidth() || Math.abs(yPos)>=hold.getHeight())){
                            if(xPos<0){
                                hold = hold.getSubimage(-xPos,0,hold.getWidth()+xPos,hold.getHeight());
                                xPos = 0;
                            }
                            if(yPos<0){
                                hold = hold.getSubimage(0,-yPos,hold.getWidth(),hold.getHeight()+yPos);
                                yPos = 0;
                            }
                            if(xPos>0)
                                hold = hold.getSubimage(0,0,hold.getWidth()-xPos,hold.getHeight());
                            if(yPos>0)
                                hold = hold.getSubimage(0,0,hold.getWidth(),hold.getHeight()-yPos);
                            g2.drawImage(hold,null,xPos,yPos);
                        }
                    }
                }
            }
            Collections.sort(dynamic);
            for(int i = 0;i<dynamic.size();i++){
                dynamic.get(i).updateEvent();   
                if(i<dynamic.size())
                    dynamic.get(i).draw(g2, x, y); 
            }

            for(int i = 0;i<dynamic.size();i++){
                Dynamic d = dynamic.get(i);
                if(d instanceof Person){                    
                    ((Person)(d)).drawTextAndMenus(g2);
                }
                if(d instanceof DynamicItem){                    
                    ((DynamicItem)(d)).drawTextAndMenus(g2);
                }
            }
            /*
            g2.setColor(Color.RED);

            for(Collideable c:collision){
                if(c.getNumber(0)!=0 || c.getMaker() instanceof DynamicSquare)
                    g2.draw(new Rectangle(c.getX()*TILE_WIDTH+x, c.getY()*TILE_HEIGHT+y, TILE_WIDTH, TILE_HEIGHT));
                
            }*/
            
            if(area != null)
            {
                if(times < 100)
                {
                    area.draw(g2);
                    times++;
                }
                else
                {
                    times = 0;
                    area.destroy();
                    area = null;
                }
            }
            


            if(txt!=null){
                txt.draw(g2);
                if(txt.isOver())
                {
                    txt = null;
                    player.unRead();
                }
            }
            
            if(list != null)
                list.draw(g2);
            
            if(menu!=null){
                menu.draw(g2);
                if(menu.result()!=null)
                {
                    if(menu!=null && menu.result().equals("Exit") || menu.result().equals("BACK")){
                        
                        player.allowUpdate(true);
                    }
                    if(menu!=null && menu.result().equals("Pokemon")){
                        
                        control = 2;
                        pkmMenu = new PokemonMenu(player,frame,false,true);
                    }
                    if(menu!=null && menu.result().equals("Save")){
                        
                        player.save("Slot1");
                        /*ObjectOutputStream out = null;
                        try {
                            
                            out = new ObjectOutputStream(new FileOutputStream("TextBox.pkm"));
                            out.writeObject(new TextBox(getFrame(),"",0,475,800,10,true,false,Style.BATTLE_TEXT2));
                        } catch (IOException ex) {
                            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                out.close();
                            } catch (IOException ex) {
                                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }*/

                    }
                    menu = null;
                    player.allowUpdate(true);
                }
            }
            
            if(control==3){
                currentFilledBox+=10;
                int w=COLUMNS-1,h=ROWS-1;
                boolean horizontal=true,dir = true;
                int xBlock=0,yBlock=0;
                for(int i=0;i<currentFilledBox;i++){
                   if(horizontal && ((dir && xBlock>=w) || (!dir && xBlock<=COLUMNS-1-w))){
                       horizontal = !horizontal;
                       if(!dir)
                           w--;
                   }
                   if(!horizontal && ((dir && yBlock>=h) || (!dir && yBlock<=ROWS-h))){
                       horizontal = !horizontal;
                       if(!dir)
                           h--;
                       dir = !dir;
                   }
                   g2.fill(new Rectangle2D.Double(xBlock*TILE_WIDTH,yBlock*TILE_HEIGHT,TILE_WIDTH,TILE_HEIGHT));
                   if(horizontal){
                       if(dir)
                           xBlock++;
                       else
                           xBlock--;
                   }else{
                        if(dir)
                            yBlock++;
                        else
                            yBlock--;
                   }
                   
                }
                if(currentFilledBox>=maximumBoxes)
                {
                    currentFilledBox = 0;
                    control = 1;
                }
            }
            
            
        }
        
        
        if(control==1 && battle!=null){

            battle.draw(g2);
            if(battle.isOver()){
                control = 0;
                pressBuffer = Animation.NONE;
                battle = null;
                player.allowUpdate(true);
            }
        }
        
        if(control==2 && pkmMenu!=null){
            pkmMenu.draw(g2);
            if(pkmMenu.isOver()){
                control = 0;
                pkmMenu = null;
                player.allowUpdate(true);
                pressBuffer = Animation.NONE;
            }
        }
        

        

        
    }
    
     /**
     * This method loads the current level. 
     * 
     * @param xCoord The x coordinate of the level
     * @param yCoord The y coordinate of the level
     */
    public void loadLevel(int xCoord,int yCoord,int offsetX,int offsetY)
    {
        String temp = levelName+xCoord+","+yCoord;
        //System.out.println(temp);
        
        try {
            //System.out.println("XCoord: "+xCoord+" YCoord: "+yCoord);
            
            background[-offsetX+1][offsetY+1] = ImageIO.read(new File(temp+".png"));
            music[-offsetX+1][offsetY+1] = load(xCoord,yCoord);
        } catch (IOException ex) {
            background[-offsetX+1][offsetY+1] = null;
        }
    }
    

    
    /**
     * This allows you to load all of the images in a folder into the program.
     * This must be done for images that will be used in a map prior to the map
     * being loaded.
     * 
     * @param folder The floder with the images.
     */
    private void loadImgs(String folder){
        File f = new File(folder);
        
        File[] files = f.listFiles();
        String[] list = null;
        if(files!=null){
            list = new String[files.length];
            
            for(int i=0;i<files.length;i++)
            {
                list[i] = files[i].getName();
                imgNames.add(files[i].getName());
                try{
                    img.add(ImageIO.read(files[i]));
                }catch(java.io.IOException e){
                }                
            }            
        }       
    }
    
    /**
     * This method is called when you call load level it loads the collision data
     * and any dynamic images like walking characters
     * 
     * @param xCoord x coordinate of the level
     * @param yCoord y coordinate of the level
     * @return The music of this level
     * 
     */
    private String load(int xCoord,int yCoord){
        String name=levelName+xCoord+","+yCoord;
        String musicName = "";
        try {
            Scanner input = new Scanner(new File(name + "collision.txt"));
            String str = "";
            musicName = "Music\\"+input.nextLine()+".mid";
            while (input.hasNextLine()) {
                str += input.nextLine();
            }
            
            for (int y1 = 0; y1 < ROWS; y1++) {
                for (int x1 = 0; x1 < COLUMNS; x1++) {
                    String smallString = str.substring(0,str.indexOf(","));
                    int index = 0;
                    int i=0;
                    int[] values = new int[3];
                    while(i<smallString.length() && index<3){
                        if(smallString.charAt(i)==':')
                        {
                            values[index] = Integer.parseInt(smallString.substring(0,i));
                            smallString = smallString.substring(i+1);
                            index++;
                            i=0;
                        }else
                            i++;
                    }
                    if(index<3 && !smallString.equals(""))
                        values[index] = Integer.parseInt(smallString.substring(0));
                    
                    if(values[0]!=0 || values[1]!=0 || values[2]!=0)
                    {
                        addToCollision(new Collideable(this,x1+xCoord*COLUMNS,y1+yCoord*-ROWS,values[0],values[1],values[2]));
                    }
                    str = str.substring(str.indexOf(",")+1);
                }
            }

            input = new Scanner(new File(name + ".2.txt"));
            str = "";
            while (input.hasNextLine()) {
                str += input.nextLine();
            }
            
            for (int y1 = 0; y1 < ROWS; y1++) {
                for (int x1 = 0; x1 < COLUMNS; x1++) {
                    String smallString = str.substring(0,str.indexOf(","));
                    int value = 0;
                    String personName="";
                    if(smallString.indexOf(":")!=-1){
                        int index = smallString.indexOf("~");
                        if(index>0)
                            personName = smallString.substring(0,index);
                        else
                            personName = smallString.substring(0,smallString.indexOf(":"));
                        smallString = smallString.substring(smallString.indexOf(":")+1);
                        
                        value = Integer.parseInt(smallString.substring(0));
                    }else
                        personName = smallString;
                    personName = personName.replaceAll(".png", "").replaceAll(".PNG","");
                    if(personName.length()>1){
                        if(value==-5){
                            dynamic.add(new NurseJoy(x1*TILE_WIDTH+xCoord*WIDTH,y1*TILE_HEIGHT+yCoord*-HEIGHT,this));
                        }else{
                            if(value>1000){
                                String string = trainerSayings.get(value-1001);
                                String[] pokemon = new String[6];
                                String pkm = string.substring(string.indexOf("pokemon: ")+"pokemon: ".length());
                                int lastIndex = 0;
                                int length = 0;
                                for(int i=0;i<6;i++){
                                    boolean cont = true;
                                    int index = pkm.indexOf(",",lastIndex);
                                    if(index<0){
                                        index = pkm.length();
                                        cont = false;
                                    }

                                    pokemon[i] = pkm.substring(lastIndex,index);
                                    lastIndex = pkm.indexOf(",")+1;
                                    pokemon[i].trim();
                                    length = i+1;
                                    if(!cont)
                                        break;

                                }
                                Pokemon[] pokemen = new Pokemon[length];
                                for(int i=0;i<length;i++){                                    
                                    pokemen[i] = new Pokemon(pokemon[i].substring(0,pokemon[i].indexOf(" ")),Integer.parseInt(pokemon[i].substring(pokemon[i].indexOf(" ")+1)));
                                }
                                dynamic.add(new Trainer(personName,string.substring(1,string.indexOf("after:")),string.substring(string.indexOf("after:")+"after:".length(),string.indexOf("pokemon:")),x1*TILE_WIDTH+xCoord*WIDTH,y1*TILE_HEIGHT+yCoord*-HEIGHT,this,pokemen,value-1001,Integer.parseInt(string.substring(0,1))));
                            }else{
                                if(value<=-10 && value>-1000){                                            
                                    dynamic.add(new DynamicSquare(personName,"",x1*TILE_WIDTH+xCoord*WIDTH,y1*TILE_HEIGHT+yCoord*-HEIGHT,value,this));
                                }else{
                                    if(value<=-1000){
                                        String string = items.get(-value-1000);
                                        DynamicItem item = new DynamicItem(string.substring(0,string.indexOf("pokemon:")).trim(),x1*TILE_WIDTH+xCoord*WIDTH,y1*TILE_HEIGHT+yCoord*-HEIGHT,value,string.substring(string.indexOf("pokemon:")+"pokemon:".length()).trim().equals("1"),this);
                                        dynamic.add(item);
                                    }else{
                                        if(value==-1)
                                            dynamic.add(new Person(personName,"",x1*TILE_WIDTH+xCoord*WIDTH,y1*TILE_HEIGHT+yCoord*-HEIGHT,value,this));
                                        else
                                            dynamic.add(new Person(personName,peopleSayings.get(value),x1*TILE_WIDTH+xCoord*WIDTH,y1*TILE_HEIGHT+yCoord*-HEIGHT,value,this));
                                    }
                                }
                            }
                        }
                        int index = Collections.binarySearch(events, new Event(value,"","","","","",0));
                        if(index>=0){
                            dynamic.get(dynamic.size()-1).addEvent(events.get(index));
                        }
                    }                    
                    

                    
                    str = str.substring(str.indexOf(",")+1);
                    
                }
               
            }
            
            Collections.sort(dynamic);
            
        } catch (FileNotFoundException ex) {
            System.out.println("File" + name + "not found");
        }
        
        return musicName;
    }
    
    /**
     * Unloads a level getting rid of the  collision data
     * 
     * @param xCoord x coordinate of the level
     * @param yCoord y coordinate of the level
     */
    public void unload(int xCoord,int yCoord){


        for (int y1 = 0; y1 <= ROWS; y1++) {
            for (int x1 = 0; x1 <= COLUMNS; x1++) {
                int c = Collections.binarySearch(collision,new Collideable(this,x1+xCoord*COLUMNS,y1+yCoord*-ROWS,0,0,0));
                
                if(c>=0){
                    collision.remove(collision.get(c));
                }
            }
        }
        for(int i=0;i<dynamic.size();i++){
            Dynamic d = dynamic.get(i);
            if(d!=getPerson()){
                if(d.getX()>=xCoord*WIDTH && d.getX()<=(xCoord+1)*WIDTH && d.getY()>=yCoord*-HEIGHT && d.getY()<=(yCoord-1)*-HEIGHT){
                    dynamic.remove(d);
                    i--;
                }
            }
        }

      
    }
    
    public void loadTrainerSayings(ArrayList<String> strs,String file){
        try {
            File f = new File(file);
            Scanner s = new Scanner(f);
            while(s.hasNextLine()){
                s.next();
                strs.add(s.nextLine().trim());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadEvents(ArrayList<Event> events,String file){
        try {
            Scanner s = new Scanner(new File(file));
            while (s.hasNextLine()) {
                events.add(new Event(s.nextLine()));
            }
            Collections.sort(events);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addToCollision(Collideable c){
        int pos = Collections.binarySearch(collision, c);

        if(pos<0)
            pos = -pos - 1;
        
        collision.add(pos,c);
    }
    
    public boolean removeFromCollision(Collideable c){
        int pos = collision.indexOf(c);
        if(pos>=0)
        {
            collision.remove(pos);
            return true;
        }
        else
            return false;
    }
    
    public boolean removeFromDynamic(Dynamic d){
        int pos = dynamic.indexOf(d);
        if(pos>=0)
        {
            dynamic.remove(pos);
            return true;
        }
        else
            return false;
    }
    
    public Dynamic inDynamic(DynamicLooker dl,Dynamic d){
        int pos = -1;
        for(int i=0;i<dynamic.size();i++)
            if(dynamic.get(i)!=d && dynamic.get(i).compareTo(dl)==0)
                pos = i;
        if(pos>=0)
            return dynamic.get(pos);
        else 
            return null;
    }

    public ArrayList<Dynamic> getDynamic(){
        return dynamic;
    }
        
    public ArrayList<Collideable> getCollision(){
        return collision;
    }
    
    public Collideable inCollision(Collideable c,Object o){
        int pos = -1;
        for(int i=0;i<collision.size();i++)
            if(collision.get(i).getMaker()!=o && collision.get(i).compareTo(c)==0)
                pos = i;
        if(pos>=0)
            return collision.get(pos);
        else 
            return null;
    }
    
    public void setLevel(int x,int y){
        levelX = x;
        levelY = y;
    }
    
    public int getLevelX(){
        return levelX;
    }
    
    public int getLevelY(){
        return levelY;
    }
    
    public void startBattle(Battle b){
        control = 3;
        battle = b;
    }
    
    public JFrame getFrame(){
        return frame;
    }
    
    public Character getPerson(){
        return player;
    }
    
    public void startTextBox(TextBox t){
        txt = t;
    }
    
    public boolean talking(){
        return txt!=null;
    }
    
    public boolean battling(){
        return battle!=null;
    }
    
     /**
     * The class that implments the keyListener
     * This gets all the keyboard events
     */
    public class KeyListen implements KeyListener
    {
        
        public void keyTyped(KeyEvent keyEvent) {    
            if(timerCounter==0 && keyEvent.getKeyChar()=='z')
            {
                player.read();
            }
        }

        public void keyPressed(KeyEvent keyEvent) {   

            
            if(player.allowedToUpdate()){
                if(keyEvent.getKeyCode()==KeyEvent.VK_ENTER){
                    String[] str = {"Pokedex","Pokemon","Item","You","Save","Option","Exit"};
                    menu = new Menu(frame,str,550,50,200,350,Style.POKEMON_MENU);
                    player.allowUpdate(false);
                }                    
                if(keyEvent.getKeyCode()==KeyEvent.VK_UP)
                    pressBuffer = Animation.UP;
                if(keyEvent.getKeyCode()==KeyEvent.VK_DOWN)
                    pressBuffer = Animation.DOWN;
                if(keyEvent.getKeyCode()==KeyEvent.VK_RIGHT)
                    pressBuffer = Animation.RIGHT;
                if(keyEvent.getKeyCode()==KeyEvent.VK_LEFT)
                    pressBuffer = Animation.LEFT;
            }

        }
        
        public void keyReleased(KeyEvent keyEvent){
                if(keyEvent.getKeyCode()==KeyEvent.VK_UP)
                    stopUp = true;
                if(keyEvent.getKeyCode()==KeyEvent.VK_DOWN)
                    stopDown = true;
                if(keyEvent.getKeyCode()==KeyEvent.VK_RIGHT)
                    stopRight = true;
                if(keyEvent.getKeyCode()==KeyEvent.VK_LEFT)
                    stopLeft = true;
        }
    }
    
     /**
     * This regulates the rate at which the game moves
     */
    public class Action implements ActionListener{
        public void actionPerformed(ActionEvent event){  
                        

            if(control==0){
                if(timerCounter==0 && !(upPressed || downPressed || rightPressed || leftPressed))
                {
                    upPressed = pressBuffer == Animation.UP;// && collisionUp;
                    downPressed = pressBuffer == Animation.DOWN;// && collisionDown;
                    rightPressed = pressBuffer == Animation.RIGHT;// && collisionRight;
                    leftPressed = pressBuffer == Animation.LEFT;// && collisionLeft;
                }

                /*
                 * Changes the animation 
                 */
                if(timerCounter==0)
                {
                    if(upPressed)
                        player.direction(Animation.UP);   

                    if(downPressed)
                        player.direction(Animation.DOWN); 

                    if(rightPressed)
                        player.direction(Animation.RIGHT);   

                    if(leftPressed)
                        player.direction(Animation.LEFT); 


                }
                if(upPressed || downPressed || rightPressed || leftPressed)
                    timerCounter++;
                else
                    timerCounter = 0;

                if(timerCounter==numberOfCounts)
                {
                    timerCounter = 0;

                    if((stopUp && upPressed)){
                        upPressed = false;
                        stopUp = false;
                        if(pressBuffer == Animation.UP)
                            pressBuffer = Animation.NONE;
                    }
                    if((stopDown && downPressed)){
                        downPressed = false;
                        stopDown = false;
                        if(pressBuffer == Animation.DOWN)
                            pressBuffer = Animation.NONE;

                    }

                    if((stopRight && rightPressed)){
                        rightPressed = false;
                        stopRight = false;
                        if(pressBuffer == Animation.RIGHT)
                            pressBuffer = Animation.NONE;
                    }

                    if((stopLeft && leftPressed)){
                        leftPressed = false;
                        stopLeft = false;
                        if(pressBuffer == Animation.LEFT)
                            pressBuffer = Animation.NONE;
                    }    
                }


                x = -(player.getX()-(int)(Window.COLUMNS/2)*Window.TILE_WIDTH);
                y = -(player.getY()-(int)(Window.ROWS/2)*Window.TILE_HEIGHT);

                int tempX = player.getX()/WIDTH;
                int tempY = -player.getY()/HEIGHT;

                if(player.getX()<0)
                    tempX--;
                if(player.getY()<0)
                    tempY++;

                loadNewLevels(tempX,tempY);

                levelX = tempX;
                levelY = tempY;
            }
                   
            
            repaint();
        }      
    }
    

    /*
     * Determines what needs to be loaded or unloaded, moves the backgrounds
     * acordingly
     */    
    private void loadNewLevels(int newX,int newY){
        
        
        if(Math.abs(newX-levelX)==1){
            unload(2*levelX-newX,levelY);
            unload(2*levelX-newX,levelY+1);
            unload(2*levelX-newX,levelY-1);

            if(newX<levelX){
                for(int i=0;i<2;i++){
                    for(int j=0;j<3;j++){
                        background[i][j] = background[i+1][j];
                        music[i][j] = music[i+1][j];
                    }
                }
            }else{
                for(int i=1;i>=0;i--){
                    for(int j=0;j<3;j++){
                        background[i+1][j] = background[i][j];
                        music[i+1][j] = music[i][j];
                    }
                } 
            }

            loadLevel(2*newX-levelX,levelY,-levelX+newX,0);
            loadLevel(2*newX-levelX,levelY+1,-levelX+newX,1);
            loadLevel(2*newX-levelX,levelY-1,-levelX+newX,-1);
            levelX = newX;

        }
        if(Math.abs(newY-levelY)==1){
            unload(levelX,2*levelY-newY);
            unload(levelX-1,2*levelY-newY);
            unload(levelX+1,2*levelY-newY);

            if(newY>levelY){
                for(int i=0;i<2;i++){
                    for(int j=0;j<3;j++){
                        background[j][i] = background[j][i+1];
                        music[j][i] = music[j][i+1];
                    }
                }
            }else{
                for(int i=1;i>=0;i--){
                    for(int j=0;j<3;j++){
                        background[j][i+1] = background[j][i];
                        music[j][i+1] = music[j][i];
                    }
                } 
            }              
            
            loadLevel(levelX,2*newY-levelY,0,-levelY+newY);
            loadLevel(levelX+1,2*newY-levelY,1,-levelY+newY);
            loadLevel(levelX-1,2*newY-levelY,-1,-levelY+newY);
            levelY = newY;
        }
    }   

}
