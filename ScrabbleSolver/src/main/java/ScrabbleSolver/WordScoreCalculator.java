package ScrabbleSolver;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class WordScoreCalculator
{
    private static final Map<Character, Integer> LETTER_SCORES = new HashMap<Character, Integer>( 28 ) {{
        put( 'a', 1 );
        put( 'b', 3 );
        put( 'c', 3 );
        put( 'd', 2 );
        put( 'e', 1 );
        put( 'f', 4 );
        put( 'g', 2 );
        put( 'h', 4 );
        put( 'i', 1 );
        put( 'j', 8 );
        put( 'k', 5 );
        put( 'l', 1 );
        put( 'm', 3 );
        put( 'n', 1 );
        put( 'o', 1 );
        put( 'p', 3 );
        put( 'q', 10 );
        put( 'r', 1 );
        put( 's', 1 );
        put( 't', 1 );
        put( 'u', 1 );
        put( 'v', 4 );
        put( 'w', 4 );
        put( 'x', 8 );
        put( 'y', 4 );
        put( 'z', 10 );
        put( '?', 0 );
    }};
    
    public static int CalculateScore( final String word )
    {
        int score = 0;
        for ( char c : word.toCharArray() )
        {
            Integer letterScore = LETTER_SCORES.get( c );
            assert( letterScore != null );
            score += letterScore;
        }
        return score;
    }
    
    public static List<ScoredWord> CalculateScores( final Collection<String> listOfWords )
    {
        assert( listOfWords != null );
        
        List<ScoredWord> scoredWords = new ArrayList<ScoredWord>( listOfWords.size() );
        
        for ( String word : listOfWords )
        {
            scoredWords.add( new ScoredWord( word ) );
        }
        
        return scoredWords;
    }
}
