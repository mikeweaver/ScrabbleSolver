package ScrabbleSolver;

public class Cell
{
    public enum MULTIPLIER
    {
        BL,
        DW,
        DL,
        TW,
        TL
    };
    
    private MULTIPLIER multiplier;
    private char character;
    
    public Cell( char character,
                 MULTIPLIER multiplier )
    {
        this.multiplier = multiplier;
        this.character = character;
    }
    
    public char GetCharacter()
    {
        return character;
    }
    
    public MULTIPLIER GetMultiplier()
    {
        return multiplier;
    }
    
    public boolean IsEmpty()
    {
        return ( character == ' ' );
    }
    
    @Override
    public String toString()
    {
        if ( character != ' ' )
        {
            return String.valueOf( character );
        }
        else if ( multiplier != MULTIPLIER.BL )
        {
            return multiplier.toString();
        }
        else
        {
            return " ";
        }
    }
}
