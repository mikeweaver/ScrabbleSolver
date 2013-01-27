package ScrabbleSolverWeb.ScrabbleSolverWeb;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.*;

import ScrabbleSolver.AnagramService;
import ScrabbleSolver.Dictionary;
import ScrabbleSolver.WordScoreCalculator;
import ScrabbleSolver.ScoredWord;

public class ScrabbleServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String characters = req.getParameter("characters");
        String beginsWith = req.getParameter("beginsWith");
        String contains = req.getParameter("contains");
        String endsWith = req.getParameter("endsWith");
        
        // find the anagrams
        Set<String> listOfWords = Main.FindAnagrams( characters, beginsWith, contains, endsWith );
        
        // score the words
        List<ScoredWord> scoredWords = WordScoreCalculator.CalculateScores( listOfWords );
        
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println( "Found " + scoredWords.size() + " words for: " + characters + "<br/>" );
        if ( beginsWith != null &&
             beginsWith.length() > 0 )
        {
            out.println( "That begin with: " + beginsWith + "<br/>" );
        }
        if ( contains != null &&
             contains.length() > 0 )
        {
            out.println( "That contain: " + contains + "<br/>" );
        }
        if ( endsWith != null &&
             endsWith.length() > 0 )
        {
            out.println( "That end with: " + endsWith + "<br/>" );
        }

        Iterator<ScoredWord> iterator = scoredWords.iterator();
        while ( iterator.hasNext() )
        {
            ScoredWord scoredWord = iterator.next();
            out.println( scoredWord.word + "&nbsp;(" + Integer.toString( scoredWord.score ) + ")<br/>");
        }
        
        out.println("</body>");
        out.println("</html>");
    }
}