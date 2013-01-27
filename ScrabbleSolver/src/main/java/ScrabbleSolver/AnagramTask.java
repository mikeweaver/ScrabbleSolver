package ScrabbleSolver;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

public class AnagramTask implements Callable<Set<String>>
{
    private class InitialData
    {
        public Node root;
    	public String characters;
    	public String beginsWith;
        public String contains;
        public String endsWith;
    	public int startIndex;
    	public int numCharsToTest;
    };
    InitialData initialData;
    public int loopIterations;
    public int functionCalls;
    public Set<String> matchingWords;
	 
	public AnagramTask( final Node root,
						final String characters,
						final String beginsWith,
						final String contains,
						final String endsWith,
						int startIndex,
						int numCharsToTest )
	{
	    assert( characters != null );
	    assert( characters.length() > 0 );
        assert( numCharsToTest <= characters.length() );
        assert( startIndex >= 0 );
        assert( startIndex < numCharsToTest );
	    
	    initialData = new InitialData();
	    initialData.root = root;
	    initialData.characters = characters.toLowerCase();
	    
	    // convert beginsWith, contains and endsWith to lower case and empty strings to null
	    if ( beginsWith != null &&
	         beginsWith.length() > 0 )
	    {
	        initialData.beginsWith = beginsWith.toLowerCase();
	    }
	    else
	    {
	        initialData.beginsWith = null;
	    }

        if ( contains != null &&
             contains.length() > 0 )
        {
            initialData.contains = contains.toLowerCase();
        }
        else
        {
            initialData.contains = null;
        }

        if ( endsWith != null &&
             endsWith.length() > 0 )
        {
            initialData.endsWith = endsWith.toLowerCase();
        }
        else
        {
            initialData.endsWith = null;
        }
        
        // if beginsWith == contains or endsWith == contains
        // then there is no point in specifying the contains
        // as the matching words, if found, will contain the contains
        if ( initialData.beginsWith == initialData.contains )
        {
            initialData.contains = null;
        }
        else if ( initialData.endsWith == initialData.contains )
        {
            initialData.contains = null;
        }
        
	    initialData.startIndex = startIndex;
		initialData.numCharsToTest = numCharsToTest;
		loopIterations = 0;
		functionCalls = 0;
		matchingWords = new TreeSet<String>();
	}
	
	@Override
	public Set<String> call()
	{
	    if ( initialData.beginsWith != null &&
	         initialData.beginsWith.length() > 0 )
	    {
	        // traverse the tree using the starting string
	        Node localRoot = Dictionary.GetMatchingNode( initialData.root, initialData.beginsWith );

	        // if we matched the prefix, then check for anagrams
	        // in the sub-tree
	        if ( localRoot != null )
	        {
                // the prefix may be a word all by itself!
	            // but only if we don't have requirements that the word
	            // must end or contain something else!
                if ( localRoot.endOfWord &&
                     ( initialData.endsWith == null ||
                       initialData.endsWith.length() == 0 ) &&
                     ( initialData.contains == null ||
                       initialData.contains.length() == 0 ) )
                {
                    matchingWords.add( initialData.beginsWith );
                }
                
	            // if the prefix is not at the edge of the tree, then start
                // a search from this point
	            if ( localRoot.center != null )
	            {
	                FindAnagrams( localRoot.center,
                                  initialData.characters,
                                  initialData.beginsWith,
                                  initialData.contains,
                                  initialData.endsWith,
                                  initialData.startIndex,
                                  initialData.numCharsToTest );
	            }
	        }
	    }
	    else
	    {
    		FindAnagrams( initialData.root,
            		      initialData.characters,
            		      "",
                          initialData.contains,
                          initialData.endsWith,
            		      initialData.startIndex,
            		      initialData.numCharsToTest );
	    }
		
		return matchingWords;
	}
	
	private void FindAnagrams( final Node localRoot,
                               final String characters,
                               final String wordSoFar,
                               final String contains,
                               final String endsWith,
                               int startIndex,
                               int numCharsToTest )
    {
        assert( numCharsToTest <= characters.length() );
        assert( startIndex >= 0 );
        assert( startIndex < numCharsToTest );
        
        // track how many function calls we have done
        functionCalls++;

        // if we have a contains string, see if there is a word that starts with wordSoFar and
        // continues with the contains string
        if ( contains != null &&
             contains.length() > 0 )
        {
            Node containsNode = Dictionary.GetMatchingNode( localRoot,
                                                            contains );
            if ( containsNode != null )
            {
                // we got a match, but only if the word doesn't have to end
                // with a specific suffix
                if ( containsNode.endOfWord &&
                     ( endsWith == null ||
                       endsWith.length() == 0 ) )
                {
                    matchingWords.add( wordSoFar + contains );
                }
                
                // continue looking for other words that match AFTER the contains string
                // remove the contains string from the argument list so that any words
                // matched after this point, will be added to the list
                FindAnagrams( containsNode.center,
                              characters,
                              wordSoFar + contains,
                              null,
                              endsWith,
                              0,
                              characters.length() );
            }
        }

        for ( int charIndex = startIndex; charIndex < ( startIndex + numCharsToTest ); charIndex++ )
        {
            // track how many iterations we have done across all function calls
            loopIterations++;
            
            // get the current character
            char currentCharacter = characters.charAt( charIndex );
            
            // if it is a wild card 
            if ( currentCharacter == '?' )
            {
                // call FindAnagrams once for each letter in the alphabet,
                // replacing the wild card with the current letter of the alphabet
                for ( char letter='a'; letter <= 'z'; letter++ )
                {
                    FindAnagrams( localRoot,
                                  characters.replaceFirst( "\\?", String.valueOf( letter ) ),
                                  wordSoFar,
                                  contains,
                                  endsWith,
                                  startIndex,
                                  numCharsToTest );
                }
                
                // skip the wild card character and continue the loop
                continue;
            }
            
            // traverse the dictionary tree and find a node that matches this character
            Node matchingNode = Dictionary.GetMatchingNode( localRoot, currentCharacter );
            
            // if we didn't find a match, then move on to the next letter
            if ( matchingNode == null )
            {
                continue;
            }
            
            // if we have a word terminator, check to see if this string ends with the terminator
            // also, since the word MUST end with this terminator, there is no sense in checking
            // if this node is the end of a word
            
            if ( endsWith != null &&
                 endsWith.length() > 0 )
            {
                // if we have a contains string that wasn't matched, then don't bother checking endsWith
                // the contains string must be matched first
                if ( contains == null ||
                     contains.length() == 0 )
                {
                    Node endsWithNode = Dictionary.GetMatchingNode( matchingNode.center,
                                                                    endsWith );
                    
                    if ( endsWithNode != null &&
                         endsWithNode.endOfWord )
                    {
                        // we got a match, add this to the list of matching words
                        matchingWords.add( wordSoFar + currentCharacter + endsWith );
                    }
                }
                // continue looking for other matching words
            }
            else if ( matchingNode.endOfWord )
            {
                // if we have a contains string that wasn't matched, then this word should
                // not be added to the list, because it MUST contain the contains string
                if ( contains == null ||
                     contains.length() == 0 )
                {
                    // we got a match, add this to the list of matching words
                    matchingWords.add( wordSoFar + currentCharacter );
                }
                // continue looking for other matching words
            }
            if ( matchingNode.center != null )
            {
                if ( characters.length() > 1 )
                {
                    String substring = characters.substring( 0, charIndex ) + characters.substring( charIndex + 1 );
                    FindAnagrams( matchingNode.center,
                                  substring,
                                  wordSoFar + currentCharacter,
                                  contains,
                                  endsWith,
                                  0,
                                  substring.length() );
                }
                else if ( contains != null &&
                          contains.length() > 0 &&
                          ( endsWith == null ||
                          endsWith.length() == 0 ) )
                {
                    // if we have a contains string, and no end string, and 
                    // we have used up all of the letters in the character set,
                    // then see if there is a word that ends with the contains string
                    Node containsNode = Dictionary.GetMatchingNode( matchingNode.center,
                                                                    contains );
                    if ( containsNode != null &&
                         containsNode.endOfWord )
                    {
                        // we got a match, add this to the list of matching words
                        matchingWords.add( wordSoFar + currentCharacter + contains );
                    }
                }
            }
        }
    }
}
