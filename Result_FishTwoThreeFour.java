import java.util.Collections;
import java.util.List;

public class Result_FishTwoThreeFour extends StepInfo
{
    final PTQ fishType;
    final int poss;

    final HouseType baseSetType;
    final HouseType coverSetType;

    final List<Integer> baseSetNumRC;
    final List<Integer> coverSetNumRC;

    final List<Square> affectedSquaresForPoss;

    Result_FishTwoThreeFour(PTQ fishType, int poss, HouseType baseSetType, List<Integer> baseSetNumRC, List<Integer> coverSetNumRC, List<Square> affectedSquaresForPoss)
    {
        this.fishType = fishType;
        this.poss = poss;
        this.baseSetType = baseSetType;
        this.baseSetNumRC = baseSetNumRC;
        this.coverSetNumRC = coverSetNumRC;
        this.affectedSquaresForPoss = affectedSquaresForPoss;

        switch(baseSetType)
        {
            case Row: coverSetType = HouseType.Col; break;
            case Col: coverSetType = HouseType.Row; break;
            default: coverSetType = null; break;
        }
    }

    @Override
    SolveMethod getSolveMethod()
    {
        switch(fishType)
        {
            case Pair: return SolveMethod.X_Wing;
            case Trip: return SolveMethod.Swordfish;
            case Quad: return SolveMethod.Jellyfish;
        }

        return null;
    }

    @Override
    String getStepExplainString()
    {
        StringBuilder stringToBuild = new StringBuilder("");

        switch(fishType)
        {
            case Pair:

                stringToBuild.append("X-Wing: Within ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(0))+" & ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(1))+", ");
                stringToBuild.append("the number "+poss+" is known to be unable to appear outside of ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(0))+" & ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(1))+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate the number "+poss+" from all squares in ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(0))+" & ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(1))+", ");
                stringToBuild.append("except those part of ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(0))+" or ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(1))+".");

                stringToBuild.append("\n\n");

                break;

            case Trip:

                stringToBuild.append("Swordfish: Within ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(1))+", & ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(2))+", ");
                stringToBuild.append("the number "+poss+" is known to be unable to appear outside of ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(1))+", & ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(2))+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate the number "+poss+" from all squares in ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(1))+", & ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(2))+" ");
                stringToBuild.append("except those part of ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(1))+", or ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(2))+".");

                stringToBuild.append("\n\n");

                break;

            case Quad:

                stringToBuild.append("Jellyfish: Within ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(1))+", ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(2))+", & ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(3))+", ");
                stringToBuild.append("the number "+poss+" is known to be unable to appear outside of ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(1))+", ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(2))+", & ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(3))+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate the number "+poss+" from all squares in ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(1))+", ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(2))+", & ");
                stringToBuild.append(houseStr(coverSetType,coverSetNumRC.get(3))+", ");
                stringToBuild.append("except those part of ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(0))+", ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(1))+", ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(2))+", or ");
                stringToBuild.append(houseStr(baseSetType,baseSetNumRC.get(3))+".");

                stringToBuild.append("\n\n");

                break;
        }

        stringToBuild = addElimStrings(stringToBuild,Collections.singletonList(new SquaresOfKilledPoss(poss,affectedSquaresForPoss)));

        return stringToBuild.toString();
    }
}