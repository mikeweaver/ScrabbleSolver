package ScrabbleSolver;

import java.util.*;

public class ScrabbleSolver
{
    private enum DIRECTION
    {
        UP,
        DOWN,
        LEFT,
        RIGHT
    };
    
    private static final char[][] testTiles =
        {          
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', ' ', 'H', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', 'T', ' ', ' ', ' ', 'E', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', 'S', 'U', 'P', 'E', 'R', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'A', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'D', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',  }
        };
    
    private ArrayList<ArrayList<List<Character>>> singleTileMoves;
    
    // TBD, can't we use regular arrays here? How about char instead of Character?
    private static final ArrayList<Character> alphabet = new ArrayList<Character>( 27 )
            {{ 
                add( 'a' );
                add( 'b' );
                add( 'c' );
                add( 'd' );
                add( 'e' );
                add( 'f' );
                add( 'g' );
                add( 'h' );
                add( 'i' );
                add( 'j' );
                add( 'k' );
                add( 'l' );
                add( 'm' );
                add( 'n' );
                add( 'o' );
                add( 'p' );
                add( 'q' );
                add( 'r' );
                add( 's' );
                add( 't' );
                add( 'u' );
                add( 'v' );
                add( 'w' );
                add( 'x' );
                add( 'y' );
                add( 'z' );
            }};
    
    Board board;
    Dictionary dictionary;
    
    public ScrabbleSolver( Dictionary dictionary )
    {
        board = new ScrabbleBoard( testTiles );
        System.out.println( board );
        this.dictionary = dictionary;
        GenerateSingleTileMoves( alphabet );
        int x = 5;
    }

    private void GenerateSingleTileMoves( List<Character> rack )
    {
        // if the rack contains blank tiles, then we must check
        // every possible letter
        if ( rack.contains( '?' ) )
        {
            rack = alphabet;
        }
        
        singleTileMoves = new ArrayList<ArrayList<List<Character>>>( board.GetHeight() );
        for ( int rowIndex = 0; rowIndex < board.GetHeight(); rowIndex++ )
        {
            ArrayList<List<Character>> rowListAcross = new ArrayList<List<Character>>( board.GetWidth() );
            singleTileMoves.add( rowIndex, rowListAcross );
            for ( int columnIndex = 0; columnIndex < board.GetWidth(); columnIndex++ )
            {
                // create the array of single tile moves
                rowListAcross.add( columnIndex, new ArrayList<Character>() );
                
                // if there is already a tile in this cell, then there are no single tile moves
                if ( !board.GetCell( rowIndex, columnIndex ).IsEmpty() )
                {
                    continue;
                }

                // get the partial words extending in all directions from this cell
                String wordRight = GetWordFromHereHorz( rowIndex,
                                                        columnIndex + 1,
                                                        DIRECTION.RIGHT );
                String wordLeft = GetWordFromHereHorz( rowIndex,
                                                         columnIndex - 1,
                                                         DIRECTION.LEFT );
                String wordDown = GetWordFromHereVert( rowIndex + 1,
                                                       columnIndex,
                                                       DIRECTION.DOWN );
                String wordUp = GetWordFromHereVert( rowIndex - 1,
                                                     columnIndex,
                                                     DIRECTION.UP );

                // If all the neighboring cells are empty then all the letters should be added
                if ( wordRight.length() == 0 &&
                     wordLeft.length() == 0 &&
                     wordDown.length() == 0 &&
                     wordUp.length() == 0 )
                {
                    rowListAcross.get( columnIndex ).addAll( rack );
                    continue;
                }
                
                for ( char character : rack )
                {
                    // construct a horizontal and vertical word using
                    // the partial words and a letter from the alphabet
                    String wordHorz = wordLeft + character + wordRight;
                    String wordVert= wordUp + character + wordDown;
                    
                    // if there are partial words, then check if a word
                    // exists in the dictionary.
                    if ( ( wordHorz.length() == 1 ||
                           dictionary.ContainsWord( wordHorz ) )
                         && 
                         ( wordVert.length() == 1 ||
                           dictionary.ContainsWord( wordVert ) )
                       )
                    {
                        rowListAcross.get( columnIndex ).add( character );
                    }
                }
            }
        }
    }
    
    private String GetWordFromHereHorz( int rowIndex,
                                        int columnIndex,
                                        DIRECTION direction )
    {
        int columnIncrement = 0;
        switch ( direction )
        {
            case RIGHT:
                columnIncrement = 1;
                break;
            case LEFT:
                columnIncrement = -1;
                break;
            default:
                assert( false );   
        }

        String word = "";
        for ( ; columnIndex >= 0 && columnIndex < board.GetWidth(); columnIndex += columnIncrement )
        {
            Cell currentCell = board.GetCell( rowIndex, columnIndex );
            if ( currentCell.IsEmpty() )
            {
                return word;
            }
            else
            {
                switch ( direction )
                {
                    case RIGHT:
                        word += currentCell.GetCharacter();
                        break;
                    case LEFT:
                        word = currentCell.GetCharacter() + word;
                        break;
                    default:
                        assert( false );   
                }
            }
        }
        return word;
    }
    
    private String GetWordFromHereVert( int rowIndex,
                                        int columnIndex,
                                        DIRECTION direction )
    {
        int rowIncrement = 0;
        switch ( direction )
        {
            case DOWN:
                rowIncrement = 1;
                break;
            case UP:
                rowIncrement = -1;
                break;
            default:
                assert( false );   
        }
        
        String word = "";
        for ( ; rowIndex >= 0 && rowIndex < board.GetHeight(); rowIndex += rowIncrement )
        {
            Cell currentCell = board.GetCell( rowIndex, columnIndex );
            if ( currentCell.IsEmpty() )
            {
                return word;
            }
            else
            {
                switch ( direction )
                {
                    case DOWN:
                        word += currentCell.GetCharacter();
                        break;
                    case UP:
                        word = currentCell.GetCharacter() + word;
                        break;
                    default:
                        assert( false );   
                }
            }
        }
        return word;
    }
}
