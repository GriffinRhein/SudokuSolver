public class FishTwoThreeFour
{
/*
	private FullSudoku mySudoku;
	private MethodExplanations myMethods;

	FishTwoThreeFour(FullSudoku theSudoku, MethodExplanations theMethods)
	{
		mySudoku = theSudoku;
		myMethods = theMethods;
	}

	// int from 0 to 8 for Rows/Cols being examined

	private int RC1;
	private int RC2;
	private int RC3;
	private int RC4;

	// Specific base sets we work with

	private Square[] firstBaseSet;
	private Square[] secondBaseSet;
	private Square[] thirdBaseSet;
	private Square[] fourthBaseSet;

	// Type of BASE SET being looked at: Row or Column

	private HouseType baseSetType;

	// Number for which a fish is being attempted

	private int numToCheck;

	// Type of Fish being sought: X-Wing, Swordfish, or Jellyfish

	private enum FishType
	{
		X_Wing,
		Swordfish,
		Jellyfish;
	}

	private FishType fullFishType;

	// int for the type of Fish being sought. 2 for X-Wing, 3 for Swordfish, 4 for Jellyfish

	private int fishInt;

	// Relevant possPrevalence array

	private int[][] ourPrev;

	// Handy boolean

	private boolean canWePass;

	// Whether a Fish has made progress in solving

	private boolean didWeGetOne;


	// Worth nothing that for X-Wing, RC3 and RC4 are never looked at, and for Swordfish, RC4 is never looked at.

	private void singleSimpleFish(Integer b1, Integer b2, Integer b3, Integer b4, FishType inputFish, HouseType inputSetType, int inputNum)
	{
		if(!(didWeGetOne))
		{
			fullFishType = inputFish;
			numToCheck = inputNum;


			// Set int based on fish type

			switch(fullFishType)
			{
				case X_Wing:    fishInt = 2; break;
				case Swordfish: fishInt = 3; break;
				case Jellyfish: fishInt = 4; break;
			}


			// Set base sets

			baseSetType = inputSetType;

			RC1 = b1;
			RC2 = b2;
			if(fishInt >= 3) RC3 = b3;
			if(fishInt >= 4) RC4 = b4;

			switch(baseSetType)
			{
				case Row:

				firstBaseSet = mySudoku.provideRow(RC1);
				secondBaseSet = mySudoku.provideRow(RC2);
				if(fishInt >= 3) thirdBaseSet = mySudoku.provideRow(RC3);
				if(fishInt >= 4) fourthBaseSet = mySudoku.provideRow(RC4);

				break;


				case Col:

				firstBaseSet = mySudoku.provideCol(RC1);
				secondBaseSet = mySudoku.provideCol(RC2);
				if(fishInt >= 3) thirdBaseSet = mySudoku.provideCol(RC3);
				if(fishInt >= 4) fourthBaseSet = mySudoku.provideCol(RC4);

				break;
			}


			// Retrieve the appropriate PossPrevalence

			switch(baseSetType)
			{
				case Row: ourPrev = mySudoku.rowPossPrevalence; break;
				case Col: ourPrev = mySudoku.colPossPrevalence; break;
			}


			// Take the prevalence of the possibility in the input rows/columns.

			// X-Wing requires exactly 2 squares in each of the two base sets to
			// have the Poss available, Swordfish requires 2-3 squares in each of
			// the three base sets to have it, and Jellyfish requires 2-4 squares
			// in each of the four base sets.

			// Function called inRange() checks if a number is in a range

			int prevRC1 = -33; int prevRC2 = -33; int prevRC3 = -33; int prevRC4 = -33;

			prevRC1 = ourPrev[RC1][numToCheck-1];
			prevRC2 = ourPrev[RC2][numToCheck-1];
			if(fishInt >= 3) prevRC3 = ourPrev[RC3][numToCheck-1];
			if(fishInt >= 4) prevRC4 = ourPrev[RC4][numToCheck-1];

			canWePass = false;

			switch(fishInt)
			{
				case 2: if(inRange(prevRC1,2,2) && inRange(prevRC2,2,2)) canWePass = true; break;
				case 3: if(inRange(prevRC1,2,3) && inRange(prevRC2,2,3) && inRange(prevRC3,2,3)) canWePass = true; break;
				case 4: if(inRange(prevRC1,2,4) && inRange(prevRC2,2,4) && inRange(prevRC3,2,4) && inRange(prevRC4,2,4)) canWePass = true; break;
			}


			// ~~~ If no square possesses a possPrevalence which makes the sought-after fish impossible, then on to the next step ~~~


			if(canWePass)
			{
				// Function goodCoverSets(), further down, looks through the base
				// sets and determines whether the sought-after fish exists

				goodCoverSets();

				canWePass = false;

				switch(fishInt)
				{
					case 2: if(fullCounter <= 2) canWePass = true; break;
					case 3: if(fullCounter <= 3) canWePass = true; break;
					case 4: if(fullCounter <= 4) canWePass = true; break;
				}


				// ~~~ If the fish exists, then on to the elimination step ~~~


				if(canWePass)
				{
					elimTime();
				}
			}
		}

	} // singleSimpleFish()


	private void elimTime()
	{
		// To erase the number from all squares in cover sets
		// but not base sets, we will need the cover sets

		Square[][] theCoverSets = new Square[fishInt][9];
		int[] intsOfSets = new int[fishInt];

		int newCounter = 0;

		for(int z=0;z<9;z++)
		{
			// Thanks to goodCoverSets(), the cover sets are those
			// whose associated spot in fullArray is marked true.  

			if(fullArray[z] == true)
			{
				switch(baseSetType)
				{
					case Row:

					theCoverSets[newCounter] = mySudoku.provideCol(z);
					intsOfSets[newCounter] = z;
					newCounter++;

					break;


					case Col:

					theCoverSets[newCounter] = mySudoku.provideRow(z);
					intsOfSets[newCounter] = z;
					newCounter++;

					break;
				}
			}
		}

		// Elimination time. Go through each square of the cover sets, and
		// knock off the possibility from all squares not in the base sets

		for(int r=0;r<fishInt;r++)
		{
			newCounter = 0;

			for(int s=0;s<9;s++)
			{
				canWePass = false;

				switch(fishInt)
				{
					case 2: if(s != RC1 && s != RC2) canWePass = true; break;
					case 3: if(s != RC1 && s != RC2 && s != RC3) canWePass = true; break;
					case 4: if(s != RC1 && s != RC2 && s != RC3 && s != RC4) canWePass = true; break;
				}

				if(canWePass)
				{
					boolean temp;

					temp = mySudoku.elimFromPossArray(theCoverSets[r][s],numToCheck);

					if(temp)
					{
						if(!(didWeGetOne))
						{
							// Clear enough in myMethods to explain elimination
							// of the number in 2, 3, or 4 rows/columns

							switch(fullFishType)
							{
								case X_Wing: myMethods.clearEnoughForNew(SolveMethod.X_Wing); break;
								case Swordfish: myMethods.clearEnoughForNew(SolveMethod.Swordfish); break;
								case Jellyfish: myMethods.clearEnoughForNew(SolveMethod.Jellyfish); break;
							}


							// Put the other constants in place

							myMethods.arrayOfKilledPoss[0] = numToCheck;

							if(baseSetType == HouseType.Row)
							{
								myMethods.houseTargetType = HouseType.Col;
								myMethods.houseContextType = HouseType.Row;
							}
							else
							{
								myMethods.houseTargetType = HouseType.Row;
								myMethods.houseContextType = HouseType.Col;
							}

							// Use miscNumsA to hold the ints of our base sets

							myMethods.miscNumsA[0] = RC1;
							myMethods.miscNumsA[1] = RC2;
							if(fishInt >= 3) myMethods.miscNumsA[2] = RC3;
							if(fishInt >= 4) myMethods.miscNumsA[3] = RC4;

							// Use miscNumsB to hold the ints of our cover sets

							for(int i=0;i<fishInt;i++)
							{
								myMethods.miscNumsB[i] = intsOfSets[i];
							}

							didWeGetOne = true;
						}

						myMethods.squaresForKilledPoss[r][newCounter] = s;
						newCounter++;
					}
				}
			}
		}

	} // elimTime()


	// Quick function just to see whether a number falls within
	// a range given by two others. Allows for shorthand.

	private Boolean inRange(int testing,int a,int b)
	{
		if(testing >= a && testing <= b)
			return true;

		return false;

	} // inRange()


	// goodCoverSets() is used to record the specific squares
	// across all base sets which can still contain the number.

	// We start with fullArray representing nine squares, and
	// knock off squares as we see that the number is still in
	// the possArray for that square in ANY of the 2-4 base sets,
	// incrementing fullCounter each time a square is axed.

	// When we're done checking all base sets, fullCounter will
	// be the total amount of cover sets needed to cover every
	// square in base sets which has the number in a possArray.

	private boolean[] fullArray = new boolean[9];
	private int fullCounter = 0;

	private void goodCoverSets()
	{
		// Reset fullArray

		for(int i=0;i<9;i++)
			fullArray[i] = false;

		fullCounter = 0;


		// Handy array to hold base sets

		Square[][] theBaseSets = new Square[][]{firstBaseSet,secondBaseSet,thirdBaseSet,fourthBaseSet};

		// Don't look at too many base sets

		boolean isThisRelevant;


		// Get to work. Every square from 0-8 which can hold the number within any of
		// the base sets will be marked on fullArray, with fullCounter incremented

		// Nine squares within a base set

		for(int a=0;a<9;a++)
		{
			// Maximum of four base sets

			for(int b=0;b<4;b++)
			{
				isThisRelevant = false;

				switch(fishInt)
				{
					case 2: if(b < 2) isThisRelevant = true; break;
					case 3: if(b < 3) isThisRelevant = true; break;
					case 4: if(b < 4) isThisRelevant = true; break;
				}

				if(isThisRelevant)
				{
					// Make sure the square is not solved

					if(theBaseSets[b][a].result == null)
					{
						// Make sure the square can still contain the number

						if(theBaseSets[b][a].possArray[numToCheck-1] != null)
						{
							// Make sure that spot hasn't already been marked off here

							if(fullArray[a] == false)
							{
								fullArray[a] = true;
								fullCounter++;
							}
						}
					}
				}
			}
		}

	} // goodCoverSets()


	// ~~~ Public functions putting the ones above to use. ~~~


	boolean X_Wing()
	{
		didWeGetOne = false;

		// Operation occurs for every possible
		// combination of 2 rows or 2 columns

		for(int a=0;a<9;a++)
		{
			for(int b=0;b<9;b++)
			{
				// Ensures that the operation occurs only
				// once per combination of rows or columns

				if(a < b)
				{
					// Every number is tested

					for(Integer e=1;e<10;e++)
					{
						singleSimpleFish(a,b,null,null,FishType.X_Wing,HouseType.Row,e);
						singleSimpleFish(a,b,null,null,FishType.X_Wing,HouseType.Col,e);
					}
				}
			}
		}

		return didWeGetOne;
	}

	boolean Swordfish()
	{
		didWeGetOne = false;

		// Operation occurs for every possible
		// combination of 3 rows or 3 columns

		for(int a=0;a<9;a++)
		{
			for(int b=0;b<9;b++)
			{
				for(int c=0;c<9;c++)
				{
					// Ensures that the operation occurs only
					// once per combination of rows or columns

					if(a < b && b < c)
					{
						// Every number is tested

						for(Integer e=1;e<10;e++)
						{
							singleSimpleFish(a,b,c,null,FishType.Swordfish,HouseType.Row,e);
							singleSimpleFish(a,b,c,null,FishType.Swordfish,HouseType.Col,e);
						}
					}
				}
			}
		}

		return didWeGetOne;
	}

	boolean Jellyfish()
	{
		didWeGetOne = false;

		// Operation occurs for every possible
		// combination of 4 rows or 4 columns

		for(int a=0;a<9;a++)
		{
			for(int b=0;b<9;b++)
			{
				for(int c=0;c<9;c++)
				{
					for(int d=0;d<9;d++)
					{
						// Ensures that the operation occurs only
						// once per combination of rows or columns

						if(a < b && b < c && c < d)
						{
							// Every number is tested

							for(Integer e=1;e<10;e++)
							{
								singleSimpleFish(a,b,c,d,FishType.Jellyfish,HouseType.Row,e);
								singleSimpleFish(a,b,c,d,FishType.Jellyfish,HouseType.Col,e);
							}
						}
					}
				}
			}
		}

		return didWeGetOne;
	}
*/
} // FishTwoThreeFour