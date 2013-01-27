package ScrabbleSolverWeb.ScrabbleSolverWeb;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.*;

import ScrabbleSolver.AnagramService;
import ScrabbleSolver.Dictionary;
import ScrabbleSolver.WordScoreCalculator;
import ScrabbleSolver.ScoredWord;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

public class ScrabbleRest extends HttpServlet
{
    private final static String FIELD_CHARACTERS = "characters";
    private final static String FIELD_BEGINS_WITH = "beginsWith";
    private final static String FIELD_CONTAINS = "contains";
    private final static String FIELD_ENDS_WITH = "endsWith";
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        List<ScoredWord> scoredWords;
        String queryString = req.getQueryString();
        if ( queryString == null ||
             queryString.length() == 0 )
        {
            scoredWords = new ArrayList<ScoredWord>();
        }
        else
        {
            // parse the JSON
            JSONObject obj = (JSONObject)JSONSerializer.toJSON( queryString );
    
            // get the arguments
            String characters = obj.getString( FIELD_CHARACTERS );
            String beginsWith = obj.optString( FIELD_BEGINS_WITH, "" );
            String contains = obj.optString( FIELD_CONTAINS, "" );
            String endsWith = obj.optString( FIELD_ENDS_WITH, "" );
            
            // find the anagrams
            Set<String> listOfWords = Main.FindAnagrams( characters, beginsWith, contains, endsWith );
            
            // score the words
            scoredWords = WordScoreCalculator.CalculateScores( listOfWords );
        }
        
        // convert list to JSON
        JsonConfig config = new JsonConfig();
        config.setArrayMode( JsonConfig.MODE_OBJECT_ARRAY );
        config.setIgnorePublicFields( false );
        JSON jsonObj = JSONSerializer.toJSON( scoredWords, config );
        
        // write the response
        resp.addHeader( "Access-Control-Allow-Origin", "*" );
        jsonObj.write( resp.getWriter() );
    }
}