import java.util.Collections;
import java.util.List;

public class Result_PointingPairsTriples extends StepInfo
{
    final PTQ subsetType;
    final int intOfBox;

    final HouseType typeOfRC;
    final int intOfRC;

    final SquaresOfKilledPoss possAndSquares;

    Result_PointingPairsTriples(PTQ subsetType, int intOfBox, HouseType typeOfRC, int intOfRC, SquaresOfKilledPoss possAndSquares)
    {
        this.subsetType = subsetType;
        this.intOfBox = intOfBox;
        this.typeOfRC = typeOfRC;
        this.intOfRC = intOfRC;
        this.possAndSquares = possAndSquares;
    }

    @Override
    SolveMethod getSolveMethod()
    {
        return SolveMethod.PointingPairTriple;
    }

    @Override
    String getStepExplainString()
    {
        StringBuilder stringToBuild = new StringBuilder("");

        if(subsetType == PTQ.Pair)
            stringToBuild.append("Pointing Pair: ");
        else
            stringToBuild.append("Pointing Triple: ");

        stringToBuild.append("Within "+houseStr(HouseType.Box,intOfBox)+", the only squares which can contain number ");
        stringToBuild.append(possAndSquares.killedPoss+" are in "+houseStr(typeOfRC,intOfRC)+".");
        stringToBuild.append("\n\n");
        stringToBuild.append("Eliminate number "+possAndSquares.killedPoss+" from all squares in ");
        stringToBuild.append(houseStr(typeOfRC,intOfRC)+" outside of "+houseStr(HouseType.Box,intOfBox)+".");
        stringToBuild.append("\n\n");

        stringToBuild = addElimStrings(stringToBuild,getSolveMethod(),Collections.singletonList(possAndSquares));
        
        return stringToBuild.toString();
    }
}