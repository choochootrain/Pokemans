
package pokeman;


/**
 * Heals the pokemon specified in use()
 * @author Kunal
 */
public class HealingItem extends Item<Pokemon>{
    private int amountToHeal;
    
    /**
     * takes a string for the name of the item. Valid entries
     * include "Potion", "Super Potion", "Hyper Potion", and "Max Potion".
     * anything else will throw an exception
     * @param s
     */
    public HealingItem(String s){
        super(s, 1);
        if(s.equals("Potion")){
            amountToHeal = 20;
            setPrice(300);
        } else if(s.equals("Super Potion")) {
            amountToHeal = 50;
            setPrice(700);
        } else if(s.equals("Hyper Potion")) {
            amountToHeal = 200;
            setPrice(1200);
        } else if(s.equals("Max Potion")){
            amountToHeal = -1;
            setPrice(2500);
        } else {
            throw new IllegalArgumentException("Invalid healing item name");
        }
    }
    
    public int use(Pokemon other){
        int initialHP = other.getCurrentHP();
        if(other.getCurrentHP() == other.getMaxHP() || other.getStatus() == Status.FAINTED){
            return -1;
        } else if (amountToHeal == -1)  {
            other.heal(other.getMaxHP());
        } else {
            other.heal(amountToHeal);
        }
        
        if(other.getCurrentHP() < initialHP)
            throw new IllegalStateException("did not heal, but was supposed to");
        
        return other.getCurrentHP() - initialHP;
    }
}
