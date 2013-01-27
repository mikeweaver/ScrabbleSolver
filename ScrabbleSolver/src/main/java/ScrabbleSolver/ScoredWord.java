package ScrabbleSolver;

public class ScoredWord
{
    public String word;
    public int score;
    
    public ScoredWord( final String word )
    {
        assert( word != null );
        this.word = word;
        score = WordScoreCalculator.CalculateScore( word );
    }
}
