package ScrabbleSolver;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {

	private enum DictionaryType
	{
		TEXT,
		BINARY
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//DictionaryTest.TestAnagram();
		
		try
		{
			if ( args.length < 3 ||
			     args[0].length() != 2 ||
				 args[0].charAt(0) != '-' ||
			     args[1].length() == 0 ||
			     args[2].length() == 0 )
			{
				PrintUsage();
				return;
			}
			
			
			
			switch ( args[0].charAt(1) )
			{
				case 'c':
					CompileDictionary( args[1], args[2] );
					return;
				case 't':
					FindAnagrams( DictionaryType.TEXT,
					              args[1],
                                  args.length >= 4 ? args[3] : null,
                                  args.length >= 5 ? args[4] : null,
                                  args.length >= 6 ? args[5] : null,
								  args[2] );
					return;
                case 'b':
					FindAnagrams( DictionaryType.BINARY,
                                  args[1],
							  	  args.length >= 4 ? args[3] : null,
		                          args.length >= 5 ? args[4] : null,
		                          args.length >= 6 ? args[5] : null,
							  	  args[2] );
					return;
				default:
					PrintUsage();
					return;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private static void CompileDictionary( final String pathToWordFile,
										   final String pathToBinaryFile ) throws FileNotFoundException, IOException
	{
		Dictionary dictionary = new Dictionary();
		dictionary.LoadDictionaryFromTextFile( pathToWordFile );
		dictionary.SaveDictionaryToBinaryFile( pathToBinaryFile );
	}
	
	private static Dictionary LoadDictionary( DictionaryType dictionaryType,
									          final String pathToDictionaryFile ) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		long startTime = System.currentTimeMillis();
		Dictionary dictionary = new Dictionary();
		switch ( dictionaryType )
		{
			case TEXT:
				dictionary.LoadDictionaryFromTextFile( pathToDictionaryFile );
				break;
			case BINARY:
				dictionary.LoadDictionaryFromBinaryFile( pathToDictionaryFile );
				break;
			default:
				assert( false );
		}
		long endTime = System.currentTimeMillis();
		System.out.println( "Loaded dictionary in " + ( endTime - startTime ) + "ms :" );
		return dictionary;
	}
	
	private static void FindAnagrams( DictionaryType dictionaryType,
	                                  final String pathToDictionaryFile,
	                                  final String beginsWith,
                                      final String contains,
                                      final String endsWith,
			  						  final String word ) throws FileNotFoundException, IOException, ClassNotFoundException, ExecutionException, InterruptedException
    {
	    Dictionary dictionary = LoadDictionary( dictionaryType, pathToDictionaryFile );
	    
	    //ScrabbleSolver solver = new ScrabbleSolver( dictionary );
	    
	    
        AnagramService anagramService = new AnagramService( dictionary );
		
        // find the words
 	    Set<String> listOfWords = anagramService.FindAnagrams( word,
                                                		       beginsWith,
                                                		       contains,
                                                		       endsWith );

		// score the words we found
		List<ScoredWord> scoredWords = WordScoreCalculator.CalculateScores( listOfWords );

		// print out the list of score words
		System.out.println( "Words: " );
		Iterator<ScoredWord> iterator = scoredWords.iterator();
		while ( iterator.hasNext() )
		{
		    ScoredWord scoredWord = iterator.next();
			System.out.println( scoredWord.word + " (" + Integer.toString( scoredWord.score ) + ")" );
		}
		
		// finalize the service to stop the threads
		anagramService.finalize();
		anagramService = null;
	}
	
	private static void PrintUsage()
	{
		System.out.println( "USAGE: -c [path to word file] [path to save dictionary to]" );
		System.out.println( "USAGE: -t [path to text dictionary file] [anagram to check] [begins with (optional)] [contains (optional)] [ends with (optional)]" );
		System.out.println( "USAGE: -b [path to binary dictionary file] [anagram to check] [begins with (optional)] [contains (optional)] [ends with (optional)]" );
	}

}
