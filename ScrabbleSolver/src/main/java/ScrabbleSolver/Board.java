package ScrabbleSolver;

import java.util.ArrayList;

public class Board
{   
    protected ArrayList<ArrayList<Cell>> cells;
    
    public Cell GetCell( int rowIndex,
                         int columnIndex )
    {
        assert( rowIndex < cells.size() );
        assert( columnIndex < cells.get( rowIndex ).size() );
        
        return cells.get( rowIndex ).get(  columnIndex );
    }
    
    public int GetWidth()
    {
        assert( cells.size() > 0 );
        return cells.get( 0 ).size();
    }
    
    public int GetHeight()
    {
        return cells.size();
    }
    
    @Override
    public String toString()
    {
        String string = "";
        for ( ArrayList<Cell> row : cells )
        {
            for ( Cell cell : row  )
            {
                string += String.format( "%1$-2s", cell.toString() );
            }
            string += "\r\n";
        }
        return string;
    }
}
