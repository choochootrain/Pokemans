/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.util.ArrayList;

/**
 *
 * @author Mark
 */
public class Cinematic {
    
    private Person p;
    private String string;
    private int index=0;
    private boolean over = false;
    
    public Cinematic(Person p,String s){
        this.p = p;
        this.string = s;
        string = string.replaceAll("&",p.getWindow().getPerson().getName());
    }
    
    public void update(){
        System.out.println(index+" "+string.length());
        if(index<string.length() && index != -1){
            if(!p.getWindow().talking() && !p.getWindow().battling()){
                
                int newIndex = string.indexOf(":",index);
                if(newIndex<0)
                    newIndex = string.length();

                String hold = string.substring(index,newIndex);
                
                p.getWindow().getPerson().allowUpdate(false);
                p.allowUpdate(false);

                if(hold.length()==1){

                    switch(hold.charAt(0)){
                        case '6':
                            {
                                String pkmName = p.getWindow().getPerson().currentPokemon()[0].getName();
                                String cin = "";
                                if(pkmName.equals("Bulbasaur"))
                                    cin = "4,-1002:7,-1002:";
                                if(pkmName.equals("Squirtle"))
                                    cin = "4,-1000:7,-1000:";
                                if(pkmName.equals("Charmander"))
                                    cin = "4,-1001:7,-1001:";
                                string = string.substring(0,newIndex+1)+cin+string.substring(newIndex+1);
                            }
                            break;
                        case '8':
                            {
                                String pkmName = p.getWindow().getPerson().currentPokemon()[0].getName();
                                String cin = "";
                                if(pkmName.equals("Bulbasaur"))
                                    cin = "4,-1001:7,-1001:";
                                if(pkmName.equals("Squirtle"))
                                    cin = "4,-1002:7,-1002:";
                                if(pkmName.equals("Charmander"))
                                    cin = "4,-1000:7,-1000:";
                                string = string.substring(0,newIndex+1)+cin+string.substring(newIndex+1);
                            }
                            break;
                        case '9':   
                            p.getWindow().getPerson().allowUpdate(true);
                            p.destroy();                            
                            break;
                        default:
                            p.makeMove(Integer.parseInt(hold));
                            break;
                    }
                    
                }else{
                    boolean code = false;
                    switch(hold.charAt(0)){
                        case '4':
                            for(Dynamic d:p.getWindow().getDynamic()){
                                if(d.getNumber()==Integer.parseInt(hold.substring(2,hold.length())))
                                    string = string.substring(0,newIndex+1)+pathFind(d,p.getWindow().getCollision())+string.substring(newIndex+1);
                            }
                            code = true;
                            break;
                        case '5':
                            int x = p.getX()/Window.TILE_WIDTH;
                            int y = p.getY()/Window.TILE_HEIGHT+1;
                            if(hold.substring(2,hold.length()).equals("1"))
                                string = string.substring(0,newIndex+1)+pathFind(x,y+Window.ROWS/2+2,p.getWindow().getCollision())+string.substring(newIndex+1);
                            code = true;
                            break;
                        case '7':
                            ArrayList<Dynamic> dynamics = p.getWindow().getDynamic();
                            for(int i=0;i<dynamics.size();i++){
                                Dynamic d = dynamics.get(i);
                                if(d.getNumber()==Integer.parseInt(hold.substring(2,hold.length())))
                                    p.getWindow().removeFromDynamic(d);
                            }
                            code = true;
                            break;
                        case 'a':
                            int i = Integer.parseInt(hold.substring(2,hold.length()));
                            if(i!=-1)
                                p.getWindow().getPerson().complete(i);         
                            code = true;
                            break;
                    }
                    if(!code){
                        if(hold.contains("BATTLE"))
                        {
                            //hold = hold.substring(hold.indexOf("BATTLE")+"BATTLE".length());
                            p.getWindow().startBattle(new Battle(p.getWindow().getPerson(),new Pokemon("Bulbasaur",5),p.getWindow().getFrame()));
                        }else{
                            p.getWindow().startTextBox(new TextBox(p.getWindow().getFrame(),hold,0,475,800,101,true,false,Style.STANDARD_TEXT));
                        }
                    }
                }
                
                index = string.indexOf(":",index)!=-1?string.indexOf(":",index)+1:-1;
                
            }
        }else{
            System.out.println("free");
            if(!p.getWindow().talking() && !p.getWindow().battling()){
                
                p.allowUpdate(true);
                p.getWindow().getPerson().allowUpdate(true);
                over = true;
            }
        }
    }
    
    public String pathFind(Dynamic d,ArrayList<Collideable> cols){
        
        return pathFind(d.getX()/Window.TILE_WIDTH,d.getY()/Window.TILE_HEIGHT+((d instanceof Person)?1:0),cols);
    }
    
    public String pathFind(int finalX,int finalY,ArrayList<Collideable> cols){
        int smallestX=Integer.MAX_VALUE;
        int smallestY=Integer.MAX_VALUE;
        int largestX=Integer.MIN_VALUE;
        int largestY=Integer.MIN_VALUE;
               
        for(Collideable col:cols){
            if(col.getX()<smallestX)
                smallestX = col.getX();
            if(col.getX()>largestX)
                largestX = col.getX();
            if(col.getY()<smallestY)
                smallestY = col.getY();
            if(col.getY()>largestY)
                largestY = col.getY();
        }
        System.out.println((largestX-smallestX)+" "+(largestY-smallestY));
        boolean[][] grid = new boolean[largestX-smallestX+1][largestY-smallestY+1];
        
        for(Collideable col:cols){
            if(col.getNumber(0)==1)
                grid[col.getX()-smallestX][col.getY()-smallestY] = true;
        }
        
        String ret = "";
        
        int x = p.getX()/Window.TILE_WIDTH-smallestX;
        int y = p.getY()/Window.TILE_HEIGHT+1-smallestY;
        finalX = finalX-smallestX;
        finalY = finalY-smallestY;
        

        int yChange = y<finalY?1:-1;
        int xChange = x<finalX?1:-1;
        
        int detourDirection = xChange;
        
        
        boolean run = true;
        
        while(run){
            yChange = y<finalY?1:-1;
            xChange = x<finalX?1:-1;
            run = false;
            while(finalY!=y && !(finalX==x && finalY==y+yChange)){
                while(grid[x][y+yChange])
                {
                    if(grid[x+detourDirection][y])
                        detourDirection = -detourDirection;
                    ret+=(detourDirection==1?Animation.RIGHT:Animation.LEFT)+":";
                    x+=detourDirection;
                    
                }    
                ret+=(yChange==1?Animation.DOWN:Animation.UP)+":";
                y+=yChange;
            }
            detourDirection = yChange;
            yChange = y<finalY?1:-1;
            xChange = x<finalX?1:-1;
            while(finalX!=x && !(finalY==y && finalX==x+xChange)){
                while(grid[x+xChange][y])
                {
                    if(grid[x][y+detourDirection])
                        detourDirection = -detourDirection;
                    ret+=(detourDirection==1?Animation.DOWN:Animation.UP)+":";
                    y+=detourDirection;
                }
                ret+=(xChange==1?Animation.RIGHT:Animation.LEFT)+":";
                x+=xChange;
                run = true;
                
            }
            
        }

        
        return ret;//ret.substring(0,ret.length()-1);      
    }
    
    
    public boolean over(){
        return over;
    }
    
}
