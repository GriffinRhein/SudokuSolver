import java.util.ArrayList;
import java.util.List;

public class ClaimingPairsTriples
{
	private ClaimingPairsTriples() {}

	// Look at a single row or column, and check whether
	// any number which does not yet exist in the set is
	// restricted to appearing only in a single box.

	// If so, eliminate that number as a possibility in
	// every square of the box except the row or column.


	// Claiming Pairs/Trips Implementation

	static StepInfo ClaimingPairsTriples(FullSudoku mySudoku)
	{
		StepInfo stepInfo = null;

		HouseType[] typesOfRC = {HouseType.Row,HouseType.Col};

		for(HouseType typeOfRC : typesOfRC)
		{
			for(int intOfRC=0;intOfRC<9;intOfRC++)
			{
				int[] possPrevalenceInRC = null;

				if(typeOfRC == HouseType.Row)
					possPrevalenceInRC = mySudoku.rowPossPrevalence[intOfRC];
				else if(typeOfRC == HouseType.Col)
					possPrevalenceInRC = mySudoku.colPossPrevalence[intOfRC];

				for(int poss=1;poss<=9;poss++)
				{
					if(possPrevalenceInRC[poss-1] == 2)
						stepInfo = checkOnePossInOneRC(mySudoku,PTQ.Pair,typeOfRC,intOfRC,poss);
					else if(possPrevalenceInRC[poss-1] == 3)
						stepInfo = checkOnePossInOneRC(mySudoku,PTQ.Trip,typeOfRC,intOfRC,poss);
					else
						continue;

					if(stepInfo != null)
						return stepInfo;
				}

			} // Everything in this loop occurs once for each Row and for each Col
		} 

		return stepInfo;

	} // ClaimingPairsTriples()


	// Determines whether all squares in a Row or Col which contain a specific poss all share a Box

	static private StepInfo checkOnePossInOneRC(FullSudoku mySudoku, PTQ subsetType, HouseType typeOfRC, int intOfRC, int poss)
	{
		Square[] squaresOfRC = null;

		if(typeOfRC == HouseType.Row)
			squaresOfRC = mySudoku.provideRow(intOfRC);
		else if(typeOfRC == HouseType.Col)
			squaresOfRC = mySudoku.provideCol(intOfRC);

		Integer boxToNixPoss = null;

		boolean canWeNixPossInBox = true;

		boolean firstSquareWithPoss = true;

		for(Square squareInRC : squaresOfRC)
		{
			if(squareInRC.result == null && squareInRC.possArray[poss-1] != null)
			{
				if(firstSquareWithPoss == true)
				{
					boxToNixPoss = squareInRC.ownBox;
					firstSquareWithPoss = false;
				}
				else
				{
					if(boxToNixPoss != squareInRC.ownBox)
						canWeNixPossInBox = false;
				}
			}
		}

		if(canWeNixPossInBox)
			return tryBoxElimination(mySudoku,subsetType,typeOfRC,intOfRC,poss,boxToNixPoss);
		else
			return null;

	} // checkOnePossInOneRC()


	static private StepInfo tryBoxElimination(FullSudoku mySudoku, PTQ subsetType, HouseType typeOfRC, int intOfRC, int poss, int intOfBox)
	{
		StepInfo stepInfo = null;

		boolean wereAnySquaresChanged = false;
		List<Square> squaresAffectedInBox = null;

		Square[] squaresOfBox = mySudoku.provideBox(intOfBox);


		// Go through every Square in that Box

		for(Square squareInBox : squaresOfBox)
		{
			boolean isSquareOutsideRC = false;

			if(typeOfRC == HouseType.Row)
			{
				// Make sure the Row that the Square is in is not the Row containing the Claiming Pair/Triple
				if(squareInBox.ownRow != intOfRC)
					isSquareOutsideRC = true;
			}
			else
			{
				// Make sure the Col that the Square is in is not the Col containing the Claiming Pair/Triple
				if(squareInBox.ownCol != intOfRC)
					isSquareOutsideRC = true;
			}

			if(isSquareOutsideRC)
			{
				boolean wasPossEliminated = mySudoku.elimFromPossArray(squareInBox,poss);

				if(wasPossEliminated)
				{
					if(wereAnySquaresChanged == false)
					{
						wereAnySquaresChanged = true;

						squaresAffectedInBox = new ArrayList<Square>();
						stepInfo = new Result_ClaimingPairsTriples(subsetType,typeOfRC,intOfRC,intOfBox,new SquaresOfKilledPoss(poss,squaresAffectedInBox));
					}

					squaresAffectedInBox.add(squareInBox);
				}
			}
		}

		return stepInfo;

	} // tryBoxElimination()

} // ClaimingPairsTriples