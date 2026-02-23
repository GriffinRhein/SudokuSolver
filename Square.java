public class Square
{
	// If result is finalized, result is not null and possArray is null
	// If result is not finalized, result is null and possArray is not null

	Integer result; // The final number that you see
	Integer[] possArray; // Every possible number it could still be

	boolean answerAtStart; // Whether the result was entered by the user

	int numPossLeft; // Number of possibilities currently left

	int ownRow, ownCol, ownBox;

	private static int[][] findBox = new int[][]{ {0,1,2}, {3,4,5}, {6,7,8} };

	Integer stepForResult;
	Integer[] stepForPossOut;

	Integer lastResortResult;
	boolean lastResortIsResultFixed;

	String getUserCoordinateString()
	{
		return "["+String.valueOf(ownRow+1)+"]["+String.valueOf(ownCol+1)+"]";
	}

	boolean sameSquare(Square otherSquare)
	{
		return (this.ownRow == otherSquare.ownRow && this.ownCol == otherSquare.ownCol);
	}

	// Constructor

	Square(int selfRow, int selfCol)
	{
		// Initialize variables to that of an unsolved square.
		// If the result is entered directly by the user before the
		// solving takes place, FullSudoku will adjust these.

		result = null;
		answerAtStart = false;

		// Fix own row and column

		ownRow = selfRow;
		ownCol = selfCol;
		ownBox = findBox[selfRow/3][selfCol/3];

		// Set full range of possibilities

		numPossLeft = 9;
		possArray = new Integer[9];

		// Every possibility is its index plus 1

		for(int i=0;i<9;i++)
		{ possArray[i] = i+1; }

		// As no solving has happened yet, records for the
		// steps where everything happened are all null

		stepForResult = null;
		stepForPossOut = new Integer[]{null,null,null,null,null,null,null,null,null};
	}

} // Square