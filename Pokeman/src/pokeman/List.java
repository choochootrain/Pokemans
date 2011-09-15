
package pokeman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import javax.swing.JFrame;

/**
 *
 * @author Hurshal
 */
public class List implements Serializable 
{
    private static final int widthFactor = 30, heightFactor = 5, curveFactor = 40, ySpaceFactor = 5, xSpaceFactor = 20, totalFactor = 10;
    private static final int styleWidth = 20, styleHeight = 5;
    private int center;
    private Font f;
    
    private String[] items;
    private int x,y,width,height,textHeight;
    private JFrame frame;
    
    private int lines;
    private int selected;
    private String result;
    private Style style;
    private int top;
    private int bottom;
    
            
    public List(JFrame fr,String[] menuItems,int x,int y,int width,int height,Style s)
    {
        style = s;
        center = width/2;
        f = Window.FONT;
        f = f.deriveFont(Font.PLAIN, 30);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        items = menuItems;
        
        fr.addKeyListener(new Key());
        frame = fr;
        
        textHeight = (int)f.getStringBounds(items[0], new FontRenderContext(null,false,false)).getHeight();
        lines = (height)/(textHeight) - 1;
        if(lines>items.length)
            lines = items.length;
        result = null;
        selected = 0;
        top = 0;
        bottom = lines;
        
    }
    
    public void draw(Graphics2D g2)
    {
        g2.setColor(style.getColor(false));

        if(style.getShape(false) == Style.ROUNDED_RECTANGLE)
            g2.fill(new RoundRectangle2D.Double(x,y,width,height,curveFactor,curveFactor));
        if(style.getShape(false) == Style.RECTANGLE)
            g2.fill(new Rectangle2D.Double(x,y,width,height));
        
        g2.setColor(style.getColor(true));
        
        if(style.getShape(true) == Style.ROUNDED_RECTANGLE)
            g2.fill(new RoundRectangle2D.Double(x+styleWidth/2,y+styleHeight/2,width-styleWidth,height-styleHeight,curveFactor,curveFactor));
        
        if(style.getShape(true) == Style.RECTANGLE)
            g2.fill(new Rectangle2D.Double(x+styleWidth/2,y+styleHeight/2,width-styleWidth,height-styleHeight));
        
        g2.setColor(Color.BLACK);
        
        g2.setFont(f);
        if(result==null)
        {
            //g2.drawString("" + items.length + " t" + top + " b" + bottom + " s" + selected + " " + " " + items[getSelected()], 300,300);
            int perLine = items.length/lines;
            if(perLine==0)
                perLine=1;
            for(int i=0;i<items.length;i++)
            {
                int factor = i-top+1;
                String str = items[i];
                if(items[i]==null)
                    str = "---";
                if(i <= bottom && i >= top)
                    g2.drawString(str,x+widthFactor,(int)(y+heightFactor+ySpaceFactor*factor+textHeight*(factor- 0.5)));
                if(selected == i){
                    int x1 = x+widthFactor;
                    int y1 = (int)(y+heightFactor+ySpaceFactor*factor+textHeight*(factor -0.08));
                    int[] xCoords = {x1-15,x1-15,x1-5};
                    int[] yCoords = {y1-5-15,y1-19-15,y1-12-15}; 
                    g2.fill(new Polygon(xCoords,yCoords,3));
                }
            }
            if(top > 0)
            {
                int x1 = x + center;
                int y1 = (int)(y + heightFactor* 1.2);
                int[] xCoords = {x1-10,x1+10,x1};
                int[] yCoords = {y1,y1,y1-10}; 
                g2.fill(new Polygon(xCoords,yCoords,3));
            }
            
            if(bottom < items.length-1)
            {
                int x1 = x + center;
                int y1 = (int)(y + height - heightFactor*1.5);
                int[] xCoords = {x1-10,x1+10,x1};
                int[] yCoords = {y1,y1,y1+10}; 
                g2.fill(new Polygon(xCoords,yCoords,3));
            }
            
        }
    }
    
    public String result(){
        return result;
    }
    
    public int getSelected(){
        return selected;
    }
    
    public class Key implements KeyListener{

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode()==KeyEvent.VK_UP && selected > 0)
            {
                if(selected == top)
                {
                    top--;
                    bottom--;
                }
                selected--;
            }
            if(e.getKeyCode()==KeyEvent.VK_DOWN && selected < items.length- 1)
            {
                if(selected == bottom)
                {
                    bottom++;
                    top++;
                }
                selected++;
            }
            //if(e.getKeyCode()==KeyEvent.VK_RIGHT && selected<items.length-1 && (items[selected+1]!=null))
            //    selected++;
            //if(e.getKeyCode()==KeyEvent.VK_LEFT && selected>0 && (items[selected-1]!=null))
            //    selected--;
            if(e.getKeyCode()==KeyEvent.VK_Z){
                result = items[selected];
                frame.removeKeyListener(this);
            }
            if(e.getKeyCode()==KeyEvent.VK_X){
                result = "BACK";
                frame.removeKeyListener(this);
            }

        }

        public void keyReleased(KeyEvent e) {
        }

    }
}
