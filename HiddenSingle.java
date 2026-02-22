public class HiddenSingle
{
	private HiddenSingle() {}

	// Check each row, column, and box to see whether
	// any number which does not yet exist as a result
	// in that house has been killed as a possibility
	// for all but one square.

	// Should one such number be found, the square where
	// it remains in the possArray will lose everything
	// in that possArray except for our number.

	// Then, since elimFromPossArray() is guaranteed to
	// put that square in the Linked List, and there is
	// not much point in making a separate step out of
	// setting the result, call NakedSingle()


	// Hidden Single Implementation

	static StepInfo HiddenSingle(FullSudoku mySudoku)
	{
		StepInfo stepInfo = null;

		Square[] squaresOfRCB = null;
		int[] possPrevalenceInRCB = null;

		for(HouseType typeOfRCB : HouseType.values())
		{
			for(int intOfRCB=0;intOfRCB<9;intOfRCB++)
			{
				if(typeOfRCB == HouseType.Row)
				{
					squaresOfRCB = mySudoku.provideRow(intOfRCB);
					possPrevalenceInRCB = mySudoku.rowPossPrevalence[intOfRCB];
				}
				else if(typeOfRCB == HouseType.Col)
				{
					squaresOfRCB = mySudoku.provideCol(intOfRCB);
					possPrevalenceInRCB = mySudoku.colPossPrevalence[intOfRCB];
				}
				else if(typeOfRCB == HouseType.Box)
				{
					squaresOfRCB = mySudoku.provideBox(intOfRCB);
					possPrevalenceInRCB = mySudoku.boxPossPrevalence[intOfRCB];
				}


				// Each number from 1 through 9 gets a turn at bat

				for(int testedPoss=1;testedPoss<=9;testedPoss++)
				{
					// Check whether the possPrevalence array indicates that there is
					// only one square within the RCB capable of containing the number.

					if(possPrevalenceInRCB[testedPoss-1] < 2)
					{
						stepInfo = examineRCB(mySudoku,typeOfRCB,intOfRCB,squaresOfRCB,testedPoss);

						// Common understanding of Hidden Singles is that they immediately
						// solve the square in question. No need to bother with a separate
						// step to make the square's only remaining possibility its result

						if(stepInfo != null)
						{
							// Since HiddenSingle is called only when NakedSingle has failed on
							// the run through the loop, the linked list must have been empty
							// before HiddenSingle was called, so any Hidden Single found
							// and inserted into the list will be at the front.

							mySudoku.NakedSingle();
							return stepInfo;
						}
					}
				}

			} // Everything in this loop occurs once for each of the 27 sets
		}

		return stepInfo;

	} // HiddenSingle()


	static private StepInfo examineRCB(FullSudoku mySudoku, HouseType typeOfRCB, int intOfRCB, Square[] squaresOfRCB, int testedPoss)
	{
		// Go through the RCB and find the square where the number is still available.

		for(Square squareInRCB : squaresOfRCB)
		{
			// For when the square is solved
			if(squareInRCB.result != null)
			{
				// Check whether the testedPoss is the square's result

				if(squareInRCB.result != testedPoss)
				{
					// If not, on to the next square.
					continue;
				}
				else				
				{
					// If so, no need to look any further through the RCB for this testedPoss.
					return null;
				}
			}

			// For when the square is not solved
			else
			{
				// Check whether the testedPoss is in the square's possArray

				if(squareInRCB.possArray[testedPoss-1] == null)
				{
					// If not, on to the next square.
					continue;
				}
				else
				{
					// If so, it must be the only square in the RCB to do so. This is a Hidden Single.

					for(int i=1;i<=9;i++)
					{
						if(i != testedPoss)
						{
							mySudoku.elimFromPossArray(squareInRCB,i);
						}
					}

					return new Result_HiddenSingle(squareInRCB,testedPoss,typeOfRCB,intOfRCB);
				}
			}
		}

		return null;

	} // examineRCB()

} // HiddenSingle