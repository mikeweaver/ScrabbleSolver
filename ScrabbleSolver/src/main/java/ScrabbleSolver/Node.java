package ScrabbleSolver;
import java.io.Serializable;

public class Node implements Serializable
{
	public Node( char character,
				 Node left,
				 Node center,
				 Node right)
	{
		this.character = character;
		this.left = left;
		this.center = center;
		this.right = right;
		this.endOfWord = false;
	}
	
	char character;
	boolean endOfWord;
	Node left;
	Node center;
	Node right;
}
