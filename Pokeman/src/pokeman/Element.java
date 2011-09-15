package pokeman;

import java.io.Serializable;

/**
 * For great justice, here is the Element class. the Move during battle should
 * run multiplerAgainst with both of the types of the second pokemon. Each
 * pokemon should have two types, if it only has one real type, the second
 * one will be NOTHING. Each move must have a type other than NOTHING or else
 * an IllegalStateException will be thrown by multiplerAgainst. As of now,
 * only red/blue/yellow types are implemented. Poison is considered good against
 * bug.
 * @author Kunal
 * @version 1
 */

public enum Element implements Serializable{
    FIRE, WATER, GROUND, FLYING, POISON, NORMAL, PSYCHIC, GHOST, BUG, ROCK,
    GRASS, FIGHTING, ELECTRIC, ICE, DRAGON, NOTHING;
    
    public double multiplerAgainst(Element other){
        switch(this){
            case FIRE:
                switch(other){
                    case BUG:
                    case GRASS:
                    case ICE:
                        return 2;
                    case FIRE:
                    case WATER:
                    case GROUND:
                    case ROCK:
                        return 0.5;
                    default:
                        return 1;
                }
            case WATER:
                switch(other){
                    case FIRE:
                    case GROUND:
                    case ROCK:
                        return 2;
                    case WATER:
                    case GRASS:
                    case DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case GROUND:
                switch(other){
                    case FIRE:
                    case ELECTRIC:
                    case POISON:
                    case ROCK:
                        return 2;
                    
                    case FLYING:
                        return 0;
                    case GRASS:
                    case BUG:
                        return 0.5;
                    default:
                        return 1;
                }
            case FLYING:
                switch(other){
                    case BUG:
                    case GRASS:
                    case FIGHTING:
                        return 2;
                    case ROCK:
                    case ELECTRIC:
                        return .5;
                    default:
                        return 1;
                }
            case POISON:
                switch(other){
                    case GROUND:
                    case POISON:
                    case ROCK:
                    case GHOST:
                        return 0.5;
                    case BUG:
                    case GRASS:
                        return 2;
                    default:
                        return 1;
                }
            case NORMAL:
                switch(other){
                    case ROCK:
                        return 0.5;
                    case GHOST:
                        return 0;
                    default:
                        return 1;
                }
            case PSYCHIC:
                switch(other){
                    case POISON:
                    case FIGHTING:
                        return 2;
                    case PSYCHIC:
                        return .5;
                    default:
                        return 1;
                }
            case GHOST:
                switch(other){
                    
                    case NORMAL:
                        return 0;
                    case PSYCHIC:
                    case GHOST:
                        return 2;
                    default:
                        return 1;
                }
            case BUG:
                switch(other){
                    case FIRE:
                    case FLYING:
                    case POISON:
                    case GHOST:
                    case FIGHTING:
                        return 0.5;
                    case PSYCHIC:
                    case GRASS:
                        return 2;
                    default:
                        return 1;
                }
            case ROCK:
                switch(other){
                    case FIRE:
                    case ICE:
                    case FLYING:
                    case BUG:
                        return 2;
                    case FIGHTING:
                    case GROUND:
                        return 0.5;
                    default:
                        return 1;
                }
            case GRASS:
                switch(other){
                    case WATER:
                    case GROUND:
                    case ROCK:
                        return 2;
                    case FLYING:
                    case POISON:
                    case BUG:
                    case FIRE:
                    case GRASS:
                    case DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case FIGHTING:
                switch(other){
                    case NORMAL:
                    case ICE:
                    case ROCK:
                        return 2;
                    case FLYING:
                    case POISON:
                    case PSYCHIC:
                    case BUG:
                        return 0.5;
                    case GHOST:
                        return 0;
                    default:
                        return 1;
                }
            case ELECTRIC:
                switch(other){
                    case WATER:
                    case FLYING:
                        return 2;
                    case GROUND:
                        return 0;
                    case GRASS:
                    case ELECTRIC:
                    case DRAGON:
                        return 0.5;
                    default:
                        return 1;
                }
            case ICE:
                switch(other){
                    case WATER:
                    case ICE:
                        return 0.5;
                    case GROUND:
                    case FLYING:
                    case GRASS:
                    case DRAGON:
                        return 2;
                    default:
                        return 1;
                }
            case DRAGON:
                switch(other){
                    case DRAGON:
                        return 2;
                    default:
                        return 1;
                }
            default:
                throw new IllegalStateException("Current type is nothing");
        }
    }
}
