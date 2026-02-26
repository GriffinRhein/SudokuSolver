import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HiddenPairsTripsQuads
{
	private HiddenPairsTripsQuads() {}


	static StepInfo HiddenPairs(FullSudoku mySudoku)
	{
		return HiddenSubsets(mySudoku,PTQ.Pair);

	} // HiddenPairs()

	static StepInfo HiddenTrips(FullSudoku mySudoku)
	{
		return HiddenSubsets(mySudoku,PTQ.Trip);

	} // HiddenTrips()

	static StepInfo HiddenQuads(FullSudoku mySudoku)
	{
		return HiddenSubsets(mySudoku,PTQ.Quad);

	} // HiddenQuads()


	private static StepInfo HiddenSubsets(FullSudoku mySudoku, PTQ subsetType)
	{
		StepInfo stepInfo = null;

		for(HouseType typeOfRCB : HouseType.values())
		{
			for(int intOfRCB=0;intOfRCB<9;intOfRCB++)
			{
				Square[] currentRCB = null;

				if(typeOfRCB == HouseType.Row)
					currentRCB = mySudoku.provideRow(intOfRCB);
				else if(typeOfRCB == HouseType.Col)
					currentRCB = mySudoku.provideCol(intOfRCB);
				else if(typeOfRCB == HouseType.Box)
					currentRCB = mySudoku.provideBox(intOfRCB);

				stepInfo = canSubsetExistInRCB(mySudoku,subsetType,currentRCB,typeOfRCB,intOfRCB);

				if(stepInfo != null)
					return stepInfo;

			} // Everything in this loop occurs once for each Row, each Col, and each Box
		}

		return stepInfo;

	} // HiddenSubsets()


	// Quick function just to see whether a number falls within
	// a range given by two others. Allows for shorthand.

	private static boolean inRange(int testing,int a,int b)
	{
		if(testing >= a && testing <= b)
			return true;

		return false;

	} // inRange()


	private static StepInfo canSubsetExistInRCB(FullSudoku mySudoku, PTQ subsetType, Square[] currentRCB, HouseType typeOfRCB, int intOfRCB)
	{
		StepInfo stepInfo = null;

		// For a Hidden Pair, all combinations of two different numbers from 1 through 9 must be checked
		// For a Hidden Triple, all combinations of three different numbers from 1 through 9 must be checked
		// For a Hidden Quad, all combinations of four different numbers from 1 through 9 must be checked

		// This is put into motion with four loops

		for(int a=1;a<=9;a++)
		{
			for(int b=1;b<=9;b++)
			{
				for(int c=1;c<=9;c++)
				{
					for(int d=1;d<=9;d++)
					{
						// When looking for a Hidden Pair, duplicate combinations of numbers are avoided by making sure a<b,c=1,d=1 (c & d aren't used past this point)
						// When looking for a Hidden Triple, duplicate combinations of numbers are avoided by making sure a<b,b<c,d=1 (d isn't used past this point)
						// When looking for a Hidden Quad, duplicate combinations of numbers are avoided by making sure a<b,b<c,c<d

						switch(subsetType)
						{
							case Pair: if(!(a < b && c == 1 && d == 1)){continue;} break;
							case Trip: if(!(a < b && b < c && d == 1)){continue;} break;
							case Quad: if(!(a < b && b < c && c < d)){continue;} break;
						}


						// Recall that possPrevalence of a number refers to the amount of squares
						// in a row/column/box which still contains the number in its possArray

						// For a Hidden Pair, all numbers must have a possPrevalence of 2
						// For a Hidden Triple, all numbers must have a possPrevalence of 2-3
						// For a Hidden Quad, all numbers must have a possPrevalence of 2-4

						int[][] possPrevalence = null;

						switch(typeOfRCB)
						{
							case Row: possPrevalence = mySudoku.rowPossPrevalence; break;
							case Col: possPrevalence = mySudoku.colPossPrevalence; break;
							case Box: possPrevalence = mySudoku.boxPossPrevalence; break;
						}

						int prevA = possPrevalence[intOfRCB][a-1];
						int prevB = possPrevalence[intOfRCB][b-1];
						int prevC = possPrevalence[intOfRCB][c-1];
						int prevD = possPrevalence[intOfRCB][d-1];

						switch(subsetType)
						{
							case Pair: if(!(inRange(prevA,2,2) && inRange(prevB,2,2))){continue;} break;
							case Trip: if(!(inRange(prevA,2,3) && inRange(prevB,2,3) && inRange(prevC,2,3))){continue;} break;
							case Quad: if(!(inRange(prevA,2,4) && inRange(prevB,2,4) && inRange(prevC,2,4) && inRange(prevD,2,4))){continue;} break;
						}


						List<Integer> possWeCheck = null;

						switch(subsetType)
						{
							case Pair: possWeCheck = Arrays.asList(a,b); break;
							case Trip: possWeCheck = Arrays.asList(a,b,c); break;
							case Quad: possWeCheck = Arrays.asList(a,b,c,d); break;
						}

						stepInfo = checkForHiddenSubset(mySudoku,subsetType,currentRCB,typeOfRCB,intOfRCB,possWeCheck);

						if(stepInfo != null)
							return stepInfo;
					}
				}
			}
		}

		return stepInfo;

	} // canSubsetExistInRCB()


	private static StepInfo checkForHiddenSubset(FullSudoku mySudoku, PTQ subsetType, Square[] currentRCB, HouseType typeOfRCB, int intOfRCB, List<Integer> possWeCheck)
	{
		// Time to see whether we actually have a Hidden Subset

		// Go through all possWeCheck, counting how many squares can still contain at least one of them

		// For a Hidden Pair, the total amount of squares which can still contain any one of them must be 2
		// For a Hidden Triple, the total amount of squares which can still contain any one of them must be 3
		// For a Hidden Quad, the total amount of squares which can still contain any one of them must be 4
		// If the amount marked is greater, then there is no Hidden Subset

		boolean[] doesSquareContainAnyPoss = {false,false,false,false,false,false,false,false,false};
		int totalSquaresLeft = 0;

		for(int poss : possWeCheck)
		{
			for(int intOfSquare=0;intOfSquare<9;intOfSquare++)
			{
				Square squareInRCB = currentRCB[intOfSquare];

				if(squareInRCB.result == null && squareInRCB.possArray[poss-1] != null)
				{
					if(doesSquareContainAnyPoss[intOfSquare] == false)
					{
						doesSquareContainAnyPoss[intOfSquare] = true;
						totalSquaresLeft++;
					}
				}
			}
		}

		switch(subsetType)
		{
			case Pair: if(totalSquaresLeft > 2){return null;} break;
			case Trip: if(totalSquaresLeft > 3){return null;} break;
			case Quad: if(totalSquaresLeft > 4){return null;} break;
		}

		// If a Hidden Subset is found, the squares containing the subset lose all
		// of their possibilities except for the ones in the Hidden Subset

		boolean wereAnySquaresChanged = false;
		List<SquaresOfKilledPoss> listOfPossAndSquares = new ArrayList<SquaresOfKilledPoss>();

		for(int poss=1;poss<=9;poss++)
		{
			if(poss == possWeCheck.get(0)){continue;}
			if(poss == possWeCheck.get(1)){continue;}
			if(subsetType.getEasyInt() >= 3 && poss == possWeCheck.get(2)){continue;}
			if(subsetType.getEasyInt() >= 4 && poss == possWeCheck.get(3)){continue;}

			List<Square> affectedSquaresForPoss = new ArrayList<Square>();

			for(int intOfSquare=0;intOfSquare<9;intOfSquare++)
			{
				if(doesSquareContainAnyPoss[intOfSquare] == true)
				{
					Square squareInRCB = currentRCB[intOfSquare];

					boolean isPossEliminated = mySudoku.elimFromPossArray(squareInRCB,poss);

					if(isPossEliminated)
					{
						wereAnySquaresChanged = true;
						affectedSquaresForPoss.add(squareInRCB);
					}
				}
			}

			listOfPossAndSquares.add(new SquaresOfKilledPoss(poss,affectedSquaresForPoss));
		}

		if(wereAnySquaresChanged)
		{
			List<Square> allSquaresLeft = new ArrayList<Square>();
			for(int intOfSquare=0;intOfSquare<9;intOfSquare++)
			{
				if(doesSquareContainAnyPoss[intOfSquare])
					allSquaresLeft.add(currentRCB[intOfSquare]);
			}

			return new Result_HiddenPairsTripsQuads(subsetType,typeOfRCB,intOfRCB,allSquaresLeft,possWeCheck,listOfPossAndSquares);
		}
		else
			return null;

	} // checkForHiddenSubset()

} // HiddenPairsTripsQuads