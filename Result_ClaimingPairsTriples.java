import java.util.Collections;
import java.util.List;

public class Result_ClaimingPairsTriples extends StepInfo
{
    final PTQ subsetType;
    final HouseType typeOfRC;
    final int intOfRC;

    final int intOfBox;

    final SquaresOfKilledPoss possAndSquares;

    Result_ClaimingPairsTriples(PTQ subsetType, HouseType typeOfRC, int intOfRC, int intOfBox, SquaresOfKilledPoss possAndSquares)
    {
        this.subsetType = subsetType;
        this.typeOfRC = typeOfRC;
        this.intOfRC = intOfRC;
        this.intOfBox = intOfBox;
        this.possAndSquares = possAndSquares;
    }

    @Override
    SolveMethod getSolveMethod()
    {
        return SolveMethod.ClaimingPairTriple;
    }

    @Override
    String getStepExplainString()
    {
        StringBuilder stringToBuild = new StringBuilder("");

        if(subsetType == PTQ.Pair)
            stringToBuild.append("Claiming Pair: ");
        else
            stringToBuild.append("Claiming Triple: ");

        stringToBuild.append("Within "+houseStr(typeOfRC,intOfRC)+", the only squares which can contain number ");
        stringToBuild.append(possAndSquares.killedPoss+" are in "+houseStr(HouseType.Box,intOfBox)+".");
        stringToBuild.append("\n\n");
        stringToBuild.append("Eliminate number "+possAndSquares.killedPoss+" from all squares in ");
        stringToBuild.append(houseStr(HouseType.Box,intOfBox)+" outside of "+houseStr(typeOfRC,intOfRC)+".");
        stringToBuild.append("\n\n");

        stringToBuild = addElimStrings(stringToBuild,getSolveMethod(),Collections.singletonList(possAndSquares));
        
        return stringToBuild.toString();
    }
}