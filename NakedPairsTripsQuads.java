public class NakedPairsTripsQuads
{
/*
	private FullSudoku mySudoku;
	private MethodExplanations myMethods;

	NakedPairsTripsQuads(FullSudoku theSudoku, MethodExplanations theMethods)
	{
		mySudoku = theSudoku;
		myMethods = theMethods;
	}

	// Handy boolean

	private boolean canWePass;

	// Array of 9 Squares which make up the row/column/box being looked at

	private Square[] singleRCB;

	// Type of set currently being looked at: Row, Column, or Box

	private HouseType fullHouseType;

	// Which set is being looked at: Integer in range 0-8

	private int whichHouse;

	// Type of Naked Subset being sought: Pair, Trip, or Quad

	private PTQ fullNakedType;

	// int for the type of Naked Subset being sought. 2 for Pairs, 3 for Triples, 4 for Quads

	private int nakedType;

	// int for Squares being examined

	private int square1;
	private int square2;
	private int square3;
	private int square4;

	// Whether a Naked Pair/Trip/Quad has been successfully utilized

	private boolean didWeGetOne;


	// This function is used for Naked Pairs, Naked Triples, and Naked Quads.
	// (Functions which call this one are at the bottom of this document)

	private void oneSubsetTypeOneRCB(Square[] inputRCB, HouseType woo, int whichSet, PTQ hoo)
	{
		singleRCB = inputRCB;
		fullHouseType = woo;
		whichHouse = whichSet;
		fullNakedType = hoo;

		switch(fullNakedType)
		{
			case Pair: nakedType = 2; break;
			case Trip: nakedType = 3; break;
			case Quad: nakedType = 4; break;
		}

		// For a Naked Pair, all combinations of two different squares from 0 through 8 must be checked
		// For a Naked Triple, all combinations of three different squares from 0 through 8 must be checked
		// For a Naked Quad, all combinations of four different squares from 0 through 8 must be checked

		// This is put into motion with four loops, with a,b,c,d becoming square1,square2,square3,square4, the examined squares

		// For a Naked Pair, duplicate combinations are avoided by making sure a<b,c=0,d=0 (c & d aren't used past this point)
		// For a Naked Triple, duplicate combinations are avoided by making sure a<b,b<c,d=0 (d isn't used past this point)
		// for a Naked Quad, duplicate combinations are avoided by making sure a<b,b<c,c<d

		for(int a=0;a<9;a++)
		{
			square1 = a;

			for(int b=0;b<9;b++)
			{
				square2 = b;

				for(int c=0;c<9;c++)
				{
					square3 = c;

					for(int d=0;d<9;d++)
					{
						square4 = d;

						// Here is where the ensure the unique combination
						// Success means we continue to the next function

						canWePass = false;

						switch(nakedType)
						{
							case 2: if(square1 < square2 && square3 == 0 && square4 == 0) canWePass = true; break;
							case 3: if(square1 < square2 && square2 < square3 && square4 == 0) canWePass = true; break;
							case 4: if(square1 < square2 && square2 < square3 && square3 < square4) canWePass = true; break;
						}

						if(canWePass)
						{
							checkNumPossLeft();
						}

					}
				}
			}
		}

	} // oneSubsetTypeOneRCB()


	private void checkNumPossLeft()
	{
		// Continue if we haven't already found a Naked Pair/Trip/Quad

		if(!(didWeGetOne))
		{
			// For a Naked Pair, all squares must have a numPossLeft of 2
			// For a Naked Triple, all squares must have a numPossLeft of 2-3
			// For a Naked Quad, all squares must have a numPossLeft of 2-4

			int w = singleRCB[square1].numPossLeft;
			int x = singleRCB[square2].numPossLeft;
			int y = singleRCB[square3].numPossLeft;
			int z = singleRCB[square4].numPossLeft;

			canWePass = false;

			switch(nakedType)
			{
				case 2: if(inRange(w,2,2) && inRange(x,2,2)) canWePass = true; break;
				case 3: if(inRange(w,2,3) && inRange(x,2,3) && inRange(y,2,3)) canWePass = true; break;
				case 4: if(inRange(w,2,4) && inRange(x,2,4) && inRange(y,2,4) && inRange(z,2,4)) canWePass = true; break;
			}

			if(canWePass)
			{
				checkForNakedSubset();
			}
		}

	} // checkNumPossLeft()


	private void checkForNakedSubset()
	{
		// Time to see whether we actually have a Naked Subset

		// Each square will be sent to checkingPossArray so that its
		// possArray can be examined. If the square can contain a number,
		// that number will be marked.

		// For a Naked Pair, the total amount of marked numbers must be 2
		// For a Naked Triple, the total amount of marked numbers must be 3
		// For a Naked Quad, the total amount of marked numbers must be 4
		// If the amount marked is greater, then there is no Naked Subset


		// Start by resetting leftInPossArray and totalLeft

		for(int s=0;s<9;s++)
			leftInPossArray[s] = false;

		totalLeft = 0;


		// Send each square to checkingPossArray

		checkingPossArray(singleRCB[square1].possArray);
		checkingPossArray(singleRCB[square2].possArray);
		if(nakedType >= 3) checkingPossArray(singleRCB[square3].possArray);
		if(nakedType >= 4) checkingPossArray(singleRCB[square4].possArray);


		// Do you have it?

		canWePass = false;

		switch(nakedType)
		{
			case 2: if(totalLeft <= 2) canWePass = true; break;
			case 3: if(totalLeft <= 3) canWePass = true; break;
			case 4: if(totalLeft <= 4) canWePass = true; break;
		}

		if(canWePass)
		{
			elimTime();
		}

	} // checkForNakedSubset()


	private void elimTime()
	{
		// Record of the Integers which are part of the Naked Subset

		Integer[] numArray = new Integer[]{null,null,null,null};


		boolean temp;

		int numTrav = 0;
		int squareTrav = 0;

		// Elimination time. The two, three, or four numbers forming the Naked Subset are
		// booted from every square in the row/column/box except for the ones we tested.

		// Go through every number from 1 through 9

		for(int f=1;f<10;f++)
		{
			// Proceed only if that number is part of the Naked Subset

			if(leftInPossArray[f-1] == true)
			{
				// Go through every square in the row/column/box

				for(int g=0;g<9;g++)
				{
					// If the square is not one of the tested squares,
					// the square loses the number from its possArray

					canWePass = false;

					switch(nakedType)
					{
						case 2: if(g != square1 && g != square2) canWePass = true; break;
						case 3: if(g != square1 && g != square2 && g != square3) canWePass = true; break;
						case 4: if(g != square1 && g != square2 && g != square3 && g != square4) canWePass = true; break;
					}

					if(canWePass)
					{
						temp = mySudoku.elimFromPossArray(singleRCB[g],f);

						if(temp)
						{
							// For stuff which needs to happen only once when a Naked Subset is found

							if(!(didWeGetOne))
							{
								// Clear enough in myMethods to explain elimination of the 2, 3, or 4 poss

								switch(fullNakedType)
								{
									case Pair: myMethods.clearEnoughForNew(SolveMethod.NakedPair); break;
									case Trip: myMethods.clearEnoughForNew(SolveMethod.NakedTriple); break;
									case Quad: myMethods.clearEnoughForNew(SolveMethod.NakedQuad); break;
								}

								// Put the other constants in place

								myMethods.houseTargetType = fullHouseType;
								myMethods.intTargetSet = whichHouse;
								myMethods.subsetType = fullNakedType;

								myMethods.miscNumsA[0] = square1;
								myMethods.miscNumsA[1] = square2;
								if(nakedType >= 3) myMethods.miscNumsA[2] = square3;
								if(nakedType >= 4) myMethods.miscNumsA[3] = square4;

								didWeGetOne = true;
							}

							// Record square in squaresForKilledPoss, increment counter

							myMethods.squaresForKilledPoss[numTrav][squareTrav] = g;
							squareTrav++;
						}
					}
				}

				// Record the number in numArray, increment numTrav, reset squareTrav

				// This happens outside of the segment which can be triggered only if
				// this Naked Subset actually leads to elimination, so the last portion
				// of this function will check whether it occurred and will transfer
				// these numbers to arrayOfKilledPoss if needed.

				numArray[numTrav] = f;
				numTrav++;
				squareTrav = 0;
			}
		}

		// If at least one possibility was eliminated from at least one square,
		// insert numbers used for the Naked Subset into MethodExplanations

		if(didWeGetOne)
		{
			myMethods.arrayOfKilledPoss[0] = numArray[0];
			myMethods.arrayOfKilledPoss[1] = numArray[1];
			if(nakedType >= 3) myMethods.arrayOfKilledPoss[2] = numArray[2];
			if(nakedType >= 4) myMethods.arrayOfKilledPoss[3] = numArray[3];
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


	// Handy for checking whether the squares are a Naked Subset.
	// Uses leftInPossArray to record the numbers left in a single possArray,
	// and increments totalLeft for each number not already in leftInPossArray.

	// Like with the actual possArray, leftInPossArray[0] represents 1, etc.

	private boolean[] leftInPossArray = new boolean[9];
	private int totalLeft;

	private void checkingPossArray(Integer[] theArray)
	{
		for(int x=0;x<9;x++)
		{
			if(theArray[x] != null)
			{
				if(leftInPossArray[x] == false)
				{
					leftInPossArray[x] = true;
					totalLeft++;
				}
			}
		}

	} // checkingPossArray()


	// ~~~ Everything else deals with what is inserted into all of this ~~~


	// Provides appropriate parameters to oneSubsetTypeOneRCB()

	private void NakedSubsetsRCB(PTQ x)
	{
		// Every row/column/box is inserted along with an int
		// noting the type of Naked Subset to be looked at.

		for(int i=0;i<9;i++)
		{
			oneSubsetTypeOneRCB(mySudoku.provideRow(i),HouseType.Row,i,x);
		}

		for(int j=0;j<9;j++)
		{
			oneSubsetTypeOneRCB(mySudoku.provideCol(j),HouseType.Col,j,x);
		}

		for(int k=0;k<9;k++)
		{
			oneSubsetTypeOneRCB(mySudoku.provideBox(k),HouseType.Box,k,x);
		}

	} // NakedSubsetsRCB


	// Public functions putting everything to use. Calls NakedSubsetsRCB()
	// with a 2 for Pairs, 3 for Triples, 4 for Quads

	boolean NakedPairs()
	{
		didWeGetOne = false;

		NakedSubsetsRCB(PTQ.Pair);

		return didWeGetOne;

	} // NakedPairs()

	boolean NakedTriples()
	{
		didWeGetOne = false;

		NakedSubsetsRCB(PTQ.Trip);

		return didWeGetOne;

	} // NakedTriples()

	boolean NakedQuads()
	{
		didWeGetOne = false;

		NakedSubsetsRCB(PTQ.Quad);

		return didWeGetOne;

	} // NakedQuads()
*/
} // NakedPairsTripsQuads