package ScrabbleSolverWeb.ScrabbleSolverWeb;

import java.io.*;
import java.net.*;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import net.htmlparser.jericho.*;

import ScrabbleSolver.ScoredWord;

public class Definition extends HttpServlet
{
    private final static String FIELD_WORD = "word";
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        List<ScoredWord> scoredWords;
        String queryString = req.getQueryString();
        if ( queryString == null ||
             queryString.length() == 0 )
        {
            
        }
        else
        {
         // parse the JSON
            JSONObject obj = (JSONObject)JSONSerializer.toJSON( queryString );
    
            // get the arguments
            String word = obj.getString( FIELD_WORD );
            
            System.out.println("Retrieving definition for '" + word + "'");

            URL url = new URL("http://dictionary.reference.com/browse/" + word.toLowerCase() );
            Source source = new Source( url );

                    
            resp.getWriter().write( "<html><head><link rel=\"stylesheet\" href=\"/css/Definition.css\" /></head><body>" );
            List<Element> elements = source.getAllElements("div");
            for ( Element element : elements )
            {
                String cssClass = element.getAttributeValue("class");
                if ( cssClass != null &&
                     cssClass.equalsIgnoreCase( "body" ) )
                {
                    
                    boolean inList = false;
                    boolean foundPartOfSpeech = false;
                    List<Element> subElements = element.getAllElements();
                    for ( Element subElement : subElements )
                    {

                        cssClass = subElement.getAttributeValue("class");
                        String tagName = subElement.getStartTag().getName();
                        if ( foundPartOfSpeech &&
                             tagName.equalsIgnoreCase( "div" ) &&
                             cssClass != null &&
                             cssClass.equalsIgnoreCase( "dndata" ) )
                        {
                            if ( !inList )
                            {
                                inList = true;
                                resp.getWriter().write( "<ol>" );
                            }
                            resp.getWriter().write( "<li>" );
                            subElement.getTextExtractor().writeTo( resp.getWriter() );
                            resp.getWriter().write( "</li>" );
                            continue;
                        }
                        else if ( tagName.equalsIgnoreCase( "span" ) &&
                                  cssClass != null &&
                                  cssClass.equalsIgnoreCase( "pg" ) )
                        {
                            if ( inList )
                            {
                                inList = false;
                                resp.getWriter().write( "</ol>" );
                            }
                            foundPartOfSpeech = true;
                            resp.getWriter().write( "<h2>" + word + "</h2><p><b>" );
                            subElement.getTextExtractor().writeTo( resp.getWriter() );
                            resp.getWriter().write( "</b></p>" );
                            continue;
                        }
                    }
                    
                    if ( inList )
                    {
                        inList = false;
                        resp.getWriter().write( "</ol>" );
                    }
                }
            }
            resp.getWriter().write( "</body></html>" );
        }
    }
}
