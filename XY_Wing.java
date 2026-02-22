public class XY_Wing
{
/*
	// Note that in an XY Wing, all three cells involved must contain EXACTLY
	// two numbers in their possArray, or the logic does not work.

	private FullSudoku mySudoku;
	private MethodExplanations myMethods;

	XY_Wing(FullSudoku theSudoku, MethodExplanations theMethods)
	{
		mySudoku = theSudoku;
		myMethods = theMethods;
	}

	private Square thePivot;
	private Square theX;
	private Square theY;

	private Integer pivotPoss1;
	private Integer pivotPoss2;

	private Integer XPoss1;
	private Integer XPoss2;

	private Integer YPoss1;
	private Integer YPoss2;

	private boolean doWeHaveWing;
	private Integer wingDigitInCommon;

	private boolean doYouElim;
	private Square elimTemplate;

	private int coolMarker;

	private Square[] sharingRCB;

	private boolean didWeGetOne;


	// We start by going through every square in the grid. Any square which is
	// down to exactly two possibilities may serve as a pivot, so for each square
	// which fulfills that criteria, move on to the next step with it as the pivot

	boolean XY_Wing()
	{
		didWeGetOne = false;

		for(int a=0;a<9;a++)
		{
			for(int b=0;b<9;b++)
			{
				thePivot = mySudoku.SudokuMap[a][b];

				// First, make sure the pivot is not already solved

				if(thePivot.result == null)
				{
					// Then, make sure it has exactly 2 possibilities left

					if(thePivot.numPossLeft == 2)
					{
						// Move on

						get_pivot_poss_and_XY();
					}
				}
			}
		}

		return didWeGetOne;

	} // XY_Wing_Begin()


	// The pivot's 2 possibilities are retrieved, and an array of the
	// 20 squares sharing an RCB with it is created. Then, every possible
	// pairing of two squares from that array is sent to the next step

	private void get_pivot_poss_and_XY()
	{
		// Retrieve the 2 possibilities

		coolMarker = 0;

		for(int c=0;c<9;c++)
		{
			if(thePivot.possArray[c] != null)
			{
				if(coolMarker == 0)
					pivotPoss1 = thePivot.possArray[c];
				else
					pivotPoss2 = thePivot.possArray[c];

				coolMarker++;
			}
		}

		// Wrangle together all squares sharing an RCB with the pivot
		// There are exactly 20 such squares, every time

		sharingRCB = mySudoku.provideAllSharingRCB(thePivot);

		// Navigate sharingRCB with TWO variables. These variables
		// will indicate where in the array are our X and Y

		for(int intX=0;intX<sharingRCB.length;intX++)
		{
			theX = sharingRCB[intX];

			if(theX.result == null && theX.numPossLeft == 2)
			{
				for(int intY=intX+1;intY<sharingRCB.length;intY++)
				{
					theY = sharingRCB[intY];

					if(theY.result == null && theY.numPossLeft == 2)
					{
						// Move on

						get_X_and_Y_poss();
					}
				}
			}
		}

	} // get_pivot_poss_and_XY()


	// Check whether X and Y both have 2 possibilities, and
	// if so retrieve them before moving to the final function

	private void get_X_and_Y_poss()
	{
		if(!(didWeGetOne))
		{
			// Retrieve possibilities for X and Y

			coolMarker = 0;

			for(int c=0;c<9;c++)
			{
				if(theX.possArray[c] != null)
				{
					if(coolMarker == 0)
						XPoss1 = theX.possArray[c];
					else
						XPoss2 = theX.possArray[c];

					coolMarker++;
				}
			}

			coolMarker = 0;

			for(int c=0;c<9;c++)
			{
				if(theY.possArray[c] != null)
				{
					if(coolMarker == 0)
						YPoss1 = theY.possArray[c];
					else
						YPoss2 = theY.possArray[c];

					coolMarker++;
				}
			}

			// Move on

			checkAndElim();
		}

	} // get_X_and_Y_poss()


	// Check whether the Pivot & X & Y form an XY Wing, and if so go
	// through the sudoku grid eliminating the digit they share from
	// all squares sharing an RCB with X and sharing an RCB with Y.
	// Jsut make sure you don't knock it from X or Y themselves

	private void checkAndElim()
	{
		// Do the Pivot & X & Y form an XY Wing?
		// Final check before the elimination stage.
		// Personalized function, is down below

		doYouHaveIt(pivotPoss1,pivotPoss2,XPoss1,XPoss2,YPoss1,YPoss2);

		if(doWeHaveWing)
		{
			boolean temp;

			int newCounter = 0;

			// Go through each square in the sudoku, performing
			// eliminations on the appropriate ones

			for(int rowLook=0;rowLook<9;rowLook++)
			{
				for(int colLook=0;colLook<9;colLook++)
				{
					elimTemplate = mySudoku.SudokuMap[rowLook][colLook];

					// Check whether the elimination can occur on this square.
					// Personalized function, is down below

					doYouElim = doYouElimThisSquare(elimTemplate);

					if(doYouElim)
					{
						temp = mySudoku.elimFromPossArray(elimTemplate,wingDigitInCommon);

						if(temp)
						{
							if(!(didWeGetOne))
							{
								// Clear enough in myMethods to explain elimination.

								// Since we don't have a specific row/column/box to work with,
								// one integer is not sufficient to indicate a square.
								// Clear two lines of squaresForKilledPoss.

								myMethods.clearEnoughForNew(SolveMethod.XY_Wing);


								// Put the other constants in place

								myMethods.arrayOfKilledPoss[0] = wingDigitInCommon;

								myMethods.SquareA = thePivot;
								myMethods.SquareB = theX;
								myMethods.SquareC = theY;

								myMethods.miscNumsA[0] = pivotPoss1;
								myMethods.miscNumsA[1] = XPoss1;
								myMethods.miscNumsA[2] = YPoss1;

								myMethods.miscNumsB[0] = pivotPoss2;
								myMethods.miscNumsB[1] = XPoss2;
								myMethods.miscNumsB[2] = YPoss2;

								didWeGetOne = true;
							}

							// Absolute maximum number of squares which share a set with X
							// and share a set with Y is 13. Since the pivot is one of them
							// but cannot contain the eliminated digit, it is impossible for
							// more than 12 squares to be affected by the XY_Wing

							myMethods.squaresForKilledPoss[0][newCounter] = elimTemplate.ownRow;
							myMethods.squaresForKilledPoss[1][newCounter] = elimTemplate.ownCol;
							newCounter++;
						}
					}
				}
			}
		}

	} // checkAndElim()


	// ~~~~~~~~~~ End of the step-by-step procedures. ~~~~~~~~~~ 
	// ~~~~~~~~~~ Helper functions beyond this point. ~~~~~~~~~~


	// Checks whether the Pivot & X & Y, appropriately located relative to one another and
	// each down to 2 possibilities, have the necessary possibilities left to form an XY Wing.
	// There are 8 ways this could be the case, and this function checks for all of them.
	// Numbers are entered in the order pivotPoss1, pivotPoss2, XPoss1, XPoss2, YPoss1, YPoss2

	private void doYouHaveIt(Integer a, Integer b, Integer c, Integer d, Integer e, Integer f)
	{
		if(c.equals(a) && e.equals(b) && d.equals(f))
		{
			doWeHaveWing = true;
			wingDigitInCommon = d;
		}
		else if(c.equals(a) && f.equals(b) && d.equals(e))
		{
			doWeHaveWing = true;
			wingDigitInCommon = d;
		}
		else if(d.equals(a) && e.equals(b) && c.equals(f))
		{
			doWeHaveWing = true;
			wingDigitInCommon = c;
		}
		else if(d.equals(a) && f.equals(b) && c.equals(e))
		{
			doWeHaveWing = true;
			wingDigitInCommon = c;
		}
		else if(c.equals(b) && e.equals(a) && d.equals(f))
		{
			doWeHaveWing = true;
			wingDigitInCommon = d;
		}
		else if(c.equals(b) && f.equals(a) && d.equals(e))
		{
			doWeHaveWing = true;
			wingDigitInCommon = d;
		}
		else if(d.equals(b) && e.equals(a) && c.equals(f))
		{
			doWeHaveWing = true;
			wingDigitInCommon = c;
		}
		else if(d.equals(b) && f.equals(a) && c.equals(e))
		{
			doWeHaveWing = true;
			wingDigitInCommon = c;
		}
		else
		{
			doWeHaveWing = false;
		}

	} // doYouHaveIt()


	// Checks whether a square is neither X nor Y but shares an
	// RCB with X and shares an RCB with Y. Good for elimination.

	private boolean doYouElimThisSquare(Square g)
	{
		// Make sure the candidate is not X

		if(g.ownRow != theX.ownRow || g.ownCol != theX.ownCol)
		{
			// Make sure the candidate is not Y

			if(g.ownRow != theY.ownRow || g.ownCol != theY.ownCol)
			{
				// Make sure the candidate shares an RCB with X

				if(g.ownRow == theX.ownRow || g.ownCol == theX.ownCol || g.ownBox == theX.ownBox)
				{
					// Make sure the candidate shares an RCB with Y

					if(g.ownRow == theY.ownRow || g.ownCol == theY.ownCol || g.ownBox == theY.ownBox)
					{
						return true;
					}
				}
			}
		}

		return false;

	} // doYouElimThisSquare()
*/
} // XY_Wing