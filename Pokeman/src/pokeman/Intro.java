

package pokeman;

import java.awt.*;
import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.awt.event.*;


/**
 *
 * @author Hurshal
 */
public class Intro
{
    private static final int WIDTH = 800, HEIGHT = 576;
    private JFrame f;
    private List l;
    private BufferedImage logo;
    private BufferedImage bkg;
    private BufferedImage title;
    
    public Intro(JFrame fr)
    {
        f = fr;
        
        File file = new File("Images\\logo.png");
        File file2 = new File("Images\\background.png");
        File file3 = new File("Images\\title.png");
        try
        {
            logo = ImageIO.read(file);
            bkg = ImageIO.read(file2);
            title = ImageIO.read(file3);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        String[] i = new String[2];
        i[0] = "Continue";
        i[1] = "New Game";
        l = new List(f, i,(WIDTH - 170)/2, 190, 170, 90, Style.INTRO);
    }
    
    public void draw(Graphics2D g2)
    {
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,WIDTH,HEIGHT);
        g2.drawImage(bkg, null,200,285);
        g2.drawImage(logo, null,(WIDTH - logo.getWidth())/2 ,10);
        g2.drawImage(title, null,(WIDTH - title.getWidth())/2+ 10 ,10 + logo.getHeight());
        l.draw(g2);
    }
    
    public boolean isAlive()
    {
        return l.result() == null;
    }
    
    public String result()
    {
        return l.result();
    }
}

