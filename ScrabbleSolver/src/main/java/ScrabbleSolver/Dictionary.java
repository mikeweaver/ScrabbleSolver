package ScrabbleSolver;
import java.util.regex.*;
import java.io.*;


public class Dictionary
{
	public Node root = null;
	
	public Dictionary()
	{
	}
	
	public void LoadDictionaryFromTextFile( final String pathToWordFile ) throws FileNotFoundException, IOException
	{
		assert( pathToWordFile != null );
		assert( pathToWordFile.length() != 0 );
		System.out.println( "Loading dictionary from text file: " + pathToWordFile );
		
		//FileInputStream fileStream = new FileInputStream( pathToWordFile );
		InputStream fileStream = Dictionary.class.getResourceAsStream( pathToWordFile );
		DataInputStream inputSream = new DataInputStream( fileStream );
		BufferedReader reader = new BufferedReader( new InputStreamReader( inputSream ) );
		
		// delete the existing dictionary tree
		root = null;
		
		// create a pattern matcher to validate the input
		// only characters are accepted
		Pattern pattern = Pattern.compile("^[a-z|A-Z]+$");
		Matcher matcher = pattern.matcher("");
		
		// read File Line By Line
        String word;
        int wordCount = 0;
        while ( ( word = reader.readLine() ) != null )
        {
            // load word into matcher
            matcher.reset( word );
            
            // check for a match
            if ( matcher.matches() )
            {
                AddWord( word.toLowerCase() );
                wordCount++;
            }
            else
            {
                System.out.println( "Skipped word " + word + ", it contains non-alpha characters" );
            }
        }
        
		System.out.println( "Added " + wordCount + " words to dictionary" );
		System.out.println( CountNodes( root ) + " nodes in tree" );
        System.out.println( "Balancing the tree" );
		root = RebalanceTree( root );
		System.out.println( "Done" );
		
		//Close the input stream
		reader.close();
		inputSream.close();
		fileStream.close();
	}
	
	public void LoadDictionaryFromBinaryFile( final String pathToBinaryFile ) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		assert( pathToBinaryFile != null );
		assert( pathToBinaryFile.length() != 0 );
		System.out.println( "Loading dictionary from binary file: " + pathToBinaryFile );

		FileInputStream fileStream = new FileInputStream( pathToBinaryFile );
	    ObjectInputStream objectStream = new ObjectInputStream( fileStream );
	    
	    // delete the existing dictionary tree
	    root = null;
	 		
	    root = (Node)objectStream.readObject();
	    
	    objectStream.close();
	    fileStream.close();
		System.out.println( "Done" );
	}
	
	public void SaveDictionaryToBinaryFile( final String pathToBinaryFile ) throws FileNotFoundException, IOException
	{
		assert( pathToBinaryFile != null );
		assert( pathToBinaryFile.length() != 0 );
		assert( root != null );
		System.out.println( "Saving dictionary to file: " + pathToBinaryFile );

	    FileOutputStream fileStream = new FileOutputStream( pathToBinaryFile );
	    ObjectOutputStream objectStream = new ObjectOutputStream( fileStream );
	    objectStream.writeObject( root );
	    objectStream.close();
	    fileStream.close();
		System.out.println( "Done" );
	}
	
	/*
	 * Add a word to the dictionary
	 */
	public void AddWord( final String word )
	{
		assert( word != null );
		assert( word.length() > 0 );
		//System.out.println( "Adding word: " + word );

		if ( root == null )
		{
			root = CreateNodes( word, 0 );
			return;
		}
		
		Node currentNode = root;
		int charIndex = 0;
		while ( charIndex < word.length() )
		{
			char currentCharacter = word.charAt( charIndex );

			if ( currentCharacter == currentNode.character )
			{
				if ( charIndex == word.length() - 1 )
				{
					currentNode.endOfWord = true;
					return;
				}
				else if ( currentNode.center == null )
				{
					currentNode.center = CreateNodes( word, charIndex + 1 );
					return;
				}
				else
				{
					currentNode = currentNode.center;
					charIndex++;
				}
			}
			else if ( currentCharacter < currentNode.character )
			{
				if ( currentNode.left == null )
				{
					currentNode.left = CreateNodes( word, charIndex );
					return;
				}
				else
				{
					currentNode = currentNode.left;
				}
			}
			else
			{
				if ( currentNode.right == null )
				{
					currentNode.right = CreateNodes( word, charIndex );
					return;
				}
				else
				{
					currentNode = currentNode.right;
				}
			}
		}
	}
    
    public boolean ContainsWord( String word )
    {
        Node currentNode = GetMatchingNode( root,
                                            word.toLowerCase() );
        if ( currentNode == null )
        {
            return false;
        }
        else
        {
            return currentNode.endOfWord;
        }
    }
	
	/*
	 * Create the nodes for a string
	 */
	private Node CreateNodes( final String word,
							  int charIndex )
	{
		Node localRoot = new Node( word.charAt( charIndex ), null, null, null );
		Node currentNode = localRoot;
		charIndex++;
		while ( charIndex < word.length() )
		{
			currentNode.center = new Node( word.charAt( charIndex ), null, null, null );
			currentNode = currentNode.center;
			charIndex++;
		}
		currentNode.endOfWord = true;
		return localRoot;
	}
	
	static public Node GetMatchingNode( final Node localRoot,
							            char character )
	{
	    assert( localRoot != null );
	    
		Node currentNode = localRoot;
		while ( currentNode != null )
		{
			if ( character == currentNode.character )
			{
				return currentNode;
			}
			else if ( character < currentNode.character )
			{
				currentNode = currentNode.left;
			}
			else
			{
				currentNode = currentNode.right;
			}
		}
		return null;
	}
	
	static public Node GetMatchingNode( final Node localRoot,
		      					  	    final String characters )
	{
        assert( localRoot != null );
        assert( characters.length() > 0 );
        
		Node currentNode = localRoot;
		for ( Integer i = 0; currentNode != null; i++  )
		{
			currentNode = GetMatchingNode( currentNode,
										   characters.charAt( i ) );
			if ( i == characters.length() - 1 )
            {
                return currentNode;
            }
            else if ( currentNode != null )
            {
                currentNode = currentNode.center;
            }
		}
		return currentNode;
	}
	
	static private Node RebalanceTree( final Node localRoot )
	{
	    if ( localRoot == null )
	    {
	        return null;
	    }
	    
	    // rotate tree until hieghts are only different by 1
	    int heightDifference = 0;
	    Node newRoot = localRoot;
	    do
	    {
    	    int leftHeight = CalculateHeight( newRoot.left );
            int rightHeight = CalculateHeight( newRoot.right );
            heightDifference = leftHeight - rightHeight;
            
    	    if ( heightDifference < -1 )
    	    {
    	        Node pivot = newRoot.right;
    	        newRoot.right = pivot.left;
    	        pivot.left = newRoot;
    	        newRoot = pivot;
    	    }
    	    else if ( heightDifference > 1 )
    	    {
                Node pivot = newRoot.left;
                newRoot.left = pivot.right;
                pivot.right = newRoot;
                newRoot = pivot;
    	    }
	    }
	    while( heightDifference < -1 || heightDifference > 1 );
	    
	    // balance the two sub trees
	    newRoot.left = RebalanceTree( newRoot.left );
	    newRoot.right = RebalanceTree( newRoot.right );
	    
	    return newRoot;
	}
	
	static private int CalculateHeight( final Node localRoot )
	{
	    if ( localRoot == null )
        {
            return 0;
        }
        else
        {
            int leftHeight = CalculateHeight( localRoot.left );
            int rightHeight = CalculateHeight( localRoot.right );
            if ( leftHeight > rightHeight )
            {
                return leftHeight + 1;
            }
            else
            {
                return rightHeight + 1; 
            }
        }
	}
	
	static private int CountNodes( final Node localRoot )
	{
	    if ( localRoot == null )
	    {
	        return 0;
	    }
	    else
	    {
	        return 1 + CountNodes( localRoot.left ) + CountNodes( localRoot.center ) + CountNodes( localRoot.right );
	    }
	}
}
