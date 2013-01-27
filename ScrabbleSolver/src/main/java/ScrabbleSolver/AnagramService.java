package ScrabbleSolver;
import java.util.concurrent.*;
import java.util.*;

public class AnagramService
{
    Dictionary dictionary;
    ExecutorService threadPool;jj
    
    public AnagramService( Dictionary dictionary )
    {
        this.dictionary = dictionary;
        threadPool = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
    }
    
    protected void finalize()
    {
        threadPool.shutdown(); // Disable new tasks from being submitted
        try {
          // Wait a while for existing tasks to terminate
          if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
              threadPool.shutdownNow(); // Cancel currently executing tasks
            // Wait a while for tasks to respond to being cancelled
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
                System.err.println("Pool did not terminate");
          }
        } catch (InterruptedException ie) {
          // (Re-)Cancel if current thread also interrupted
            threadPool.shutdownNow();
          // Preserve interrupt status
          Thread.currentThread().interrupt();
        }
    }
    
    public Set<String> FindAnagrams( final String characters,
                                     final String beginsWith,
                                     final String contains,
                                     final String endsWith )
    {
        System.out.println( "Finding anagrams for: " + characters );
        if ( beginsWith != null &&
             beginsWith.length() > 0 )
        {
            System.out.println( "That begin with: " + beginsWith );
        }
        if ( contains != null &&
             contains.length() > 0 )
        {
            System.out.println( "That contain: " + contains );
        }
        if ( endsWith != null &&
             endsWith.length() > 0 )
        {
            System.out.println( "That end with: " + endsWith );
        }
        
        Set<String> matchingWords = FindAnagrams( dictionary.root,
                                                  beginsWith,
                                                  contains,
                                                  endsWith,
                                                  characters );
        System.out.println( matchingWords.size() + " anagrams found" );      
        
        return matchingWords;
    }
        
    private Set<String> FindAnagrams( final Node root,
                                      final String beginsWith,
                                      final String contains,
                                      final String endsWith,
                                      final String characters ) 
    {
        // time the search for the words
        long startTime = System.currentTimeMillis();
        
        Set<String> matchingWords = new TreeSet<String>();

        List<AnagramTask> taskList = new ArrayList<AnagramTask>();
        for ( int charIndex = 0; charIndex < characters.length(); charIndex++ )
        {
            AnagramTask task = new AnagramTask( root,
                                                characters,
                                                beginsWith,
                                                contains,
                                                endsWith,
                                                charIndex,
                                                1 );
            taskList.add( task );
        }
        
        try
        {
            List<Future<Set<String>>> taskResultList = threadPool.invokeAll( taskList );
        
            for ( Future<Set<String>> taskResult : taskResultList )
            {
                assert( !taskResult.isCancelled() );
                assert( taskResult.isDone() );
                
                matchingWords.addAll( taskResult.get() );
            }
            
            int loopIterations = 0;
            int functionCalls = 0;
            for ( AnagramTask task : taskList )
            {
                loopIterations += task.loopIterations;
                functionCalls += task.functionCalls;
            }
            
            // record the end time
            long endTime = System.currentTimeMillis();
            System.out.println( "loopIterations: " + loopIterations );
            System.out.println( "functionCalls: " + functionCalls );
            System.out.println( "Found " + matchingWords.size() + " anagrams in " + ( endTime - startTime ) + "ms" );
        }
        catch( Exception e )
        {
            System.out.println( "Exception occurred" );
        }
        
        return matchingWords;
    }
}
