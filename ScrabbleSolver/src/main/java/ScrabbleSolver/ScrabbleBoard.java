package ScrabbleSolver;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrabbleBoard extends Board
{
    private static final int boardWidth = 15;
    private static final int boardHeight = 15;
    
    private static final Cell.MULTIPLIER[][] multipliers =
    {          
        { Cell.MULTIPLIER.TW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TW },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.TW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TW },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DW, Cell.MULTIPLIER.BL },
        { Cell.MULTIPLIER.TW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TW, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.DL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.BL, Cell.MULTIPLIER.TW }
    };
    
    public ScrabbleBoard( char[][] tiles )
    {
        assert( tiles != null );
        
        // create a pattern matcher to validate the input
        // only characters and question marks are accepted
        Pattern pattern = Pattern.compile("^[a-z|A-Z|\\?]+$");
        Matcher matcher = pattern.matcher("");
        
        // create the arrays of cells
        cells = new ArrayList<ArrayList<Cell>>( boardHeight );
        for ( int i = 0; i < boardHeight; i++ )
        {
            cells.add( i, new ArrayList<Cell>( boardWidth ) );
        }
        
        // populate the board with tiles
        assert( tiles.length == boardHeight );
        assert( multipliers.length == boardHeight );
        for ( int rowIndex = 0; rowIndex < boardWidth; rowIndex++ )
        {
            ArrayList<Cell> row = cells.get( rowIndex );
            assert( tiles[rowIndex].length == boardWidth );
            assert( multipliers[rowIndex].length == boardWidth );
            for ( int columnIndex = 0; columnIndex < boardHeight; columnIndex++ )
            {
                // load character into matcher
                //matcher.reset( new String( tiles[rowIndex][columnIndex] ) );
                
                // check for a match
                assert ( matcher.matches() );
                
                // create the cell
                row.add( columnIndex, new Cell( tiles[rowIndex][columnIndex],
                                                multipliers[rowIndex][columnIndex] ) );
            }
        }
    }
}
