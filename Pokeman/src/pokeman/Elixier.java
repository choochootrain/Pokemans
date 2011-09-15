

package pokeman;

/**
 * Valid names for elixiers are "Max Elixier" and "Elixier"
 * other names will throw exceptions
 * @author Kunal
 */
public class Elixier extends Item<Pokemon>{
    
    private int amountToHeal;
    
    public Elixier(String name){
        super(name, 1);
        
        if(name.equals("Max Elixier")){
            setPrice(0);
            amountToHeal = -1;
        } else if (name.equals("Elixier")) {
            setPrice(1000);
            amountToHeal = 10;
        } else {
            throw new IllegalArgumentException("Invalid name");
        }
    }
    
    /**
     * Use it on the specified pokemon.
     * @param other
     * @return -1 if it was unable to heal anything, 1 if it was
     */
    @Override
    public int use(Pokemon other) {
        int counter = 0;
        for(Move m: other.getMoves()){
            if(amountToHeal >= 0){
                counter += m.refilPP(amountToHeal);
            } else {
                counter += m.refilPP(m.getTotalPP());
            }
        }
        
        if (counter <= 0)
            counter = -1;
        else
            counter = 1;
        
        return counter;
    }

}
