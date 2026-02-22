import java.util.ArrayList;
import java.util.List;

public class PointingPairsTriples
{
	private PointingPairsTriples() {}

	// Go through each of the 9 boxes, and determine whether any (unsolved) number
	// in any box is limited to one row or column within that box. If so, eliminate
	// that number from all squares in that row or column but not in that box.


	// Pointing Pairs/Trips Implementation

	static StepInfo PointingPairsTriples(FullSudoku mySudoku)
	{
		StepInfo stepInfo = null;

		// From Box 0 through Box 8, each box gets a run through the loop.

		for(int intOfBox=0;intOfBox<9;intOfBox++)
		{
			int[] possPrevalenceInBox = mySudoku.boxPossPrevalence[intOfBox];

			for(int poss=1;poss<=9;poss++)
			{
				if(possPrevalenceInBox[poss-1] == 2)
					stepInfo = checkOnePossInOneBox(mySudoku,PTQ.Pair,intOfBox,poss);
				else if(possPrevalenceInBox[poss-1] == 3)
					stepInfo = checkOnePossInOneBox(mySudoku,PTQ.Trip,intOfBox,poss);
				else
					continue;

				if(stepInfo != null)
					return stepInfo;
			}

		} // Everything in this loop occurs once for each box

		return stepInfo;

	} // PointingPairsTriples()


	// Determines whether all squares in a Box which contain a specific poss all share a Row or Col

	static private StepInfo checkOnePossInOneBox(FullSudoku mySudoku, PTQ subsetType, int intOfBox, int poss)
	{
		Square[] squaresOfBox = mySudoku.provideBox(intOfBox);

		Integer rowToNixPoss = null;
		Integer colToNixPoss = null;

		boolean canWeNixPossInRow = true;
		boolean canWeNixPossInCol = true;

		boolean firstSquareWithPoss = true;

		for(Square squareInBox : squaresOfBox)
		{
			if(squareInBox.result == null && squareInBox.possArray[poss-1] != null)
			{
				if(firstSquareWithPoss == true)
				{
					rowToNixPoss = squareInBox.ownRow;
					colToNixPoss = squareInBox.ownCol;
					firstSquareWithPoss = false;
				}
				else
				{
					if(rowToNixPoss != squareInBox.ownRow)
						canWeNixPossInRow = false;

					if(colToNixPoss != squareInBox.ownCol)
						canWeNixPossInCol = false;
				}
			}
		}

		if(canWeNixPossInRow)
			return tryRowOrColElimination(mySudoku,subsetType,intOfBox,poss,HouseType.Row,rowToNixPoss);
		else if(canWeNixPossInCol)
			return tryRowOrColElimination(mySudoku,subsetType,intOfBox,poss,HouseType.Col,colToNixPoss);
		else
			return null;

	} // checkOnePossInOneBox()


	static private StepInfo tryRowOrColElimination(FullSudoku mySudoku, PTQ subsetType, int intOfBox, int poss, HouseType typeOfRC, int intOfRC)
	{
		StepInfo stepInfo = null;

		boolean wereAnySquaresChanged = false;
		List<Square> squaresAffectedInRC = null;

		Square[] squaresOfRC;

		if(typeOfRC == HouseType.Row)
			squaresOfRC = mySudoku.provideRow(intOfRC);
		else
			squaresOfRC = mySudoku.provideCol(intOfRC);


		// Go through every Square in that Row or Col

		for(Square squareInRC : squaresOfRC)
		{
			// Make sure the Box that the Square is in is not the Box containing the Pointing Pair/Triple

			boolean isSquareOutsideBox = false;

			if(typeOfRC == HouseType.Row)
			{
				// If we're going through a Row overlapping the Box, we know the Square is
				// outside the Box iff the Col it's in does not overlap with the Box
				if(!(BoxTranslator.doesColOverlapBox(intOfBox,squareInRC.ownCol)))
					isSquareOutsideBox = true;
			}
			else
			{
				// If we're going through a Col overlapping the Box, we know the Square is
				// outside the Box iff the Row it's in does not overlap with the Box
				if(!(BoxTranslator.doesRowOverlapBox(intOfBox,squareInRC.ownRow)))
					isSquareOutsideBox = true;
			}

			if(isSquareOutsideBox)
			{
				boolean wasPossEliminated = mySudoku.elimFromPossArray(squareInRC,poss);

				if(wasPossEliminated)
				{
					if(wereAnySquaresChanged == false)
					{
						wereAnySquaresChanged = true;

						squaresAffectedInRC = new ArrayList<Square>();
						stepInfo = new Result_PointingPairsTriples(subsetType,intOfBox,typeOfRC,intOfRC,new SquaresOfKilledPoss(poss,squaresAffectedInRC));
					}

					squaresAffectedInRC.add(squareInRC);
				}
			}
		}

		return stepInfo;

	} // tryRowOrColElimination()

} // PointingPairsTriples