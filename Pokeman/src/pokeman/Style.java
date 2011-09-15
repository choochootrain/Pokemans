/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pokeman;

import java.awt.Color;

/**
 *
 * @author Mark
 */
public class Style {
    
    public static final int RECTANGLE = 0,ROUNDED_RECTANGLE = 1;
    
    public static final Style STANDARD_TEXT = new Style(new Color(86,218,228),Color.WHITE,ROUNDED_RECTANGLE,ROUNDED_RECTANGLE),
            BATTLE_TEXT = new Style(new Color(60,52,89),new Color(233,59,50),RECTANGLE,ROUNDED_RECTANGLE),
            BATTLE_TEXT2 = new Style(new Color(60,52,89),new Color(105,159,159),RECTANGLE,ROUNDED_RECTANGLE),
            BATTLE_TEXT3 = new Style(new Color(60,52,89),Color.WHITE,RECTANGLE,ROUNDED_RECTANGLE),
            POKEMON_MENU = new Style(new Color(60,52,89),Color.WHITE,RECTANGLE,RECTANGLE),
            SIGN = new Style(Color.BLACK,new Color(222,184,135),RECTANGLE,RECTANGLE),
            INTRO = new Style(Color.WHITE,Color.WHITE, ROUNDED_RECTANGLE, ROUNDED_RECTANGLE);
    
    private Color out,in;
    private int inShape,outShape;
    
    public Style(Color outSideColor,Color inSideColor,int outSideShape,int inSideShape){
        out = outSideColor;
        in = inSideColor;
        inShape = inSideShape;
        outShape = outSideShape;
    }
    
    public int getShape(boolean in){
        if(in)
            return inShape;
        else
            return outShape;
    }
    
    public Color getColor(boolean in){
        if(in)
            return this.in;
        else
            return this.out;
    }
}
