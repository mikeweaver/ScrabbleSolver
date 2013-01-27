package ScrabbleSolverWeb.ScrabbleSolverWeb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.webapp.WebAppContext;

import ScrabbleSolver.AnagramService;
import ScrabbleSolver.Dictionary;

/**
 * 
 * This class launches the web application in an embedded Jetty container.
 * This is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 * @author John Simone
 */
public class Main {
    
    private static AnagramService anagramService = null;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        String webappDirLocation = "src/main/webapp/";
        
        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation+"/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);
        
        //Parent loader priority is a class loader setting that Jetty accepts.
        //By default Jetty will behave like most web containers in that it will
        //allow your application to replace non-server libraries that are part of the
        //container. Setting parent loader priority to true changes this behavior.
        //Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);
        
        server.setHandler(root);
        
        // construct a dictionary and anagram service
        Dictionary dictionary = LoadDictionary( "/twl.txt" );
        anagramService = new AnagramService( dictionary );
        
        server.start();
        server.join();
        
        
    }
    
    private static Dictionary LoadDictionary( final String pathToDictionaryFile ) throws FileNotFoundException, IOException
    {
        long startTime = System.currentTimeMillis();
        Dictionary dictionary = new Dictionary();
        dictionary.LoadDictionaryFromTextFile( pathToDictionaryFile );
        long endTime = System.currentTimeMillis();
        System.out.println( "Loaded dictionary in " + ( endTime - startTime ) + "ms :" );
        return dictionary;
    }
    
    public static Set<String> FindAnagrams( final String characters,
                                            final String beginsWith,
                                            final String contains,
                                            final String endsWith ) throws FileNotFoundException, IOException
    {
        assert( anagramService != null );
        
        long startTime = System.currentTimeMillis();
        Set<String> listOfWords = anagramService.FindAnagrams( characters,
                                                               beginsWith,
                                                               contains,
                                                               endsWith );
        long endTime = System.currentTimeMillis();
        System.out.println( "Found " + listOfWords.size() + " anagrams in " + ( endTime - startTime ) + "ms" );
        return listOfWords;
    }
}
