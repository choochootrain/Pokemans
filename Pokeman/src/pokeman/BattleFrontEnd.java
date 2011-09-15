
package pokeman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Mark, Kunal
 */
public class BattleFrontEnd{
    
    public static final int NONE = -1,MAIN = 0,MOVE = 1,POKEMON = 2;
    private static final int TXT_HEIGHT = 101;
    
    private TextBox txt,back;
    private Menu menu,moveMenu;
    private PokemonMenu pkmMenu;
    private int menuType;
    private JFrame frame;
    private BufferedImage background,circle;

    private Pokemon theirs,yours;
    private double yourCurrentPercent,theirCurrentPercent;
    private Character player;
    private int youDown,theirDown;
    private double currentExpPercent;
    private int expLevelsToGo,oldLevel;
    private ArrayList<String> queue = new ArrayList<String>();


    
    private int yBorder = 30,yStart=475;
    
    public BattleFrontEnd(JFrame frame,Character player){
        try {
            this.frame = frame;
            this.player = player;
            menuType = NONE;
            back = new TextBox(frame,"",0,475,800,TXT_HEIGHT,false,false,Style.BATTLE_TEXT2);
            back.removeKeyListener();
            pkmMenu = null;
            background = ImageIO.read(new File("Images\\BattleBackground4.png"));
            circle = ImageIO.read(new File("Images\\circle2.png"));
            expLevelsToGo = 0;
            currentExpPercent = 0;
        } catch (IOException ex) {
            Logger.getLogger(BattleFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void draw(Graphics2D g2){
        if(pkmMenu!=null){
            pkmMenu.draw(g2);

        }else{
            g2.drawImage(background,null,0,0);
            g2.drawImage(circle,null,0,yStart-yBorder-circle.getHeight()+75);
            g2.drawImage(circle,null,Window.WIDTH-circle.getWidth(),yBorder+75);

            back.draw(g2);

            if(txt!=null){
                txt.draw(g2);
                if(this.waitingForHPAndExp())
                    txt.removeKeyListener();
                else
                    txt.addKeyListener();
                if(txt.isOver())
                {
                    if(queue.size()==0)
                        txt = null;        
                    else
                        txt = new TextBox(frame,queue.remove(0),0,475,800,TXT_HEIGHT,true,false,Style.BATTLE_TEXT2);
                }
            }

            if(menu!=null){
                menu.draw(g2);
                if(menuType==MOVE){
                    int i = menu.getSelected();
                    txt = new TextBox(frame,"PP: "+yours.getMoves()[i].getPP()+"/"+yours.getMoves()[i].getTotalPP()+" "+yours.getMoves()[i].element(),600,yStart,200,TXT_HEIGHT,false,false,Style.BATTLE_TEXT3);
                    txt.removeKeyListener();                    
                }
                if(menu.result()!=null && menu.result().equals("BACK")){
                    if(menuType==MOVE)
                        txt = null;
                        makeMenu(MAIN);
                }
                if(menu.result()!=null && menu.result().equals("Attack"))
                    makeMenu(MOVE);
                if(menu.result()!=null && menuType==MOVE)
                    txt = null;
                if(menu.result()!=null && menu.result().equals("Pokemon")){
                    pkmMenu = new PokemonMenu(player,frame,true,true);
                    menu = null;
                }

            }



            g2.drawImage(yours.getBack(),null,0+circle.getWidth()/2-yours.getBack().getWidth()/2,Window.HEIGHT-TXT_HEIGHT-yours.getBack().getHeight());
            g2.drawImage(theirs.getFront(),null,Window.WIDTH-circle.getWidth()/2-theirs.getBack().getWidth()/2,yBorder);

            drawInterface(g2, true, yours, 485, Window.HEIGHT-101-85-20);
            drawInterface(g2, false, theirs, 10, 30);

        }
        
        
    }
    
    public void setPokemon(Pokemon p,boolean isYours){
        if(isYours){
            yours = p;
            yourCurrentPercent = yours.getCurrentHP()/(double)yours.getMaxHP();
            currentExpPercent = (yours.expBetweenCurrentAndNext()-yours.expToNextLevel())/(double)yours.expBetweenCurrentAndNext();
            oldLevel = yours.getLevel();
        }else{
            theirs = p;
            theirCurrentPercent = theirs.getCurrentHP()/(double)theirs.getMaxHP();
        }
    }
    
    public void setExp(double currentPercent,int levelsToGo){
        currentExpPercent = currentPercent;
        expLevelsToGo = levelsToGo;
    }
    
    public void setText(String text){
        queue.add(text);
        if(txt==null)
            txt = new TextBox(frame,queue.remove(0),0,475,800,TXT_HEIGHT,true,false,Style.BATTLE_TEXT2);
    }
    
    public boolean removeKeyListener(){
        if(txt==null)
            return false;
        txt.removeKeyListener();
        return true;
    }
    
    
    public boolean addKeyListener(){
        if(txt==null)
            return false;
        txt.addKeyListener();
        return true;
    }
    
    public void makeMenu(int type){
        menuType = type;
        
        if(type == MAIN){
            String[] str = {"Attack","Pokemon","Item","Run"};
            menu = new Menu(frame,str,300,yStart,500,TXT_HEIGHT,Style.BATTLE_TEXT3);
        }
        
        if(type == MOVE){
            Move[] moves = yours.getMoves();
            String[] str = {moves[0]==null?null:moves[0].name(),moves[1]==null?null:moves[1].name(),moves[2]==null?null:moves[2].name(),moves[3]==null?null:moves[3].name()};
            menu = new Menu(frame,str,0,yStart,600,TXT_HEIGHT,Style.BATTLE_TEXT3);
        }
        
        if(type == POKEMON){
            pkmMenu = new PokemonMenu(player,frame,true,false);
            menu = null;
        }
    }
    
    public Object getResult(){

        if(menu == null && pkmMenu==null)
            return null;
        Object ret = null;
        if(menu!=null){
            String result = menu.result();

            if(result == null)
                return null;

            

            if(menuType == MOVE){
                ret = yours.getMoves()[menu.getSelected()];
            }


            menu = null;
            menuType = NONE;
        }
        
        if(pkmMenu!=null && pkmMenu.isOver()){                
            Pokemon p = pkmMenu.getResult();
            
            if(p!=yours)
                ret = p;

            pkmMenu = null;
            if(p==null)
                makeMenu(MAIN);
            menuType = NONE;
        }

        return ret;
    }
    
    public boolean waiting(){
        return txt!=null || menu!=null || moveMenu!=null || pkmMenu!=null;
    }
    
    public boolean waitingForHPAndExp(){
        return Math.abs(yourCurrentPercent-yours.getCurrentHP()/(double)yours.getMaxHP())>.1 || Math.abs(theirCurrentPercent-theirs.getCurrentHP()/(double)theirs.getMaxHP())>.1 || Math.abs(currentExpPercent-(yours.expBetweenCurrentAndNext()-yours.expToNextLevel())/(double)yours.expBetweenCurrentAndNext())>.1;
    }
    
    public static double drawHpBar(Graphics2D g2, int x, int y, int totalHP, int currentHP,double currentPercent){

        //sizes for the large dark green round rectangle
        int MAINWIDTH = 132, MAINHEIGHT = 14, MAINARCS = 10;

        
        //size, offset for the white round rectangle
        int FirstXOffset = 30, FirstYOffset = 2, WhiteWidth = 100, WhiteHeight = 10,
                WhiteArcs = 10;
        
        //size, offset for the colored strip
        int XRectOffset = 35, YRectOffset = 4, RectMaxWidth = 93, SmallHeight = 2;
        
        //DRAW OUTSIDE BORDER
        g2.setColor(new Color(80, 104, 88));//dark green, for border
        RoundRectangle2D r = new RoundRectangle2D.Double(x, y, MAINWIDTH, MAINHEIGHT,
                MAINARCS, MAINARCS);
        
        g2.fill(r);
        
        //DRAW INSIDE WHITE ROUNDRECT
        RoundRectangle2D insiderect = new RoundRectangle2D.Double(x+FirstXOffset,
                y+FirstYOffset, WhiteWidth, WhiteHeight, WhiteArcs, WhiteArcs);
        g2.setColor(Color.WHITE);
        g2.fill(insiderect);
        
        //DRAW COLORED HEALTH BAR
        Color cone;
        Color ctwo;
        if(currentPercent * totalHP > ((int)(2.25 * totalHP/4 + 0.75))){
            cone = new Color(88, 208, 128);
            ctwo = new Color(112, 248, 168);
        } else if (currentPercent * totalHP > (265*(int)(0.2*totalHP + 0.2)/256 + 2)) {
            cone = new Color(200, 168, 8);
            ctwo = new Color(248, 224, 56);
        } else {
            cone = new Color(168, 64, 72);
            ctwo = new Color(248, 88, 56);
        }
        
        
        
        int RectActualWidth =(int)(RectMaxWidth * (currentHP/(double)totalHP));
        
        
        double d = 1/(double)RectMaxWidth;
        for(int i=0;i<2;i++){            
            if(RectActualWidth/(double)RectMaxWidth<currentPercent-d){
                    currentPercent-=d;
            }
        }
        
        RectActualWidth = (int)(RectMaxWidth * currentPercent);
        
        Rectangle2D.Double rect = new Rectangle2D.Double(x+XRectOffset, y+YRectOffset,
                RectActualWidth, SmallHeight);
        g2.setColor(cone);
        g2.fill(rect);
        rect = new Rectangle2D.Double(x+XRectOffset, y+YRectOffset+SmallHeight,
                RectActualWidth, SmallHeight*2);
        g2.setColor(ctwo);
        g2.fill(rect);
        
        rect = new Rectangle2D.Double(x + XRectOffset + RectActualWidth, y + YRectOffset,
                RectMaxWidth - RectActualWidth, SmallHeight);
        g2.setColor(new Color(72, 64, 88));//dark purpleish
        g2.fill(rect);
        
        rect = new Rectangle2D.Double(x+XRectOffset + RectActualWidth,
                y+YRectOffset+SmallHeight, RectMaxWidth -RectActualWidth,
                SmallHeight*2);
        g2.setColor(new Color(80, 104, 88));//dark green, same as border
        g2.fill(rect);
        
        //draw "HP"
        Rectangle2D.Double square = new Rectangle2D.Double(x+6, y+2, 4, 4);
        cone = new Color(248, 208, 80);
        ctwo = new Color(248, 176, 64);
        
        //this next bit moves a rectangle around and draws it to make "HP"
        g2.setColor(cone);
        g2.fill(square);
        square.x = x+12;
        g2.fill(square);
        square.x = x+18;
        g2.fill(square);
        square.x = x+22;
        square.height = 2;
        square.width = 6;
        g2.fill(square);
        square.width = 2;
        square.height = 2;
        square.x = x+26;
        square.y = y+4;
        g2.fill(square);
        g2.setColor(ctwo);
        square.width = 4;
        square.height = 4;
        square.x = x+6;
        square.y = y+8;
        g2.fill(square);
        square.x = x+12;
        g2.fill(square);
        square.x = x+18;
        g2.fill(square);
        square.x = x+6;
        square.y = y+6;
        square.width = 10;
        square.height = 2;
        g2.fill(square);
        square.x = x+18;
        g2.fill(square);
        
        return currentPercent;
        
    }
    
    public void drawInterface(Graphics2D g2, boolean yours, Pokemon p,int x, int y){
        
        
        Font f = Window.FONT;
        f = f.deriveFont(Font.BOLD, 20);

        int WIDTH = 250,HEIGHT = 75,ARC = 20;
        int HP_XSHIFT = 55,HP_YSHIFT = 35;
        int NAME_XSHIFT = 35,NAME_YSHIFT = 0;
        int HP2_XSHIFT = 100,HP2_YSHIFT = 38;
        int EXP_HEIGHT = 10, EXP_SHIFT=20, EXP_WIDTH=EXP_SHIFT+WIDTH+10, EXP_EXTENSION=0;

        x += EXP_SHIFT;

        if(!yours){
            HEIGHT -= 15;
        }



        g2.setFont(f);

        g2.setColor(new Color(77,104,99));
        g2.fill(new RoundRectangle2D.Double(x,y,WIDTH,HEIGHT,ARC,ARC));

        if(yours){
            int[] xPoints = {x-EXP_SHIFT,x+WIDTH-5,x+WIDTH-5,x+WIDTH+EXP_EXTENSION,x+WIDTH+EXP_EXTENSION,x};
            int[] yPoints = {y+EXP_HEIGHT+HEIGHT,y+EXP_HEIGHT+HEIGHT,y+HEIGHT,y+HEIGHT,y+HEIGHT-20,y+HEIGHT-20};
            g2.fill(new Polygon(xPoints,yPoints,6));
            g2.fill(new Ellipse2D.Double(x+WIDTH-EXP_HEIGHT*2+EXP_EXTENSION,y+HEIGHT-EXP_HEIGHT,EXP_HEIGHT*2,EXP_HEIGHT*2));
            
            drawExpBar(g2, x+50, y + HEIGHT - 3, p);
            //g2.setFont(f.deriveFont(Font.BOLD, 10));
            //g2.setColor(new Color(239,229,7));
            //g2.drawString("EXP", x+8 , y+HEIGHT+EXP_HEIGHT-3);
            //g2.setFont(f.deriveFont(Font.BOLD, 20));
        }



        g2.setColor(new Color(32,56,0));
        g2.fill(new RoundRectangle2D.Double(x+3,y+3,WIDTH-6,HEIGHT-6,ARC,ARC));
        g2.setColor(new Color(252,249,216));        
        g2.fill(new RoundRectangle2D.Double(x+5,y+5,WIDTH-10,HEIGHT-10,ARC,ARC));        

        boolean done = false;
        if(yours){
            double temp = yourCurrentPercent;
            yourCurrentPercent = drawHpBar(g2, x+HP_XSHIFT, y+HP_YSHIFT, p.getMaxHP(), p.getCurrentHP(),yourCurrentPercent);
            done = Math.abs(yourCurrentPercent-temp)<.001;
        }else
            theirCurrentPercent = drawHpBar(g2, x+HP_XSHIFT, y+HP_YSHIFT, p.getMaxHP(), p.getCurrentHP(),theirCurrentPercent);

        g2.setColor(new Color(62,59,68));     
        String name = p.getName() + "  Lv: "+p.getLevel();

        g2.drawString(name, x+NAME_XSHIFT,y+NAME_YSHIFT+ (int)(f.getStringBounds(name, new FontRenderContext(null,true,true)).getHeight()));
        if(!yours)
            return;
        int health = (int)(yourCurrentPercent*p.getMaxHP());
        if(done)
            health = p.getCurrentHP();
        String hp = health + "/" + p.getMaxHP();
        //System.out.println(p.getCurrentHP());
        g2.drawString(hp, x+HP2_XSHIFT,y+HP2_YSHIFT+ (int)(f.getStringBounds(hp, new FontRenderContext(null,true,true)).getHeight()));

    }
    
    public void drawExpBar(Graphics2D g2,int x,int y,Pokemon p){
        int RectWidth = 128;
        
        if(p.getLevel()!=oldLevel)
            expLevelsToGo = p.getLevel()-oldLevel;            
        oldLevel = p.getLevel();
        
        
        if(expLevelsToGo>0 || currentExpPercent<(yours.expBetweenCurrentAndNext()-yours.expToNextLevel())/(double)yours.expBetweenCurrentAndNext())
        {
            for(int i=0;i<3;i++){
                currentExpPercent+=1.0/RectWidth;
                if(currentExpPercent>=1){
                    currentExpPercent = 0;
                    expLevelsToGo--;
                }
            }
        }
        drawExpBar(g2, x, y , p , currentExpPercent);
    }
    
    /**
     * draws the bar that says exp
     * @param x
     * @param y
     * @param p
     */
    public static void drawExpBar(Graphics2D g2, int x, int y, Pokemon p, double currentPercent){
        int RectWidth = 128;
        int RectHeight = 6;
        
        Font f = Window.FONT;
        f = f.deriveFont(Font.BOLD, 20);
        
        g2.setFont(f.deriveFont(Font.BOLD, 10));
        g2.setColor(new Color(239,229,7));
        g2.drawString("EXP", x , y + 10);
        g2.setFont(f.deriveFont(Font.BOLD, 20));
        g2.setColor(new Color(192, 184, 112));
        g2.fill(new Rectangle2D.Double(x + 30, y+10-RectHeight, RectWidth, RectHeight));
        
        int colorWidth = (int)(RectWidth * currentPercent);

        g2.setColor(new Color(64, 200, 248));
        g2.fill(new Rectangle2D.Double(x+30, y+10-RectHeight, colorWidth, RectHeight ));
    }
    
}