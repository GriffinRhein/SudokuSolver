import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NakedPairsTripsQuads
{
	private NakedPairsTripsQuads() {}


	static StepInfo NakedPairs(FullSudoku mySudoku)
	{
		return NakedSubsets(mySudoku,PTQ.Pair);

	} // NakedPairs()

	static StepInfo NakedTrips(FullSudoku mySudoku)
	{
		return NakedSubsets(mySudoku,PTQ.Trip);

	} // NakedTrips()

	static StepInfo NakedQuads(FullSudoku mySudoku)
	{
		return NakedSubsets(mySudoku,PTQ.Quad);

	} // NakedQuads()


	private static StepInfo NakedSubsets(FullSudoku mySudoku, PTQ subsetType)
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

	} // NakedSubsets()


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

		// For a Naked Pair, all combinations of two different squares in the RCB must be checked
		// For a Naked Triple, all combinations of three different squares in the RCB must be checked
		// For a Naked Quad, all combinations of four different squares in the RCB must be checked

		// This is put into motion with four loops

		for(int a=0;a<9;a++)
		{
			for(int b=0;b<9;b++)
			{
				for(int c=0;c<9;c++)
				{
					for(int d=0;d<9;d++)
					{
						// When looking for a Naked Pair, duplicate combinations of squares are avoided by making sure a<b,c=0,d=0 (c & d aren't used past this point)
						// When looking for a Naked Triple, duplicate combinations of squares are avoided by making sure a<b,b<c,d=0 (d isn't used past this point)
						// When looking for a Naked Quad, duplicate combinations of squares are avoided by making sure a<b,b<c,c<d

						switch(subsetType)
						{
							case Pair: if(!(a < b && c == 0 && d == 0)){continue;} break;
							case Trip: if(!(a < b && b < c && d == 0)){continue;} break;
							case Quad: if(!(a < b && b < c && c < d)){continue;} break;
						}


						// For a Naked Pair to exist, the squares must all have a numPossLeft of 2
						// For a Naked Triple to exist, the squares must all have a numPossLeft of 2-3
						// For a Naked Quad to exist, the squares must all have a numPossLeft of 2-4

						int nplA = currentRCB[a].numPossLeft;
						int nplB = currentRCB[b].numPossLeft;
						int nplC = currentRCB[c].numPossLeft;
						int nplD = currentRCB[d].numPossLeft;

						switch(subsetType)
						{
							case Pair: if(!(inRange(nplA,2,2) && inRange(nplB,2,2))){continue;} break;
							case Trip: if(!(inRange(nplA,2,3) && inRange(nplB,2,3) && inRange(nplC,2,3))){continue;} break;
							case Quad: if(!(inRange(nplA,2,4) && inRange(nplB,2,4) && inRange(nplC,2,4) && inRange(nplD,2,4))){continue;} break;
						}


						List<Square> squaresWeCheck = null;

						switch(subsetType)
						{
							case Pair: squaresWeCheck = Arrays.asList(currentRCB[a],currentRCB[b]); break;
							case Trip: squaresWeCheck = Arrays.asList(currentRCB[a],currentRCB[b],currentRCB[c]); break;
							case Quad: squaresWeCheck = Arrays.asList(currentRCB[a],currentRCB[b],currentRCB[c],currentRCB[d]); break;
						}

						stepInfo = checkForNakedSubset(mySudoku,subsetType,currentRCB,typeOfRCB,intOfRCB,squaresWeCheck);

						if(stepInfo != null)
							return stepInfo;
					}
				}
			}
		}

		return stepInfo;

	} // canSubsetExistInRCB()


	private static StepInfo checkForNakedSubset(FullSudoku mySudoku, PTQ subsetType, Square[] currentRCB, HouseType typeOfRCB, int intOfRCB, List<Square> squaresWeCheck)
	{
		// Time to see whether we actually have a Naked Subset

		// Go through all squares, counting how many possibilities from 1-9 still exist in at least one square

		// For a Naked Pair, the total amount of possibilities which still exist anywhere must be 2
		// For a Naked Triple, the total amount of possibilities which still exist anywhere must be 3
		// For a Naked Quad, the total amount of possibilities which still exist anywhere must be 4
		// If the amount marked is greater, then there is no Naked Subset

		boolean[] doesPossExistInAnySquare = {false,false,false,false,false,false,false,false,false};
		int totalPossLeft = 0;

		for(Square theSquare : squaresWeCheck)
		{
			for(int poss=1;poss<=9;poss++)
			{
				if(theSquare.possArray[poss-1] != null)
				{
					if(doesPossExistInAnySquare[poss-1] == false)
					{
						doesPossExistInAnySquare[poss-1] = true;
						totalPossLeft++;
					}
				}
			}
		}

		switch(subsetType)
		{
			case Pair: if(totalPossLeft > 2){return null;} break;
			case Trip: if(totalPossLeft > 3){return null;} break;
			case Quad: if(totalPossLeft > 4){return null;} break;
		}


		// If a Naked Subset is found, the possibilities making up the subset are eliminated
		// from all squares in the RCB except for the ones housing the Naked Subset

		boolean wereAnySquaresChanged = false;
		List<Integer> allPossLeft = new ArrayList<Integer>();
		List<SquaresOfKilledPoss> listOfPossAndSquares = new ArrayList<SquaresOfKilledPoss>();

		for(int poss=1;poss<=9;poss++)
		{
			if(doesPossExistInAnySquare[poss-1] == true)
			{
				allPossLeft.add(poss);
				List<Square> affectedSquaresForPoss = new ArrayList<Square>();

				for(Square squareInRCB : currentRCB)
				{
					if(squareInRCB.sameSquare(squaresWeCheck.get(0))){continue;}
					if(squareInRCB.sameSquare(squaresWeCheck.get(1))){continue;}
					if(subsetType.getEasyInt() >= 3 && squareInRCB.sameSquare(squaresWeCheck.get(2))){continue;}
					if(subsetType.getEasyInt() >= 4 && squareInRCB.sameSquare(squaresWeCheck.get(3))){continue;}

					boolean isPossEliminated = mySudoku.elimFromPossArray(squareInRCB,poss);

					if(isPossEliminated)
					{
						wereAnySquaresChanged = true;
						affectedSquaresForPoss.add(squareInRCB);
					}
				}

				listOfPossAndSquares.add(new SquaresOfKilledPoss(poss,affectedSquaresForPoss));
			}
		}

		if(wereAnySquaresChanged)
			return new Result_NakedPairsTripsQuads(subsetType,typeOfRCB,intOfRCB,allPossLeft,squaresWeCheck,listOfPossAndSquares);
		else
			return null;

	} // checkForNakedSubset()

} // NakedPairsTripsQuads