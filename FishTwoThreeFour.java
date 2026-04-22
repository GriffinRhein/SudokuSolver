import java.util.ArrayList;
import java.util.List;

public class FishTwoThreeFour
{
	private FishTwoThreeFour() {}


	static StepInfo X_Wing(FullSudoku mySudoku)
	{
		return FishGeneral(mySudoku,PTQ.Pair);
	}

	static StepInfo Swordfish(FullSudoku mySudoku)
	{
		return FishGeneral(mySudoku,PTQ.Trip);
	}

	static StepInfo Jellyfish(FullSudoku mySudoku)
	{
		return FishGeneral(mySudoku,PTQ.Quad);
	}


	private static StepInfo FishGeneral(FullSudoku mySudoku, PTQ fishType)
	{
		StepInfo stepInfo = null;

		HouseType[] typesOfRC = {HouseType.Row,HouseType.Col};

		for(int poss=1;poss<=9;poss++)
		{
			for(HouseType baseSetType : typesOfRC)
			{
				for(int a=0;a<9;a++)
				{
					for(int b=a+1;b<9;b++)
					{
						if(fishType.getEasyInt() == 2)
						{
							stepInfo = checkForFish(mySudoku,fishType,poss,baseSetType,a,b,null,null);

							if(stepInfo != null)
								return stepInfo;
							else
								continue;
						}

						for(int c=b+1;c<9;c++)
						{
							if(fishType.getEasyInt() == 3)
							{
								stepInfo = checkForFish(mySudoku,fishType,poss,baseSetType,a,b,c,null);

								if(stepInfo != null)
									return stepInfo;
								else
									continue;
							}

							for(int d=c+1;d<9;d++)
							{
								if(fishType.getEasyInt() == 4)
								{
									stepInfo = checkForFish(mySudoku,fishType,poss,baseSetType,a,b,c,d);

									if(stepInfo != null)
										return stepInfo;
									else
										continue;
								}
							}
						}
					}
				}
			}
		}

		return stepInfo;

	} // FishGeneral()


	// Quick function just to see whether a number falls within
	// a range given by two others. Allows for shorthand.

	private static boolean inRange(int testing,int a,int b)
	{
		if(testing >= a && testing <= b)
			return true;

		return false;

	} // inRange()


	private static StepInfo checkForFish(FullSudoku mySudoku, PTQ fishType, int poss, HouseType baseSetType, Integer rc1, Integer rc2, Integer rc3, Integer rc4)
	{
		StepInfo stepInfo = null;


		// Before actually checking the squares of each set to see which ones can still contain the poss,
		// check HOW MANY squares in each set can still contain the poss, using the possPrevalence arrays

		// X-Wing requires exactly 2 squares in each of the two base sets to have the poss available
		// Swordfish requires 2-3 squares in each of the three base sets to have the poss available
		// Jellyfish requires 2-4 squares in each of the four base sets to have the poss available

		int[][] possPrevalence = null;

		switch(baseSetType)
		{
			case Row: possPrevalence = mySudoku.rowPossPrevalence; break;
			case Col: possPrevalence = mySudoku.colPossPrevalence; break;
		}

		Integer p1 = possPrevalence[rc1][poss-1];
		Integer p2 = possPrevalence[rc2][poss-1];
		Integer p3 = (fishType.getEasyInt() >= 3) ? possPrevalence[rc3][poss-1] : null;
		Integer p4 = (fishType.getEasyInt() >= 4) ? possPrevalence[rc4][poss-1] : null;

		switch(fishType)
		{
			case Pair: if(!(inRange(p1,2,2) && inRange(p2,2,2))){return null;} break;
			case Trip: if(!(inRange(p1,2,3) && inRange(p2,2,3) && inRange(p3,2,3))){return null;} break;
			case Quad: if(!(inRange(p1,2,4) && inRange(p2,2,4) && inRange(p3,2,4) && inRange(p4,2,4))){return null;} break;
		}


		// NOW check the squares themselves

		List<Square[]> allBaseSets = new ArrayList<Square[]>();

		switch(baseSetType)
		{
			case Row:

				allBaseSets.add(mySudoku.provideRow(rc1));
				allBaseSets.add(mySudoku.provideRow(rc2));
				if(fishType.getEasyInt() >= 3){allBaseSets.add(mySudoku.provideRow(rc3));}
				if(fishType.getEasyInt() >= 4){allBaseSets.add(mySudoku.provideRow(rc4));}

				break;

			case Col:

				allBaseSets.add(mySudoku.provideCol(rc1));
				allBaseSets.add(mySudoku.provideCol(rc2));
				if(fishType.getEasyInt() >= 3){allBaseSets.add(mySudoku.provideCol(rc3));}
				if(fishType.getEasyInt() >= 4){allBaseSets.add(mySudoku.provideCol(rc4));}

				break;
		}

		boolean[] doesSquareContainPoss = {false,false,false,false,false,false,false,false,false};
		int squaresWithPoss = 0;

		for(Square[] baseSet : allBaseSets)
		{
			for(int intOfSquare=0;intOfSquare<9;intOfSquare++)
			{
				Square theSquare = baseSet[intOfSquare];

				if(theSquare.result == null && theSquare.possArray[poss-1] != null)
				{
					if(doesSquareContainPoss[intOfSquare] == false)
					{
						doesSquareContainPoss[intOfSquare] = true;
						squaresWithPoss++;
					}
				}
			}
		}

		switch(fishType)
		{
			case Pair: if(squaresWithPoss > 2){return null;} break;
			case Trip: if(squaresWithPoss > 3){return null;} break;
			case Quad: if(squaresWithPoss > 4){return null;} break;
		}


		// If we've made it this far, we have found a potential fish;
		// we just need to see whether it can eliminate anything

		boolean wereAnySquaresChanged = false;

		// Get the cover sets

		List<Square[]> allCoverSets = new ArrayList<Square[]>();

		for(int i=0;i<doesSquareContainPoss.length;i++)
		{
			if(doesSquareContainPoss[i] == true)
			{
				switch(baseSetType)
				{
					case Row: allCoverSets.add(mySudoku.provideCol(i)); break;
					case Col: allCoverSets.add(mySudoku.provideRow(i)); break;
				}
			}
		}

		// Trying eliminating the poss in all squares which
		// are in a cover set and not in a base set

		List<Square> affectedSquaresForPoss = new ArrayList<Square>();

		for(int i=0;i<9;i++)
		{
			switch(fishType)
			{
				case Pair: if(i == rc1 || i == rc2){continue;} break;
				case Trip: if(i == rc1 || i == rc2 || i == rc3){continue;} break;
				case Quad: if(i == rc1 || i == rc2 || i == rc3 || i == rc4){continue;} break;
			}

			for(Square[] coverSet : allCoverSets)
			{
				boolean isPossEliminated = mySudoku.elimFromPossArray(coverSet[i],poss);

				if(isPossEliminated)
				{
					wereAnySquaresChanged = true;
					affectedSquaresForPoss.add(coverSet[i]);
				}
			}
		}

		if(wereAnySquaresChanged)
		{
			List<Integer> baseSetNumRC = new ArrayList<Integer>();

			baseSetNumRC.add(rc1);
			baseSetNumRC.add(rc2);
			if(fishType.getEasyInt() >= 3){baseSetNumRC.add(rc3);}
			if(fishType.getEasyInt() >= 4){baseSetNumRC.add(rc4);}

			List<Integer> coverSetNumRC = new ArrayList<Integer>();

			for(int i=0;i<doesSquareContainPoss.length;i++)
			{
				if(doesSquareContainPoss[i] == true)
					coverSetNumRC.add(i);
			}

			return new Result_FishTwoThreeFour(fishType,poss,baseSetType,baseSetNumRC,coverSetNumRC,affectedSquaresForPoss);
		}
		else
			return null;

	} // checkForFish()

} // FishTwoThreeFour