public class Result_HiddenSingle extends StepInfo
{
    final Square theSquare;
    final int theResult;

    final HouseType typeOfRCB;
    final int intOfRCB;

    Result_HiddenSingle(Square theSquare, int theResult, HouseType typeOfRCB, int intOfRCB)
    {
        this.theSquare = theSquare;
        this.theResult = theResult;
        this.typeOfRCB = typeOfRCB;
        this.intOfRCB = intOfRCB;
    }

    @Override
    SolveMethod getSolveMethod()
    {
        return SolveMethod.HiddenSingle;
    }

    @Override
    String getStepExplainString()
    {
        StringBuilder stringToBuild = new StringBuilder("");

        stringToBuild.append("Hidden Single: Square "+coordStr(HouseType.Row,theSquare.ownRow,theSquare.ownCol)+" is the only ");
        stringToBuild.append("square in "+houseStr(typeOfRCB,intOfRCB)+" which can contain "+theResult+".");

        return stringToBuild.toString();
    }
}