public class BoxTranslator
{
	static private int[][] coolBoxCoordinates = new int[][]{{0,1,2,0,1,2},
															{0,1,2,3,4,5},
															{0,1,2,6,7,8},
															{3,4,5,0,1,2},
															{3,4,5,3,4,5},
															{3,4,5,6,7,8},
															{6,7,8,0,1,2},
															{6,7,8,3,4,5},
															{6,7,8,6,7,8} };

	// Box indicated with int from 0-8
	// Square indicated with int from 0-8

	static public int rowOfBoxSquare(int boxInQuestion, int squareInThatBox)
	{
		return coolBoxCoordinates[boxInQuestion][squareInThatBox / 3];
	}

	static public int colOfBoxSquare(int boxInQuestion, int squareInThatBox)
	{
		return coolBoxCoordinates[boxInQuestion][(squareInThatBox % 3) + 3];
	}


	static public boolean doesRowOverlapBox(int boxInQuestion, int rowInQuestion)
	{
		if(rowInQuestion != coolBoxCoordinates[boxInQuestion][0])
		{
			if(rowInQuestion != coolBoxCoordinates[boxInQuestion][1])
			{
				if(rowInQuestion != coolBoxCoordinates[boxInQuestion][2])
				{
					return false;
				}
			}
		}

		return true;
	}

	static public boolean doesColOverlapBox(int boxInQuestion, int colInQuestion)
	{
		if(colInQuestion != coolBoxCoordinates[boxInQuestion][3])
		{
			if(colInQuestion != coolBoxCoordinates[boxInQuestion][4])
			{
				if(colInQuestion != coolBoxCoordinates[boxInQuestion][5])
				{
					return false;
				}
			}
		}

		return true;
	}

} // BoxTranslator class