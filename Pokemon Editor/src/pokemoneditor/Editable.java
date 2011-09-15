/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokemoneditor;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Editable extends JComponent implements MouseListener,MouseMotionListener,KeyListener,ActionListener{
 
    
    private boolean linesOn;
    public static final int COLUMNS = 25,ROWS=18,WIDTH = 800,HEIGHT = 576,BACKGROUND = 0,STATIC = 1,DYNAMIC = 2;
    private int xRate,yRate;
    private BufferedImage currentImage;
    private int mouseX,mouseY,currentLayer,oldX,oldY;
    
    private BufferedImage[][][] images = new BufferedImage[3][COLUMNS][ROWS];
    private String[][][] extraImageData = new String[3][COLUMNS][ROWS];
    private int[][][] rotated = new int[3][COLUMNS][ROWS];
    
    private int currentRotation;
    
    private ArrayList<BufferedImage> img;
    private ArrayList<String> imgNames;
    private int[] codes;
    
    private String extraInfo;
    private String music;
    
    private int holdX=-1,holdY=-1;
    
    /** Creates a new instance of Editable */
    public Editable(JFrame frame,ArrayList<BufferedImage> imgs ,ArrayList<String> strs, int[] codes) {
        xRate = (WIDTH/COLUMNS);
        yRate = (HEIGHT/ROWS);
        linesOn = true;
        addMouseListener(this);
        addMouseMotionListener(this);
        frame.addKeyListener(this);
        Timer t = new Timer(100,this);
        t.start();
        img = imgs;
        imgNames = strs;
        this.codes = codes;
        extraInfo = "";
        music = JOptionPane.showInputDialog("Music name:");
        currentRotation = 0;
    }
    
    public void paintComponent(Graphics g){

        Graphics2D g2 = (Graphics2D)g;


        int max = 3;
        if(!linesOn)
            max=2;
        for(int j=0;j<max;j++)
        {
            for(int y=0;y<ROWS;y++)
            {
                for(int x = 0;x<COLUMNS;x++)
                {
                    if(images[j][x][y]!=null){
                        AffineTransform a = new AffineTransform();
                        a.setToTranslation(x*xRate,y*yRate);
                        switch(rotated[j][x][y]){
                            case 0:
                                break;
                            case 1:
                                a.rotate(Math.toRadians(90),images[j][x][y].getWidth()/2,images[j][x][y].getHeight()/2);
                                break;    
                            case 2:
                                a.rotate(Math.toRadians(180),images[j][x][y].getWidth()/2,images[j][x][y].getHeight()/2);
                                break;    
                            case 3:
                                a.rotate(Math.toRadians(270),images[j][x][y].getWidth()/2,images[j][x][y].getHeight()/2);
                                break;
                        }
                        g2.drawImage(images[j][x][y],a,null);
                    }
                }
            }
        }

        if(linesOn && false)
        {
            for(int x=0;x<=COLUMNS+1;x++){
                int tempX = xRate*x;
                g2.draw(new Line2D.Double(tempX,0,tempX,HEIGHT));
            }
            for(int y=0;y<=ROWS+1;y++){
                int tempY = yRate*y;
                g2.draw(new Line2D.Double(0,tempY,WIDTH,tempY));
            }
        }        
        
        if(currentImage!=null && linesOn){
            AffineTransform a = new AffineTransform();

            if(holdX==-1 && holdY==-1){
                BufferedImage mouse = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
                mouse.setData(currentImage.getData());
                Graphics2D imgGraphics = mouse.createGraphics();
                imgGraphics.setColor(Color.RED);
                imgGraphics.draw(new Rectangle2D.Double(0, 0, currentImage.getWidth()-1, currentImage.getHeight()-1));
                

                a.setToTranslation(mouseX,mouseY);
                switch(currentRotation){
                case 0:
                    break;
                case 1:
                    a.rotate(Math.toRadians(90),currentImage.getWidth()/2,currentImage.getHeight()/2);
                    break;    
                case 2:
                    a.rotate(Math.toRadians(180),currentImage.getWidth()/2,currentImage.getHeight()/2);
                    break;    
                case 3:
                    a.rotate(Math.toRadians(270),currentImage.getWidth()/2,currentImage.getHeight()/2);
                    break;
                }
                g2.drawImage(mouse,a,null);
            }else{
                int x1 = holdX/xRate;
                int y1 = holdY/yRate;
                int x2 = mouseX/xRate;
                int y2 = mouseY/yRate;
                
                for(int x=x1;x<=x2;x++){
                    for(int y=y1;y<=y2;y++){
                        a.setToTranslation(x*xRate,y*yRate);
                        switch(currentRotation){
                            case 0:
                                break;
                            case 1:
                                a.rotate(Math.toRadians(90),currentImage.getWidth()/2,currentImage.getHeight()/2);
                                break;    
                            case 2:
                                a.rotate(Math.toRadians(180),currentImage.getWidth()/2,currentImage.getHeight()/2);
                                break;    
                            case 3:
                                a.rotate(Math.toRadians(270),currentImage.getWidth()/2,currentImage.getHeight()/2);
                                break;
                        }
                        g2.drawImage(currentImage,a,null);
                    }
                }
            }
        }
        
        if(holdX!=-1 && holdY!=-1){
            g2.setColor(Color.BLUE);
            System.out.println(mouseX);
            g2.draw(new Rectangle(holdX,holdY,mouseX-holdX+xRate,mouseY-holdY+yRate));
        }
    }
    
    /**
     * Turns the grid on or off
     */
    public void setLines(boolean on)
    {
        linesOn = on;
    }
    
    /**
     * sets the current extra info
     * @param on
     */
    public void setExtraInfo(String info)
    {
        extraInfo = info;
    }
    
     /**
     * Sets the current Image
     */
    public void setImage(BufferedImage img,int layer)
    {
        currentLayer = layer;
        currentImage = img;
    }
    
    public void save(String name)
    {
        for(int layer=0;layer<3;layer++)
        {
            DataOutputStream d=null;
            try {
                d = new DataOutputStream(new FileOutputStream(name + "." + layer + ".txt"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            for(int y=0;y<ROWS;y++)
            {
                String str="";
                for(int x = 0;x<COLUMNS;x++)
                {                
                    if(images[layer][x][y]!=null)
                        str+=imgNames.get(img.indexOf(images[layer][x][y]))+"~"+rotated[layer][x][y];
                    if(layer!=STATIC && extraImageData[layer][x][y]!=null && !extraImageData[layer][x][y].equals(""))
                        str+=":"+extraImageData[layer][x][y];
                    str+=",";
                }
                try {
                    d.writeBytes(str+"\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }  
        }
        try {
            PrintWriter writer = new PrintWriter(new File(name + "collision.txt"));
            writer.println(music);
            int width = WIDTH/COLUMNS;
            int height = HEIGHT/ROWS;
            String arr[][] = new String[COLUMNS][ROWS];
            for(int y=0;y<ROWS;y++)
            {
                for(int x = 0;x<COLUMNS;x++)
                {    
                    arr[x][y]="1";
                }   
            }
            
            for(int y=0;y<ROWS;y++)
            {
                for(int x = 0;x<COLUMNS;x++)
                {  
                    arr[x][y] = "1";
                }
            }
            
            for(int y=0;y<ROWS;y++)
            {
                for(int x = 0;x<COLUMNS;x++)
                {            
                    
                    if(images[BACKGROUND][x][y]!=null){
                        for(int i=0;i<=(images[BACKGROUND][x][y].getWidth()-.001)/width;i++){
                            for(int j=0;j<=(images[BACKGROUND][x][y].getHeight()-.001)/height;j++){
                                if(x+i<arr.length && y+j<arr[0].length)
                                    arr[x+i][y+j] = "0";

                            }
                        }
                    }
                    
                }
            }
            
            for(int y=0;y<ROWS;y++)
            {
                for(int x = 0;x<COLUMNS;x++)
                {            
                    
                    if(images[STATIC][x][y]!=null){
                        String code = "1";
                        if(extraImageData[STATIC][x][y]!=null && !extraImageData[STATIC][x][y].equals(""))
                            code = extraImageData[STATIC][x][y];
                        for(int i=0;i<=(images[STATIC][x][y].getWidth()-.001)/width;i++){
                            for(int j=0;j<=(images[STATIC][x][y].getHeight()-.001)/height;j++){
                                if(x+i<arr.length && y+j<arr[0].length)
                                    arr[x+i][y+j] = code;
                                
                            }
                        }
                    }

                }
            }
            
            for(int y=0;y<ROWS;y++)
            {
                for(int x = 0;x<COLUMNS;x++)
                {   
                    writer.write(arr[x][y]+",");
                }
            }
            writer.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void load(String name)
    {

        for (int layer = 0; layer < 3; layer++) {
            BufferedReader b = null;
            try {
                b = new BufferedReader(new FileReader(name + "." + layer + ".txt"));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            for (int y = 0; y < ROWS; y++) {
                String str = null;
                try {
                    str = b.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                int x = 0;
                String currentString = "";
                String extraData = "";
                boolean colon = false;
                for (int i = 0; i < str.length(); i++) {


                    if (str.charAt(i) == ',') {
                        System.out.print(currentString);
                        int rotate = 0;
                        int index = currentString.indexOf("~");
                        if(index>0){
                            try{
                                rotate = Integer.parseInt(currentString.substring(index+1));
                            }catch(Exception e){
                            }
                            currentString = currentString.substring(0,index);
                        }
                        System.out.println(rotate);
                        
                        if (imgNames.indexOf(currentString) != -1) {
                            images[layer][x][y] = img.get(imgNames.indexOf(currentString));
                        } else {
                            images[layer][x][y] = null;
                        }
                        extraImageData[layer][x][y] = extraData;
                        rotated[layer][x][y] = rotate;
                        x++;
                        currentString = "";
                        extraData = "";
                        colon = false;
                    } else {
                        if (colon) {
                            extraData += str.charAt(i);
                        }
                        if (str.charAt(i) == ':') {
                            colon = true;
                        }
                        if (!colon) {
                            currentString += str.charAt(i);
                        }
                    }
                }
            }
        }
        
        try {
            Scanner input = new Scanner(new File(name + "collision.txt"));
            music = input.nextLine();
            String line = "";
            while(input.hasNextLine()){
                 line += input.nextLine();
            }
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLUMNS; x++) {
                    extraImageData[STATIC][x][y] = line.substring(0,line.indexOf(","));
                    line = line.substring(line.indexOf(",")+1);
                    System.out.println(extraImageData[STATIC][x][y]);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Editable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void mouseClicked(MouseEvent e) 
    {    
    }
    
    public void mouseEntered(MouseEvent e) 
    {    
    }
    
    public void mouseExited(MouseEvent e) 
    {    
        mouseX = -10000;
        mouseY = -10000;     
        oldX = mouseX;
        oldY = mouseY;
        repaint();
    }
    
    public void mousePressed(MouseEvent e) 
    {    
    }
    
    public void mouseReleased(MouseEvent e) 
    {    
        if(e.getButton()==MouseEvent.BUTTON1)
        {            
            String str = "";
            
            
            int index = img.indexOf(currentImage);
            if(index>0)
                System.out.println(codes[index]);
            if(index>=0 && codes[index]!=0)
                str = ""+codes[index];
            else
                str = extraInfo;
            
            if(holdX!=-1 && holdY!=-1){             
                int x1 = holdX/xRate;
                int y1 = holdY/yRate;
                int x2 = mouseX/xRate;
                int y2 = mouseY/yRate;
                for(int x=x1;x<=x2;x++){
                    for(int y=y1;y<=y2;y++){
                        images[currentLayer][x][y] = currentImage;
                        extraImageData[currentLayer][x][y] = str;
                        rotated[currentLayer][x][y] = currentRotation;
                    }
                }
            }else{
                int x1 = mouseX/xRate;
                int y1 = mouseY/yRate;
                int x2 = (mouseX+currentImage.getWidth())/xRate;
                int y2 = (mouseY+currentImage.getHeight())/yRate;
                for(int x=x1;x<x2;x++)
                    for(int y=y1;y<y2;y++)
                        images[currentLayer][x][y] = null;
                images[currentLayer][mouseX/xRate][mouseY/yRate] = currentImage;
                extraImageData[currentLayer][mouseX/xRate][mouseY/yRate] = str;
                rotated[currentLayer][mouseX/xRate][mouseY/yRate] = currentRotation;

            }
        }
        if(e.getButton()==MouseEvent.BUTTON3)
        {
            if(holdX!=-1 && holdY!=-1){
                int x1 = holdX/xRate;
                int y1 = holdY/yRate;
                int x2 = mouseX/xRate;
                int y2 = mouseY/yRate;
                for(int x=x1;x<=x2;x++){
                    for(int y=y1;y<=y2;y++){
                        images[currentLayer][x][y] = null;
                        extraImageData[currentLayer][x][y] = "";
                        rotated[currentLayer][x][y] = 0;
                    }
                }
            }else{
                images[currentLayer][mouseX/xRate][mouseY/yRate] = null;
                extraImageData[currentLayer][mouseX/xRate][mouseY/yRate] = "";
                rotated[currentLayer][mouseX/xRate][mouseY/yRate] = 0;
            }
        }
        if(e.getButton()==MouseEvent.BUTTON2)
        {
            for(int i=0;i<images[currentLayer].length;i++)
            {
                for(int j=0;j<images[currentLayer][0].length;j++)
                {
                    images[currentLayer][i][j] = currentImage;
                }
            }
        }
        
        repaint();
                    
    }
    
    public void mouseDragged(MouseEvent e) 
    {                   
        if(currentImage!=null)
        {
            mouseX = (int)(e.getX()/xRate)*xRate;
            mouseY = (int)(e.getY()/yRate)*yRate;
            int xDiff = Math.abs(mouseX - oldX);
            int yDiff = Math.abs(mouseY - oldY);
            repaint(mouseX-xDiff,mouseY-yDiff,currentImage.getWidth()+xDiff*2,currentImage.getHeight()+yDiff*2);
            oldX = mouseX;
            oldY = mouseY;
        }
    }
    
    public void mouseMoved(MouseEvent e) 
    {    
        if(currentImage!=null)
        {
            mouseX = (int)(e.getX()/xRate)*xRate;
            mouseY = (int)(e.getY()/yRate)*yRate;
            int xDiff = Math.abs(mouseX - oldX);
            int yDiff = Math.abs(mouseY - oldY);
            repaint(mouseX-xDiff,mouseY-yDiff,currentImage.getWidth()+xDiff*2,currentImage.getHeight()+yDiff*2);
            oldX = mouseX;
            oldY = mouseY;
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_UP){
            currentRotation = 0;
        }
        if(e.getKeyCode()==KeyEvent.VK_RIGHT){
            currentRotation = 1;
        }
        if(e.getKeyCode()==KeyEvent.VK_LEFT){
            currentRotation = 3;
        }
        if(e.getKeyCode()==KeyEvent.VK_DOWN){
            currentRotation = 2;
        }
        if(e.getKeyCode()==KeyEvent.VK_CONTROL && holdX==-1 && holdY==-1){
            
            holdX = mouseX;
            holdY = mouseY;
            System.out.println(mouseX);
        }
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_CONTROL){
            holdX = -1;
            holdY = -1;
            
        }
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    
}

