public class Result_NakedSingle extends StepInfo
{
    final Square theSquare;

    Result_NakedSingle(Square theSquare)
    {
        this.theSquare = theSquare;
    }

    @Override
    SolveMethod getSolveMethod()
    {
        return SolveMethod.NakedSingle;
    }

    @Override
    String getStepExplainString()
    {
        StringBuilder stringToBuild = new StringBuilder("");

        stringToBuild.append("Naked Single: Square "+coordStr(HouseType.Row,theSquare.ownRow,theSquare.ownCol)+" must contain "+theSquare.result+".");

        return stringToBuild.toString();
    }
}