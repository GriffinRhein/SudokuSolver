public class CheckStartingNums
{
	private String[][] stringArray;

	CheckStartingNums(String[][] b)
	{
		stringArray = b;
	}


	boolean isEntryDupeFree()
	{
		boolean areWeDupeFree = true;
		int currentRCB = 0; 

		while(areWeDupeFree && currentRCB < 9)
		{
			areWeDupeFree = checkOneRCB(provideStringRow(currentRCB));
			currentRCB++;
		}

		currentRCB = 0;

		while(areWeDupeFree && currentRCB < 9)
		{
			areWeDupeFree = checkOneRCB(provideStringCol(currentRCB));
			currentRCB++;
		}

		currentRCB = 0;

		while(areWeDupeFree && currentRCB < 9)
		{
			areWeDupeFree = checkOneRCB(provideStringBox(currentRCB));
			currentRCB++;
		}

		return areWeDupeFree;

	} // isSudokuDupeFree()


	boolean checkOneRCB(String[] theRCB)
	{
		// Create array of booleans, and fill it with false

		boolean[] allPoss = new boolean[9];

		for(int a=0;a<9;a++)
		{
			allPoss[a] = false;
		}


		Integer currentResult;

		// Check the result of each square. If that result has not appeared before,
		// set its corresponding boolean in allPoss to true. If the result has
		// appeared before, areWeDupeFree is false.

		for(int a=0;a<9;a++)
		{
			if(theRCB[a] != "")
			{
				currentResult = Integer.valueOf(theRCB[a]);

				if(allPoss[currentResult - 1] == false)
				{
					allPoss[currentResult - 1] = true;
				}

				else
				{
					return false;
				}
			}
		}

		return true;

	} // checkForDupesOneRCB()


	boolean areThereSeventeenClues()
	{
		int myCounter = 0;

		for(int a=0;a<9;a++)
		{
			for(int b=0;b<9;b++)
			{
				if(stringArray[a][b] != "")
					myCounter++;
			}
		}

		if(myCounter < 17)
			return false;

		return true;

	} // areThereSeventeenClues()


	// Provide a row of Strings

	private String[] provideStringRow(int rowNum)
	{
		String[] rowProvided = new String[9];

		for(int i=0;i<9;i++)
		{
			rowProvided[i] = stringArray[rowNum][i];
		}

		return rowProvided;
	}

	// Provide a column of Strings

	private String[] provideStringCol(int colNum)
	{
		String[] colProvided = new String[9];

		for(int i=0;i<9;i++)
		{
			colProvided[i] = stringArray[i][colNum];
		}

		return colProvided;
	}

	// Provide a box of Strings

	private String[] provideStringBox(int boxNum)
	{
		String[] boxProvided = new String[9];

		int goodRow;
		int goodCol;

		for(int i=0;i<9;i++)
		{
			goodRow = BoxTranslator.rowOfBoxSquare(boxNum,i);
			goodCol = BoxTranslator.colOfBoxSquare(boxNum,i);

			boxProvided[i] = stringArray[goodRow][goodCol];
		}

		return boxProvided;
	}

} // CheckStartingNums class