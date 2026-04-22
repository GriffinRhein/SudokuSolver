import java.util.List;

public abstract class StepInfo
{
    abstract SolveMethod getSolveMethod();
    abstract String getStepExplainString();


	String houseStr(HouseType inputHT, int whichSet)
	{
		String stringToReturn = inputHT+" "+Integer.toString(whichSet+1);

		return stringToReturn;

	} // houseStr()


	String coordStr(HouseType a, int whichSet, int theSquareInSet)
	{
		// myOriginalY and myOriginalX are the coordinates
		// the Sudoku actually uses when solving (0-8)

		int myOriginalY;
		int myOriginalX;

		if(a == HouseType.Row)
		{
			myOriginalY = whichSet;
			myOriginalX = theSquareInSet;
		}
		else if(a == HouseType.Col)
		{
			myOriginalY = theSquareInSet;
			myOriginalX = whichSet;
		}
		else
		{
			myOriginalY = BoxTranslator.rowOfBoxSquare(whichSet,theSquareInSet);
			myOriginalX = BoxTranslator.colOfBoxSquare(whichSet,theSquareInSet);
		}

		String stringToReturn = "["+Integer.toString(myOriginalY+1)+"]["+Integer.toString(myOriginalX+1)+"]";

		return stringToReturn;

	} // coordStr()


    StringBuilder addElimStrings(StringBuilder s, List<SquaresOfKilledPoss> listOfPossAndSquares)
	{
		for(int i=0;i<listOfPossAndSquares.size();i++)
		{
			SquaresOfKilledPoss possAndSquares = listOfPossAndSquares.get(i);
			int lookStr = 0;

			s.append(possAndSquares.killedPoss+" is eliminated from ");

			if(possAndSquares.squaresList.isEmpty())
				s.append("no squares.");
			else
			{
				while(lookStr < possAndSquares.squaresList.size())
				{
					// Write out the coordinates of the square where the number was eliminated

					s.append(possAndSquares.squaresList.get(lookStr).getUserCoordinateString());


					// Grammar

					if(lookStr+1 >= possAndSquares.squaresList.size())
						{ s.append("."); }
					else if(lookStr+2 >= possAndSquares.squaresList.size())
					{
						if(lookStr == 0)
							s.append(" & ");
						else
							s.append(", & ");
					}
					else
						{ s.append(", "); }


					// On to the next square the number was eliminated from

					lookStr++;
				}
			}

			if(i < listOfPossAndSquares.size()-1)
				s.append("\n\n");
		}

		return s;

	} // addElimStrings()
}