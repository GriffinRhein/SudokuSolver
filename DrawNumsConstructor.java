import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.lang.StringBuilder;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DrawNumsConstructor extends PaintedObjects
{
	// Sudoku ultimately formed from the numbers entered

	FullSudoku ourSentSudoku;


	// Makes sure the puzzle is solvable, and later applies
	// that solution if the human methods falter

	NonhumanSolver theNonhumanSolver;


	// Pop-Up windows for various purposes

	private PopUpPane itsThePopUpPane = new PopUpPane(this);


	// Stock messages to insert into a JTextArea.

	private String noProgressMessage = "No further progress can be made with the integrated logical methods.";
	private String lastResortMessage = "Puzzle has been completed, using Algorithm X.";
	private String puzzleDoneMessage = "Puzzle is complete!";


	// Whether the arrow keys will currently move the square around
	// and the number keys will enter numbers into the Sudoku

	// Constructor switches it to True

	private boolean controlsOn = false;


	// Declare and initialize all KeyStroke

	private KeyStroke pressRight = KeyStroke.getKeyStroke("RIGHT");
	private KeyStroke pressLeft = KeyStroke.getKeyStroke("LEFT");
	private KeyStroke pressUp = KeyStroke.getKeyStroke("UP");
	private KeyStroke pressDown = KeyStroke.getKeyStroke("DOWN");
	private KeyStroke pressOne = KeyStroke.getKeyStroke("1");
	private KeyStroke pressTwo = KeyStroke.getKeyStroke("2");
	private KeyStroke pressThree = KeyStroke.getKeyStroke("3");
	private KeyStroke pressFour = KeyStroke.getKeyStroke("4");
	private KeyStroke pressFive = KeyStroke.getKeyStroke("5");
	private KeyStroke pressSix = KeyStroke.getKeyStroke("6");
	private KeyStroke pressSeven = KeyStroke.getKeyStroke("7");
	private KeyStroke pressEight = KeyStroke.getKeyStroke("8");
	private KeyStroke pressNine = KeyStroke.getKeyStroke("9");
	private KeyStroke pressSpace = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0);
	private KeyStroke pressBack = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0);
	private KeyStroke pressDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0);
	private KeyStroke pressEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);


	// InputMap and ActionMap for the highlight controlled by the user

	private InputMap inputMap = recToWorkWith.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
	private ActionMap actionMap = recToWorkWith.getActionMap();


	// Slightly simpler functions for assigning KeyStrokes to Actions

	private void setTheAction(KeyStroke a, String b, Action c)
	{
		inputMap.put(a,b);
		actionMap.put(b,c);
	}

	private void setNumAction(KeyStroke a, String b, NumAction c)
	{
		inputMap.put(a,b);
		actionMap.put(b,c);
	}


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	private enum StatusUpdateType
	{
		InitialFill,
		LogicMethod,
		LastResort;
	}

	// Update the UI to show the current state of the Sudoku

	private void updateCurrentStatus(StatusUpdateType pointOfSolve, boolean displayImmediately)
	{
		// Give the PaintedObjects the information

		int currentSolveStep = ourSentSudoku.stepOfSolve;

		for(int i=0;i<numRowInGrid;i++)
		{
			for(int j=0;j<numColInGrid;j++)
			{
				if(fillInMap[i][j].getNum().equals(""))
				{
					if(ourSentSudoku.SudokuMap[i][j].result != null)
					{
						if(pointOfSolve == StatusUpdateType.LogicMethod)
							fillInMap[i][j].addedOnHumanSolve = true;
						else if(pointOfSolve == StatusUpdateType.LastResort)
							fillInMap[i][j].addedOnLastResort = true;
					}

					fillInMap[i][j].setNum(ourSentSudoku.SudokuMap[i][j].result,currentSolveStep);
					fillInMap[i][j].setPossArray(ourSentSudoku.SudokuMap[i][j].possArray,currentSolveStep);
				}
			}
		}


		if(displayImmediately)
		{
			// Display it

			for(int i=0;i<numRowInGrid;i++)
			{
				for(int j=0;j<numColInGrid;j++)
				{
					fillInMap[i][j].repaint();
				}
			}

			holderOfAllSteps.revalidate();
			holderOfAllSteps.repaint();

			SwingUtilities.invokeLater(new Runnable() {

				// Note to self: Find a way to do this without needing invokeLater,
				// probably by knowing the height of each added item beforehand.

				@Override
				public void run() {
					itsTheScrollPane.revalidate();
					verticalBar.setValue(verticalBar.getMaximum());
				}
			});	
		}

	} // updateCurrentStatus()


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	private enum stepsButtonPurpose
	{
		StartSlowSolve,
		ForwardOneStep,
		GoToLastResort,
		Nothing;
	}

	// Determine the current purpose of itsTheBeginStepsButton

	private void setStepsButton(stepsButtonPurpose a)
	{
		switch(a)
		{
			case StartSlowSolve:

			itsTheBeginStepsButton.setAction(slowSolveStartAction);
			itsTheBeginStepsButton.setText("Slow Solve");
			itsTheBeginStepsButton.setEnabled(true);

			break;


			case ForwardOneStep:

			itsTheBeginStepsButton.setAction(advanceStepAction);
			itsTheBeginStepsButton.setText("Next Step");
			itsTheBeginStepsButton.setEnabled(true);

			break;


			case GoToLastResort:

			itsTheBeginStepsButton.setAction(lastResortAction);
			itsTheBeginStepsButton.setText("Last Resort");
			itsTheBeginStepsButton.setEnabled(true);

			break;


			case Nothing:

			itsTheBeginStepsButton.setAction(nothingAction);
			itsTheBeginStepsButton.setText("");
			itsTheBeginStepsButton.setEnabled(false);

			break;
		}

	} // setStepsButton()


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	// Surrounds a String with tildes and blank lines, for now

	private String bundleInTilde(String a)
	{
		String toReturn = "~~~~~~~~~~"+"\n"+a+"\n"+"~~~~~~~~~~"+"\n";

		return toReturn;

	} // bundleInTilde()


	// BEGIN SOLVING PROCEDURES

	private void finalAct(boolean allAtOnce)
	{
		// Put all Strings input by the user into a 9x9 array

		// getNum() returns an empty String of "" if no number
		// was entered, so you can count on that being in all
		// spots of stringCompForFinal thought of as empty

		String[][] stringCompForFinal = new String[numRowInGrid][numColInGrid];

		for(int y=0;y<numRowInGrid;y++)
		{
			for(int x=0;x<numColInGrid;x++)
			{
				stringCompForFinal[y][x] = fillInMap[y][x].getNum();
			}
		}


		// Create the CheckStartingNums, out of the numbers entered

		CheckStartingNums ourStartChecker = new CheckStartingNums(stringCompForFinal);


		// Make sure the user has not inserted duplicates within a row/column/box

		if(!(ourStartChecker.isEntryDupeFree()))
		{
			itsThePopUpPane.showInsertedDupeMessage();
		}

		// Make sure the user has entered at least 17 numbers

		else if(!(ourStartChecker.areThereSeventeenClues()))
		{
			itsThePopUpPane.showNotEnoughCluesMessage();
		}

		// If so, advance

		else
		{
			// Create FullSudoku, the puzzle itself

			ourSentSudoku = new FullSudoku(stringCompForFinal);


			// Make sure the puzzle is solvable, before doing anything else

			theNonhumanSolver = new NonhumanSolver(ourSentSudoku);
			int virtualSolveSolutions = theNonhumanSolver.dancingLinksSolve();

			if(virtualSolveSolutions <= 0)
			{
				itsThePopUpPane.showNoSolutionMessage();
			}
			else if(virtualSolveSolutions >= 2)
			{
				itsThePopUpPane.showTooManySolutionsMessage();
			}


			// If so, advance

			else
			{
				// Lock the controls for inputting numbers

				turnControlsOff();

				recToWorkWith.setDrawStatus(false);
				recToWorkWith.repaint();


				// If this function was called using the itsTheSolveButton

				if(allAtOnce)
				{
					// Make itsTheBeginStepsButton unusable

					setStepsButton(stepsButtonPurpose.Nothing);


					// Call function to solve the puzzle all at once

					oneBigSolve();
				}

				// If this function was called using the itsTheBeginStepsButton

				else
				{
					// Change itsTheBeginStepsButton so that its new purpose
					// is to solve one more step of the puzzle

					setStepsButton(stepsButtonPurpose.ForwardOneStep);


					// Write out the opening text, and display the sudoku in its starting state

					holderOfAllSteps.addOneStep(bundleInTilde("Puzzle Start!"));

					updateCurrentStatus(StatusUpdateType.InitialFill,true);
				}
			}
		}

	} // finalAct()


	// Solve all at once

	private void oneBigSolve()
	{
		holderOfAllSteps.addOneStep(bundleInTilde("Puzzle Start!"));

		StepInfo stepInfo;

		do
		{
			stepInfo = ourSentSudoku.solveOneStep();

			if(stepInfo != null)
			{
				holderOfAllSteps.addOneStep(bundleInTilde(stepInfo.getStepExplainString()));
			}
		}
		while(stepInfo != null);


		if(ourSentSudoku.amountSquaresSolved == 81)
		{
			holderOfAllSteps.addOneStep(bundleInTilde(puzzleDoneMessage));

			updateCurrentStatus(StatusUpdateType.LogicMethod,true);
		}
		else
		{
			holderOfAllSteps.addOneStep(bundleInTilde(noProgressMessage));

			updateCurrentStatus(StatusUpdateType.LogicMethod,false);
			enactLastResort(true);
		}

	} // oneBigSolve()


	// Solve only one step

	private void advanceOneStep()
	{
		StepInfo stepInfo = ourSentSudoku.solveOneStep();

		if(ourSentSudoku.amountSquaresSolved == 81)
		{
			holderOfAllSteps.addOneStep(bundleInTilde(stepInfo.getStepExplainString()));

			holderOfAllSteps.addOneStep(bundleInTilde(puzzleDoneMessage));

			setStepsButton(stepsButtonPurpose.Nothing);
		}
		else if(stepInfo == null)
		{
			holderOfAllSteps.addOneStep(bundleInTilde(noProgressMessage));

			setStepsButton(stepsButtonPurpose.GoToLastResort);
		}
		else
		{
			holderOfAllSteps.addOneStep(bundleInTilde(stepInfo.getStepExplainString()));
		}


		updateCurrentStatus(StatusUpdateType.LogicMethod,true);

	} // advanceOneStep()


	// Last resort once logic techniques run out

	private void enactLastResort(boolean calledInOneBigSolve)
	{
		// Copy NonhumanSolver results into the sudoku squares

		theNonhumanSolver.copyFinalAnswerToSentSudoku();

		if(!(theNonhumanSolver.noConflictsInResults()))
		{
			System.out.println("This should NEVER happen, but there is CONFLICT");
			System.out.println("between the Nonhuman Solver & the Human Solver.");
		}


		// Put everything in the UI

		if(calledInOneBigSolve)
		{
			holderOfAllSteps.addOneStep(bundleInTilde(lastResortMessage));
		}
		else
		{
			holderOfAllSteps.addOneStep(bundleInTilde(lastResortMessage));

			setStepsButton(stepsButtonPurpose.Nothing);
		}

		updateCurrentStatus(StatusUpdateType.LastResort,true);

	} // enactLastResort()


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	private Action enterAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			finalAct(true);
		}
	};

	private Action slowSolveStartAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			finalAct(false);
		}
	};

	private Action advanceStepAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			advanceOneStep();
		}
	};

	private Action lastResortAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			enactLastResort(false);
		}
	};

	private Action nothingAction = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{

		}
	};


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	// Object containing some JButton functions

	private JButtonCommands itsTheButtonCommands = new JButtonCommands(this);

	// Resets entire program to starting state

	private Action resetSudoku = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			itsTheButtonCommands.resetEverything();
		}
	};

	// Reverts puzzle to what the user input

	private Action undoTheSolve = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			itsTheButtonCommands.revertToUserInput();
		}
	};

	// Save Sudoku

	private Action saveTheSudoku = new AbstractAction()
	{
		String savedAs;

		public void actionPerformed(ActionEvent e)
		{
			savedAs = itsTheButtonCommands.savingSudoku();


			// Give user the String

			itsThePopUpPane.outputTheString(savedAs);
		}
	};

	// Load Sudoku

	private Action loadTheSudoku = new AbstractAction()
	{
		String savedAs;
		int wasThereError;

		public void actionPerformed(ActionEvent e)
		{
			savedAs = itsThePopUpPane.getString();


			// Implement String into puzzle

			wasThereError = itsTheButtonCommands.loadingSudoku(savedAs);

			if(wasThereError != 0)
				itsThePopUpPane.showErrorMessage(wasThereError);
		}
	};


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	// Turn user controls off

	void turnControlsOff()
	{
		if(controlsOn)
		{
			inputMap.clear();
			actionMap.clear();

			itsTheSolveButton.setEnabled(false);
			itsTheUndoSolveButton.setEnabled(true);
			itsTheLoadSudokuButton.setEnabled(false);

			// Nothing here about the beginStepsButton;
			// that has to be handled separately on the
			// one occasion this function is called.

			controlsOn = false;
		}
	}


	// Turn user controls on

	void turnControlsOn()
	{
		if(!(controlsOn))
		{
			setTheAction(pressRight,"rightAction",rightAction);
			setTheAction(pressLeft,"leftAction",leftAction);
			setTheAction(pressUp,"upAction",upAction);
			setTheAction(pressDown,"downAction",downAction);

			setNumAction(pressOne,"oneAction",oneAction);
			setNumAction(pressTwo,"twoAction",twoAction);
			setNumAction(pressThree,"threeAction",threeAction);
			setNumAction(pressFour,"fourAction",fourAction);
			setNumAction(pressFive,"fiveAction",fiveAction);
			setNumAction(pressSix,"sixAction",sixAction);
			setNumAction(pressSeven,"sevenAction",sevenAction);
			setNumAction(pressEight,"eightAction",eightAction);
			setNumAction(pressNine,"nineAction",nineAction);
			setNumAction(pressBack,"backAction",backAction);
			setNumAction(pressDelete,"deleteAction",deleteAction);

			setTheAction(pressEnter,"enterAction",enterAction);

			itsTheSolveButton.setEnabled(true);
			itsTheUndoSolveButton.setEnabled(false);
			itsTheLoadSudokuButton.setEnabled(true);

			setStepsButton(stepsButtonPurpose.StartSlowSolve);

			controlsOn = true;
		}
	}


	// ~~~~~~~~~~
	// ~~~~~~~~~~
	// ~~~~~~~~~~


	// Constructor!!!

	DrawNumsConstructor()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,780);

		int JFrameWidth = (int)this.getWidth();
		int JFrameHeight = (int)this.getHeight();

		setSizeValues(JFrameWidth,JFrameHeight);

		int actualContentHeight = gridSquareHeight*(numRowInGrid+3);
		int twoButtonPanelWidth = gridSquareWidth*(numColInGrid/3);
		int textSideWidth = gridSquareWidth*7;


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Set up borders, and space for the actual content

		Dimension northSouthBorderSize = new Dimension( JFrameWidth,gridSquareHeight/2 );
		Dimension westBorderSize = new Dimension( gridSquareWidth,actualContentHeight );
		Dimension eastBorderSize = new Dimension( (gridSquareWidth*3)/2,actualContentHeight );
		Dimension actualContentSize = new Dimension( (gridSquareWidth*(2*(numColInGrid+7)+1))/2,actualContentHeight );


		setLayout( new BorderLayout() );

		northBorder.setOpaque(false);
		northBorder.setPreferredSize(northSouthBorderSize);

		southBorder.setOpaque(false);
		southBorder.setPreferredSize(northSouthBorderSize);

		westBorder.setOpaque(false);
		westBorder.setPreferredSize(westBorderSize);

		eastBorder.setOpaque(false);
		eastBorder.setPreferredSize(eastBorderSize);

		containsEverything.setOpaque(false);
		containsEverything.setPreferredSize(actualContentSize);

		// ADD THEM

		add(northBorder,BorderLayout.NORTH);
		add(southBorder,BorderLayout.SOUTH);
		add(westBorder,BorderLayout.WEST);
		add(eastBorder,BorderLayout.EAST);
		add(containsEverything,BorderLayout.CENTER);


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Set up containers for the Sudoku Side and the Text Side

		Dimension sudokuSideBackdropSize = new Dimension( gridSquareWidth*(numColInGrid+1),actualContentHeight );
		Dimension gapInBetweenSize = new Dimension( gridSquareWidth/2,actualContentHeight );
		Dimension textSideBackdropSize = new Dimension( textSideWidth,actualContentHeight );


		containsEverything.setLayout( new FlowLayout(FlowLayout.CENTER,0,0) );

		sudokuSideBackdrop.setOpaque(false);
		sudokuSideBackdrop.setPreferredSize(sudokuSideBackdropSize);

		highMiddleGap.setOpaque(false);
		highMiddleGap.setPreferredSize(gapInBetweenSize);

		textSideBackdrop.setOpaque(false);
		textSideBackdrop.setPreferredSize(textSideBackdropSize);

		// ADD THEM

		containsEverything.add(sudokuSideBackdrop);
		containsEverything.add(highMiddleGap);
		containsEverything.add(textSideBackdrop);


		// ~~~~~~~~~~ 
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Organize Sudoku Side with Col Labels, Row Labels, the Square highlight the user
		// moves around the grid, and the space for the containers holding JButtons

		Dimension colLabelsSize = new Dimension( gridSquareWidth*(numColInGrid+1),gridSquareHeight );
		Dimension rowLabelsSize = new Dimension( gridSquareWidth,gridSquareHeight*numRowInGrid );
		Dimension recRoomSize = new Dimension( fullGridWidth,fullGridHeight );
		Dimension boxOfBoxesSize = new Dimension( gridSquareWidth*(numColInGrid+1),gridSquareHeight*2 );


		sudokuSideBackdrop.setLayout( new BorderLayout() );

		// Col Labels

		theColLabels.setOpaque(false);
		theColLabels.setPreferredSize(colLabelsSize);

		// Row Labels

		theRowLabels.setOpaque(false);
		theRowLabels.setPreferredSize(rowLabelsSize);

		// Square Highlight

		recToWorkWith.setOpaque(false);
		recToWorkWith.setPreferredSize(recRoomSize);

		// Button Backdrop

		panelOfButtonPanels.setOpaque(false);
		panelOfButtonPanels.setPreferredSize(boxOfBoxesSize);

		// ADD THEM

		sudokuSideBackdrop.add(theColLabels, BorderLayout.NORTH);
		sudokuSideBackdrop.add(theRowLabels, BorderLayout.WEST);
		sudokuSideBackdrop.add(recToWorkWith, BorderLayout.CENTER);
		sudokuSideBackdrop.add(panelOfButtonPanels, BorderLayout.SOUTH);


		// ~~~~~~~~~~ 
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Lay grid lines over where the Square highlight can move

		Dimension gridMeasurements = new Dimension( fullGridWidth,fullGridHeight );


		recToWorkWith.setLayout( new BorderLayout() );

		gridToWorkWith.setOpaque(false);
		gridToWorkWith.setPreferredSize(gridMeasurements);

		// ADD IT

		recToWorkWith.add(gridToWorkWith, BorderLayout.CENTER);


		// ~~~~~~~~~~ 
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Create NumHolders To Accept & Display Numbers

		Dimension tileMeasurements = new Dimension(gridSquareWidth,gridSquareHeight);


		gridToWorkWith.setLayout( new GridLayout(numRowInGrid,numColInGrid) );

		for(int i=0;i<numRowInGrid;i++)
		{
			for(int j=0;j<numColInGrid;j++)
			{
				fillInMap[i][j] = new NumHolder(i,j);
				fillInMap[i][j].setOpaque(false);
				fillInMap[i][j].setPreferredSize(tileMeasurements);
			}
		}

		// ADD THEM

		for(int i=0;i<numRowInGrid;i++)
		{
			for(int j=0;j<numColInGrid;j++)
			{
				gridToWorkWith.add(fillInMap[i][j]);
			}
		}


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Fill space under Sudoku Grid with JPanels which will contain buttons

		Dimension emptyLeftPanelSize = new Dimension(gridSquareWidth,gridSquareHeight*2);
		Dimension twoButtonPanelSize = new Dimension(twoButtonPanelWidth,gridSquareHeight*2);


		panelOfButtonPanels.setLayout( new FlowLayout(FlowLayout.CENTER,0,0) );

		emptyButtonBackdrop.setOpaque(false);
		emptyButtonBackdrop.setPreferredSize(emptyLeftPanelSize);

		westButtonBackdrop.setOpaque(false);
		westButtonBackdrop.setPreferredSize(twoButtonPanelSize);

		centerButtonBackdrop.setOpaque(false);
		centerButtonBackdrop.setPreferredSize(twoButtonPanelSize);

		eastButtonBackdrop.setOpaque(false);
		eastButtonBackdrop.setPreferredSize(twoButtonPanelSize);

		// ADD THEM

		panelOfButtonPanels.add(emptyButtonBackdrop);
		panelOfButtonPanels.add(westButtonBackdrop);
		panelOfButtonPanels.add(centerButtonBackdrop);
		panelOfButtonPanels.add(eastButtonBackdrop);


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Insert JButtons into leftmost & rightmost panels for them

		Dimension standardButtonGapSize = new Dimension(twoButtonPanelWidth,gridSquareHeight/2);
		Dimension standardActualButtonSize = new Dimension((twoButtonPanelWidth*2)/3,gridSquareHeight/2);


		westButtonBackdrop.setLayout( new FlowLayout(FlowLayout.CENTER,0,0) );
		eastButtonBackdrop.setLayout( new FlowLayout(FlowLayout.CENTER,0,0) );

		// Blank JPanels which exist to take up space

		normalButtonGap1.setOpaque(false);
		normalButtonGap1.setPreferredSize(standardButtonGapSize);

		normalButtonGap2.setOpaque(false);
		normalButtonGap2.setPreferredSize(standardButtonGapSize);

		normalButtonGap3.setOpaque(false);
		normalButtonGap3.setPreferredSize(standardButtonGapSize);

		normalButtonGap4.setOpaque(false);
		normalButtonGap4.setPreferredSize(standardButtonGapSize);

		// Save Sudoku Button

		itsTheSaveSudokuButton.setFocusPainted(false);
		itsTheSaveSudokuButton.setPreferredSize(standardActualButtonSize);

		// Load Sudoku Button

		itsTheLoadSudokuButton.setFocusPainted(false);
		itsTheLoadSudokuButton.setPreferredSize(standardActualButtonSize);

		// Reset Button

		itsTheResetButton.setFocusPainted(false);
		itsTheResetButton.setPreferredSize(standardActualButtonSize);

		// Undo Solve Button

		itsTheUndoSolveButton.setFocusPainted(false);
		itsTheUndoSolveButton.setPreferredSize(standardActualButtonSize);

		// ADD THEM

		westButtonBackdrop.add(normalButtonGap1);
		westButtonBackdrop.add(itsTheSaveSudokuButton);
		westButtonBackdrop.add(normalButtonGap2);
		westButtonBackdrop.add(itsTheLoadSudokuButton);

		eastButtonBackdrop.add(normalButtonGap3);
		eastButtonBackdrop.add(itsTheResetButton);
		eastButtonBackdrop.add(normalButtonGap4);
		eastButtonBackdrop.add(itsTheUndoSolveButton);

		// Set Actions

		itsTheSaveSudokuButton.setAction(saveTheSudoku);
		itsTheSaveSudokuButton.setText("Save Sudoku");

		itsTheLoadSudokuButton.setAction(loadTheSudoku);
		itsTheLoadSudokuButton.setText("Load Sudoku");

		itsTheResetButton.setAction(resetSudoku);
		itsTheResetButton.setText("Reset All");

		itsTheUndoSolveButton.setAction(undoTheSolve);
		itsTheUndoSolveButton.setText("Undo Solve");


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Insert JButtons into center panel for them

		Dimension solveButtonGapSize = new Dimension(twoButtonPanelWidth,(gridSquareHeight/2)-10);
		Dimension actualSolveButtonSize = new Dimension(twoButtonPanelWidth,(gridSquareHeight/2)+20);


		centerButtonBackdrop.setLayout( new FlowLayout(FlowLayout.CENTER,0,0) );

		// Solve Gap 1

		solveButtonGap1.setOpaque(false);
		solveButtonGap1.setPreferredSize(solveButtonGapSize);

		// Solve Button

		itsTheSolveButton.setFocusPainted(false);
		itsTheSolveButton.setPreferredSize(actualSolveButtonSize);

		// Solve Gap 2

		solveButtonGap2.setOpaque(false);
		solveButtonGap2.setPreferredSize(solveButtonGapSize);

		// Begin Steps Button

		itsTheBeginStepsButton.setFocusPainted(false);
		itsTheBeginStepsButton.setPreferredSize(standardActualButtonSize);

		// ADD THEM

		centerButtonBackdrop.add(solveButtonGap1);
		centerButtonBackdrop.add(itsTheSolveButton);
		centerButtonBackdrop.add(solveButtonGap2);
		centerButtonBackdrop.add(itsTheBeginStepsButton);

		// Set Action

		itsTheSolveButton.setAction(enterAction);
		itsTheSolveButton.setText("Complete Solve");


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		// Insert JTextArea/JScrollPane into the proper spot on its side

		Dimension topTextGapSize = new Dimension(textSideWidth,gridSquareHeight);
		Dimension bottomTextGapSize = new Dimension(textSideWidth,gridSquareHeight*2);
		Dimension scrollPaneSize = new Dimension(textSideWidth,gridSquareHeight*numRowInGrid);


		textSideBackdrop.setLayout( new FlowLayout(FlowLayout.CENTER,0,0) );

		// Blank JPanels which exist to take up space

		textGap1.setOpaque(false);
		textGap1.setPreferredSize(topTextGapSize);

		textGap2.setOpaque(false);
		textGap2.setPreferredSize(bottomTextGapSize);

		// Scroll Pane & JPanel to hold all JTextArea

		holderOfAllSteps.setFocusable(false);
		holderOfAllSteps.setLayout( new GridBagLayout() );

		itsTheScrollPane.setFocusable(false);
		itsTheScrollPane.setPreferredSize(scrollPaneSize);

		// ADD THEM

		textSideBackdrop.add(textGap1);
		textSideBackdrop.add(itsTheScrollPane);
		textSideBackdrop.add(textGap2);


		// ~~~~~~~~~~
		// ~~~~~~~~~~
		// ~~~~~~~~~~


		pack();


		// This just makes it so hitting spacebar does nothing

		InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
		im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
		im.put(KeyStroke.getKeyStroke("released SPACE"), "none");


		// Ensure user control is allowed at start

		turnControlsOn();
		itsTheResetButton.setEnabled(true);
		itsTheSaveSudokuButton.setEnabled(true);


		// Last bit of JFrame setup

		getContentPane().setBackground( new Color(255,225,225) );
		setVisible(true);
	}

} // DrawNumsConstructor, our outermost class