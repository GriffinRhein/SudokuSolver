import java.lang.StringBuilder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class NonhumanSolver
{
	// This class solves using Dancing Links for Algorithm X

	private class Node
	{
		Node right = null;
		Node left = null;
		Node above = null;
		Node below = null;

		Integer ownRow;
		Integer ownCol;

		Node(Integer a, Integer b)
		{
			ownRow = a;
			ownCol = b;
		}
	}

	private class RowNode extends Node
	{
		boolean stillAlive = true;

		RowNode(Integer a, Integer b)
		{
			super(a,b);
		}
	}

	private class ColNode extends Node
	{
		boolean stillAlive = true;

		int numElements = 0;

		ColNode(Integer a, Integer b)
		{
			super(a,b);
		}
	}

	private FullSudoku ourSudoku;

	private RowNode[] startingCoverRows = new RowNode[729];
	private ColNode[] startingCoverCols = new ColNode[324];

	private Node topLeftNode;

	private StringBuilder writtenString; // Never actually gets shared or printed, at present

	private Deque<Node> nodesUsedToFindSolution = new ArrayDeque<>();

	private Integer[] finalListOfRowsInSolution = new Integer[81];
	private int finalListIndex = 0;
	private int startListIndex = 0;

	private Stack<Integer> deletedCols = new Stack<>();
	private Stack<Integer> deletedRows = new Stack<>();

	private boolean areWeDone = false;
	private boolean wasNonhumanSolveSuccessful = false;

	private Integer[][] nonhumanOutputGrid = new Integer[9][9];

	NonhumanSolver(FullSudoku a)
	{
		ourSudoku = a;

		for(int i=0;i<startingCoverRows.length;i++)
		{
			startingCoverRows[i] = new RowNode(i,null);
		}

		for(int i=0;i<startingCoverCols.length;i++)
		{
			startingCoverCols[i] = new ColNode(null,i);
		}

		createFullNetwork();

	} // Constructor


	private void createFullNetwork()
	{
		topLeftNode = new Node(null,null);

		startingCoverCols[0] = new ColNode(null,0);
		topLeftNode.right = startingCoverCols[0];
		startingCoverCols[0].left = topLeftNode;		
		
		for(int col=1;col<startingCoverCols.length;col++)
		{
			startingCoverCols[col] = new ColNode(null,col);
			startingCoverCols[col-1].right = startingCoverCols[col];
			startingCoverCols[col].left = startingCoverCols[col-1];
		}

		startingCoverRows[0] = new RowNode(0,null);
		topLeftNode.below = startingCoverRows[0];
		startingCoverRows[0].above = topLeftNode;

		for(int row=1;row<startingCoverRows.length;row++)
		{
			startingCoverRows[row] = new RowNode(row,null);
			startingCoverRows[row-1].below = startingCoverRows[row];
			startingCoverRows[row].above = startingCoverRows[row-1];
		}

		Node workingNode;
		Node columnTraversalNode;
		
		int matrixRow = 0;
		int[] matrixCol = new int[4];

		int squareBox;
		int[][] findingSquareBox = new int[][]{ {0,1,2}, {3,4,5}, {6,7,8} };

		for(int squareRow=0;squareRow<9;squareRow++)
		{
			for(int squareCol=0;squareCol<9;squareCol++)
			{
				squareBox = findingSquareBox[squareRow/3][squareCol/3];

				for(int squareNumMinusOne=0;squareNumMinusOne<9;squareNumMinusOne++)
				{
					workingNode = startingCoverRows[matrixRow];

					matrixCol[0] = (9*squareRow)+(squareCol);
					matrixCol[1] = 81+(9*squareRow)+(squareNumMinusOne);
					matrixCol[2] = 162+(9*squareCol)+(squareNumMinusOne);
					matrixCol[3] = 243+(9*squareBox)+(squareNumMinusOne);

					for(int i=0;i<matrixCol.length;i++)
					{
						workingNode.right = new Node(matrixRow,matrixCol[i]);
						workingNode.right.left = workingNode;
						workingNode = workingNode.right;

						columnTraversalNode = startingCoverCols[matrixCol[i]];
						while(columnTraversalNode.below != null)
						{
							columnTraversalNode = columnTraversalNode.below;
						}
						columnTraversalNode.below = workingNode;

						workingNode.above = columnTraversalNode;

						startingCoverCols[matrixCol[i]].numElements++;
					}

					matrixRow++;
				}
			}
		}

	} // createFullNetwork()


	// Called by DrawNumsConstructor

	int dancingLinksSolve()
	{
		writtenString = new StringBuilder();

		Square[][] debuggingReturn = new Square[9][9];
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				debuggingReturn[i][j] = new Square(i,j);
			}
		}

		// First, get all of the known information in.

		useKnownInformation();

		// Then, get to it.

		int totalSolutions = 0;

		while(areWeDone == false)
		{
			// This is the beginning of a new "level",
			// where you check the columns to see if
			// you have a solution and your next stop

			if(topLeftNode.right == null)
			{
				// If there are no columns left, we have found a solution.
				// Back up & slide to look for more.

				totalSolutions++;
				finalListIndex = startListIndex;

				for(Node n: nodesUsedToFindSolution)
				{
					finalListOfRowsInSolution[finalListIndex] = n.ownRow;
					finalListIndex++;
				}

				if(totalSolutions >= 2)
					areWeDone = true;
				else
					backUpAndSlide();
			}
			else
			{
				// nextColToAdd() returns null if any column has 0 elements.
				// Otherwise, it returns the number of the column with the
				// fewest amount of 1/True entries.

				Integer nextColToAdd = findNextColumn();

				if(nextColToAdd == null)
				{
					// If ANY column still exists and has 0 entries, back up & slide.

					backUpAndSlide();
				}
				else
				{
					// If there is no column with zero entries, add the first node
					// of the returned number's column to the solution set
					
					Node quickNode = startingCoverCols[nextColToAdd].below;
					nodesUsedToFindSolution.addFirst(quickNode);
					eliminationsFromAddRowSolution(quickNode.ownRow);
				}
			}
		}

		if(totalSolutions >= 1)
		{
			wasNonhumanSolveSuccessful = true;
			enterFinalAnswerIntoExtraGrid();
		}

		return totalSolutions;
	}


	// "Back up and slide" refers to what you do after hitting a dead end or a successful solution;
	// replacing your most recently added node with its successor. And if it has no successor, then
	// you remove it and replace the one before that with its successor, etc.

	private void backUpAndSlide()
	{
		// Backs up however much we need to and advanced one step in a different direction
		// Returns false if we've run out of room to back up and the algorithm is finished

		while(true)
		{
			if(nodesUsedToFindSolution.isEmpty() == true)
			{
				areWeDone = true;
				break;
			}
			else
			{
				writtenString.append("~~~~~Row "+nodesUsedToFindSolution.peekFirst().ownRow+" REMOVED from solution~~~~~\n");

				if(nodesUsedToFindSolution.peekFirst().below != null)
				{
					Node trustyNode = nodesUsedToFindSolution.removeFirst();
					restorationsFromRemoveRowSolution();

					trustyNode = trustyNode.below;
					nodesUsedToFindSolution.addFirst(trustyNode);
					eliminationsFromAddRowSolution(trustyNode.ownRow);

					break;
				}
				else
				{
					nodesUsedToFindSolution.removeFirst();
					restorationsFromRemoveRowSolution();
				}
			}
		}

	} // backUpAndSlide()


	private void useKnownInformation()
	{
		Square[][] theSudokuMap = ourSudoku.SudokuMap;
		Square currentSquare;

		int squareRow;
		int squareCol;
		int squareNumMinusOne;

		int matrixRow;

		for(int y=0;y<9;y++)
		{
			for(int x=0;x<9;x++)
			{
				currentSquare = theSudokuMap[y][x];

				if(currentSquare.result != null)
				{
					squareRow = currentSquare.ownRow;
					squareCol = currentSquare.ownCol;
					squareNumMinusOne = currentSquare.result-1;

					matrixRow = (81*squareRow)+(9*squareCol)+(squareNumMinusOne);

					finalListOfRowsInSolution[finalListIndex] = matrixRow;
					finalListIndex++;
					startListIndex++;

					eliminationsFromAddRowSolution(matrixRow);
				}
			}
		}

	} // useKnownInformation()


	private void enterFinalAnswerIntoExtraGrid()
	{
		int squareRow;
		int squareCol;
		int squareNum;

		for(Integer n: finalListOfRowsInSolution)
		{
			// If this function is being called, nothing in finalListOfRowsInSolution
			// should be null, but it's better to be safe.

			if(n != null)
			{
				squareRow = n / 81;
				squareCol = (n % 81) / 9;
				squareNum = (n % 9) + 1;

				nonhumanOutputGrid[squareRow][squareCol] = squareNum;
			}
		}

	} // enterFinalAnswerIntoExtraGrid()


	void copyFinalAnswerToSentSudoku()
	{
		// As a precautionary measure, this function does nothing
		// unless wasNonhumanSolveSuccessful was set to true

		if(wasNonhumanSolveSuccessful)
		{
			// For all squares that were not previously solved, give them their results,
			// set their numPossLeft to 1, set their possArray to null, and increment
			// amountSquaresSolved. We should end at 81 amountSquaresSolved during any success.

			Square currentSquare;

			for(int y=0;y<9;y++)
			{
				for(int x=0;x<9;x++)
				{
					currentSquare = ourSudoku.SudokuMap[y][x];

					if(currentSquare.result == null)
					{
						currentSquare.result = nonhumanOutputGrid[y][x];
						currentSquare.possArray = null;
						currentSquare.numPossLeft = 1;

						ourSudoku.amountSquaresSolved++;
					}
				}
			}

			// Then set every slot in the possPrevalence arrays to 1,
			// since it's appropriate for a finished puzzle.

			for(int i=0;i<9;i++)
			{
				for(int j=0;j<9;j++)
				{
					ourSudoku.rowPossPrevalence[i][j] = 1;
					ourSudoku.colPossPrevalence[i][j] = 1;
					ourSudoku.boxPossPrevalence[i][j] = 1;
				}
			}

			// Shouldn't REALLY be necessary, but this function should never be called twice in a row

			wasNonhumanSolveSuccessful = false;
		}

	} // copyFinalAnswerToSentSudoku()


	boolean noConflictsInResults()
	{
		Integer officialSquareNum;
		Integer nonhumanNum;

		for(int y=0;y<9;y++)
		{
			for(int x=0;x<9;x++)
			{
				officialSquareNum = ourSudoku.SudokuMap[y][x].result;
				nonhumanNum = nonhumanOutputGrid[y][x];

				if(officialSquareNum != null && nonhumanNum != null)
				{
					if(!(officialSquareNum.equals(nonhumanNum)))
						return false;
				}
			}
		}

		return true;

	} // noConflictsInResults()


	private Integer findNextColumn()
	{
		// Assumes that there is at least one column

		// Returns null if any column has 0 elements

		int nextRemainingCol = topLeftNode.right.ownCol;
		ColNode colNodeTraverser = startingCoverCols[nextRemainingCol];

		if(colNodeTraverser.numElements == 0)
			return null;

		int leastNumberOfTrue = colNodeTraverser.numElements;
		Integer colToReturn = colNodeTraverser.ownCol;
		
		while(colNodeTraverser.right != null)
		{
			nextRemainingCol = colNodeTraverser.right.ownCol;
			colNodeTraverser = startingCoverCols[nextRemainingCol];

			if(colNodeTraverser.numElements == 0)
				return null;

			if(colNodeTraverser.numElements < leastNumberOfTrue)
			{
				leastNumberOfTrue = colNodeTraverser.numElements;
				colToReturn = colNodeTraverser.ownCol;
			}
		}

		return colToReturn;

	} // findNextColumn()


	private void eliminationsFromAddRowSolution(Integer theRow)
	{
		deletedCols.push(null);
		deletedRows.push(null);

		writtenString.append("~~~~~Row "+theRow+" ADDED to solution~~~~~\n");

		int matrixOriginalRow = theRow;
		Node nodeInOriginalRow;

		int[] matrixRemovedCols = new int[4];
		int arrayIndexOne = 0;
		Node nodeInRemovedMatrixCol;

		int matrixRemovedRow;
		Node nodeInRemovedMatrixRow;
		

		// Go through that row, making note of the columns of all entries

		nodeInOriginalRow = startingCoverRows[matrixOriginalRow];
		while(nodeInOriginalRow.right != null)
		{
			nodeInOriginalRow = nodeInOriginalRow.right;
			matrixRemovedCols[arrayIndexOne] = nodeInOriginalRow.ownCol;
			arrayIndexOne++;
		}


		// For each of those columns, declare it dead, then...

		for(int matrixCol: matrixRemovedCols)
		{
			if(startingCoverCols[matrixCol].stillAlive == true)
			{
				startingCoverCols[matrixCol].stillAlive = false;

				// Attach what's to the left of the header with what's to its right.
				// ONLY the header needs to suffer this fate for the column to die.

				nodeInRemovedMatrixCol = startingCoverCols[matrixCol];

				if(nodeInRemovedMatrixCol.left != null)
					nodeInRemovedMatrixCol.left.right = nodeInRemovedMatrixCol.right;

				if(nodeInRemovedMatrixCol.right != null)
					nodeInRemovedMatrixCol.right.left = nodeInRemovedMatrixCol.left;

				deletedCols.push(matrixCol);
				writtenString.append("Eliminate Col "+matrixCol+"\n");

				// For each of those rows, declare it dead, then...

				while(nodeInRemovedMatrixCol.below != null)
				{
					nodeInRemovedMatrixCol = nodeInRemovedMatrixCol.below;
					matrixRemovedRow = nodeInRemovedMatrixCol.ownRow;

					if(startingCoverRows[matrixRemovedRow].stillAlive == true)
					{
						startingCoverRows[matrixRemovedRow].stillAlive = false;

						// Go through each element in the row. Attach what's above it & what's below it.

						nodeInRemovedMatrixRow = startingCoverRows[matrixRemovedRow];

						deletedRows.push(matrixRemovedRow);
						writtenString.append("Eliminate Row "+matrixRemovedRow+"\n");

						while(nodeInRemovedMatrixRow != null)
						{
							if(nodeInRemovedMatrixRow.above != null)
								nodeInRemovedMatrixRow.above.below = nodeInRemovedMatrixRow.below;

							if(nodeInRemovedMatrixRow.below != null)
								nodeInRemovedMatrixRow.below.above = nodeInRemovedMatrixRow.above;

							if(nodeInRemovedMatrixRow.ownCol != null)
								startingCoverCols[nodeInRemovedMatrixRow.ownCol].numElements--;

							nodeInRemovedMatrixRow = nodeInRemovedMatrixRow.right;
						}
					}
				}
			}
		}

	} // eliminationsFromAddRowSolution()


	private void restorationsFromRemoveRowSolution()
	{
		Node handyNode;

		// Re-insert removed rows

		while(deletedRows.peek() != null)
		{
			Integer readdedRow = deletedRows.pop();

			if(startingCoverRows[readdedRow].stillAlive == false)
			{
				startingCoverRows[readdedRow].stillAlive = true;
				writtenString.append("Restore Row "+startingCoverRows[readdedRow].ownRow+"\n");

				handyNode = startingCoverRows[readdedRow];

				while(handyNode != null)
				{
					if(handyNode.above != null)
						handyNode.above.below = handyNode;

					if(handyNode.below != null)
						handyNode.below.above = handyNode;

					if(handyNode.ownCol != null)
						startingCoverCols[handyNode.ownCol].numElements++;

					handyNode = handyNode.right;
				}
			}
		}

		deletedRows.pop();

		// Re-insert removed columns

		while(deletedCols.peek() != null)
		{
			Integer readdedCol = deletedCols.pop();

			if(startingCoverCols[readdedCol].stillAlive == false)
			{
				startingCoverCols[readdedCol].stillAlive = true;
				writtenString.append("Restore Col "+startingCoverCols[readdedCol].ownCol+"\n");

				handyNode = startingCoverCols[readdedCol];

				if(handyNode.left != null)
					handyNode.left.right = handyNode;

				if(handyNode.right != null)
					handyNode.right.left = handyNode;
			}
		}

		deletedCols.pop();

	} // restorationsFromRemoveRowSolution()

} // NonhumanSolver