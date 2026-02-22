public class HiddenPairsTripsQuads
{
/*
	private FullSudoku mySudoku;
	private MethodExplanations myMethods;

	HiddenPairsTripsQuads(FullSudoku theSudoku, MethodExplanations theMethods)
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

	// int from 0 to 8 indicating the row/column/box being looked at

	private int whichHouse;

	// Type of Hidden Subset being sought: Pair, Trip, or Quad

	private PTQ fullHiddenType;

	// int for the type of Hidden Subset being sought. 2 for Pairs, 3 for Triples, 4 for Quads

	private int hiddenType;

	// Numbers being examined

	private int num1;
	private int num2;
	private int num3;
	private int num4;

	// Relevant possPrevalence array.

	private int[][] thisPossPrevalence;

	// Whether a Hidden Pair/Trip/Quad has been successfully utilized

	private boolean didWeGetOne;


	// This function is used for Hidden Pairs, Hidden Triples, and Hidden Quads.
	// (Functions which call this one are at the bottom of this document)

	private void oneSubsetTypeOneRCB(Square[] examinedRCB, HouseType woo, int whichSet, PTQ hoo)
	{
		singleRCB = examinedRCB;
		fullHouseType = woo;
		whichHouse = whichSet;
		fullHiddenType = hoo;

		switch(fullHiddenType)
		{
			case Pair: hiddenType = 2; break;
			case Trip: hiddenType = 3; break;
			case Quad: hiddenType = 4; break;
		}
		
		// For a Hidden Pair, all combinations of two different numbers from 1 through 9 must be checked
		// For a Hidden Triple, all combinations of three different numbers from 1 through 9 must be checked
		// For a Hidden Quad, all combinations of four different numbers from 1 through 9 must be checked

		// This is put into motion with four loops, with a,b,c,d becoming num1,num2,num3,num4, the examined numbers

		// For a Hidden Pair, duplicate combinations are avoided by making sure a<b,c=1,d=1 (c & d aren't used past this point)
		// For a Hidden Triple, duplicate combinations are avoided by making sure a<b,b<c,d=1 (d isn't used past this point)
		// for a Hidden Quad, duplicate combinations are avoided by making sure a<b,b<c,c<d

		for(int a=1;a<10;a++)
		{
			num1 = a;

			for(int b=1;b<10;b++)
			{
				num2 = b;

				for(int c=1;c<10;c++)
				{
					num3 = c;

					for(int d=1;d<10;d++)
					{
						num4 = d;

						// Here is where the ensure the unique combination
						// Success means we continue to the next function

						canWePass = false;

						switch(hiddenType)
						{
							case 2: if(num1 < num2 && num3 == 1 && num4 == 1) canWePass = true; break;
							case 3: if(num1 < num2 && num2 < num3 && num4 == 1) canWePass = true; break;
							case 4: if(num1 < num2 && num2 < num3 && num3 < num4) canWePass = true; break;
						}

						if(canWePass)
						{
							checkPossPrevalence();
						}
					}
				}
			}
		}

	} // oneSubsetTypeOneRCB()


	private void checkPossPrevalence()
	{
		// Continue if we haven't already found a Hidden Pair/Trip/Quad

		if(!(didWeGetOne))
		{
			// Recall that possPrevalence of a number refers to the amount of squares
			// in a row/column/box which still contains the number in its possArray

			// For a Hidden Pair, all numbers must have a possPrevalence of 2
			// For a Hidden Triple, all numbers must have a possPrevalence of 2-3
			// For a Hidden Quad, all numbers must have a possPrevalence of 2-4

			// Set thisPossPrevalence to the appropriate possPrevalence array,
			// and for the current row/column/box retrieve the prevalance of
			// each of the numbers. Continue if the relevant ones are acceptable.
				
			switch(fullHouseType)
			{
				case Row: thisPossPrevalence = mySudoku.rowPossPrevalence; break;
				case Col: thisPossPrevalence = mySudoku.colPossPrevalence; break;
				case Box: thisPossPrevalence = mySudoku.boxPossPrevalence; break;
			}

			int w = thisPossPrevalence[whichHouse][num1 - 1];
			int x = thisPossPrevalence[whichHouse][num2 - 1];
			int y = thisPossPrevalence[whichHouse][num3 - 1];
			int z = thisPossPrevalence[whichHouse][num4 - 1];

			canWePass = false;

			switch(hiddenType)
			{
				case 2: if(inRange(w,2,2) && inRange(x,2,2)) canWePass = true; break;
				case 3: if(inRange(w,2,3) && inRange(x,2,3) && inRange(y,2,3)) canWePass = true; break;
				case 4: if(inRange(w,2,4) && inRange(x,2,4) && inRange(y,2,4) && inRange(z,2,4)) canWePass = true; break;
			}

			if(canWePass)
			{
				checkForHiddenSubset();
			}
		}

	} // checkPossPrevalence()


	private void checkForHiddenSubset()
	{
		// Time to see whether we actually have a Hidden Subset

		// Each number will be sent to totalChecker with each square
		// in the row/column/box, and if a square can contain one of
		// the numbers, it will be marked.

		// For a Hidden Pair, the total amount of marked squares must be 2
		// For a Hidden Triple, the total amount of marked squares must be 3
		// For a Hidden Quad, the total amount of marked squares must be 4
		// If the amount marked is greater, then there is no Hidden Subset


		// Start by resetting totalSquaresIn & totalCounter

		for(int f=0;f<9;f++)
			totalSquaresIn[f] = false;

		totalCounter = 0;


		// Send each number to totalChecker with each square in the set. f represents each square.
		// For f=0, totalChecker checks singleRCB[0], et cetera.

		for(int f=0;f<9;f++)
		{
			totalChecker(num1,f);
			totalChecker(num2,f);
			if(hiddenType >= 3) totalChecker(num3,f);
			if(hiddenType >= 4) totalChecker(num4,f);
		}


		// Do you have it?

		canWePass = false;

		switch(hiddenType)
		{
			case 2: if(totalCounter <= 2) canWePass = true; break;
			case 3: if(totalCounter <= 3) canWePass = true; break;
			case 4: if(totalCounter <= 4) canWePass = true; break;
		}

		if(canWePass)
		{
			itIsElimination();
		}

	} // checkForHiddenSubset()


	private void itIsElimination()
	{
		// Record of the Integers which are not part of the Hidden Subset

		Integer[] numArray = new Integer[]{null,null,null,null,null,null,null};


		boolean temp;

		int numTrav = 0;
		int squareTrav = 0;

		// Elimination time. The two, three, or four squares we tested in the
		// row/column/box lose every number except for the ones we tested.

		for(int h=1;h<10;h++)
		{
			canWePass = false;

			switch(hiddenType)
			{
				case 2: if(h != num1 && h != num2) canWePass = true; break;
				case 3: if(h != num1 && h != num2 && h != num3) canWePass = true; break;
				case 4: if(h != num1 && h != num2 && h != num3 && h != num4) canWePass = true; break;
			}

			if(canWePass)
			{
				// Go through each square in the row/column/box

				for(int g=0;g<9;g++)
				{
					// Act only if this square was found to be in the Hidden Subset

					if(totalSquaresIn[g] == true)
					{
						temp = mySudoku.elimFromPossArray(singleRCB[g],h);

						if(temp)
						{
							if(!(didWeGetOne))
							{
								// Clear enough in myMethods to explain elimination of all but the 2, 3, or 4 poss

								switch(fullHiddenType)
								{
									case Pair: myMethods.clearEnoughForNew(SolveMethod.HiddenPair); break;
									case Trip: myMethods.clearEnoughForNew(SolveMethod.HiddenTriple); break;
									case Quad: myMethods.clearEnoughForNew(SolveMethod.HiddenQuad); break;
								}


								// Put the other constants in place

								myMethods.houseTargetType = fullHouseType;
								myMethods.intTargetSet = whichHouse;
								myMethods.subsetType = fullHiddenType;

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

				numArray[numTrav] = h;
				numTrav++;
				squareTrav = 0;
			}
		}

		// If at least one possibility was eliminated from at least one square,
		// insert squares used and numbers not used into MethodExplanations

		if(didWeGetOne)
		{
			// Send numbers making up the Hidden Subset

			myMethods.miscNumsA[0] = num1;
			myMethods.miscNumsA[1] = num2;
			if(hiddenType >= 3) myMethods.miscNumsA[2] = num3;
			if(hiddenType >= 4) myMethods.miscNumsA[3] = num4;


			// Send squares used in the Hidden Subset

			int findThem = 0;

			for(int i=0;i<totalSquaresIn.length;i++)
			{
				if(totalSquaresIn[i])
				{
					myMethods.miscNumsB[findThem] = i;
					findThem++;
				}
			}


			// Send numbers not used, a.k.a. the possibilites which were nixed

			myMethods.arrayOfKilledPoss[0] = numArray[0];
			myMethods.arrayOfKilledPoss[1] = numArray[1];
			myMethods.arrayOfKilledPoss[2] = numArray[2];
			myMethods.arrayOfKilledPoss[3] = numArray[3];
			myMethods.arrayOfKilledPoss[4] = numArray[4];
			if(hiddenType <= 3) myMethods.arrayOfKilledPoss[5] = numArray[5];
			if(hiddenType <= 2) myMethods.arrayOfKilledPoss[6] = numArray[6];
		}

	} // itIsElimination()


	// Quick function just to see whether a number falls within
	// a range given by two others. Allows for shorthand.

	private Boolean inRange(int testing,int a,int b)
	{
		if(testing >= a && testing <= b)
			return true;

		return false;

	} // inRange()


	// Used for checking whether the numbers form a Hidden Subset.
	// totalSquaresIn records squares where a number is in a possArray,
	// and increments totalCounter for each new square recorded.

	// No loops here; the loop is in the big function this time.

	private boolean[] totalSquaresIn = new boolean[9];
	private int totalCounter = 0;

	private void totalChecker(int num, int where)
	{
		// Make sure the square is not solved

		if(singleRCB[where].result == null)
		{
			// Make sure the square can still contain the number

			if(singleRCB[where].possArray[num - 1] != null)
			{
				// Make sure that spot hasn't already been marked off here

				if(totalSquaresIn[where] == false)
				{
					totalSquaresIn[where] = true;
					totalCounter++;
				}
			}
		}

	} // totalChecker()


	// ~~~ Everything else deals with what is inserted into all of this ~~~


	// Provides appropriate parameters to oneSubsetTypeOneRCB()

	private void HiddenSubsetsRCB(PTQ x)
	{
		// Every row/column/box is inserted along with an int noting which
		// row/column/box it is, an int noting whether it is a row or column
		// or box, and an int noting the type of Hidden Subset to be looked at.

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

	} // HiddenSubsetsRCB


	// Public functions putting everything to use. Calls HiddenSubsetsRCB()
	// with a 2 for Pairs, 3 for Triples, 4 for Quads

	boolean HiddenPairs()
	{
		didWeGetOne = false;

		HiddenSubsetsRCB(PTQ.Pair);

		return didWeGetOne;

	} // HiddenPairs()

	boolean HiddenTriples()
	{
		didWeGetOne = false;

		HiddenSubsetsRCB(PTQ.Trip);

		return didWeGetOne;

	} // HiddenTriples()

	boolean HiddenQuads()
	{
		didWeGetOne = false;

		HiddenSubsetsRCB(PTQ.Quad);

		return didWeGetOne;

	} // HiddenQuads()
*/
} // HiddenPairsTripsQuads