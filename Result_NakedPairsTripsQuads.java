import java.util.List;

public class Result_NakedPairsTripsQuads extends StepInfo
{
    final PTQ subsetType;
    final HouseType typeOfRCB;
    final int intOfRCB;

    final List<Integer> possInSubset;
    final List<Square> squaresInSubset;

    final List<SquaresOfKilledPoss> elimList;

    Result_NakedPairsTripsQuads(PTQ subsetType, HouseType typeOfRCB, int intOfRCB, List<Integer> possInSubset, List<Square> squaresInSubset, List<SquaresOfKilledPoss> elimList)
    {
        this.subsetType = subsetType;
        this.typeOfRCB = typeOfRCB;
        this.intOfRCB = intOfRCB;
        this.possInSubset = possInSubset;
        this.squaresInSubset = squaresInSubset;
        this.elimList = elimList;
    }

    @Override
    SolveMethod getSolveMethod()
    {
        switch(subsetType)
        {
            case Pair: return SolveMethod.NakedPair;
            case Trip: return SolveMethod.NakedTrip;
            case Quad: return SolveMethod.NakedQuad;
        }

        return null;
    }

    @Override
    String getStepExplainString()
    {
        StringBuilder stringToBuild = new StringBuilder("");

        String firstSquareCoor = squaresInSubset.get(0).getUserCoordinateString();
        String secondSquareCoor = squaresInSubset.get(1).getUserCoordinateString();
        String thirdSquareCoor = null; if(subsetType.getEasyInt() >= 3){thirdSquareCoor = squaresInSubset.get(2).getUserCoordinateString();}
        String fourthSquareCoor = null; if(subsetType.getEasyInt() >= 4){fourthSquareCoor = squaresInSubset.get(3).getUserCoordinateString();}

        switch(subsetType)
        {
            case Pair:

                stringToBuild.append("Naked Pair: Within "+houseStr(typeOfRCB,intOfRCB)+", squares "+firstSquareCoor+" & "+secondSquareCoor+" ");
                stringToBuild.append("must not contain any number besides "+possInSubset.get(0)+" or "+possInSubset.get(1)+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate numbers "+possInSubset.get(0)+" & "+possInSubset.get(1)+" from all squares in ");
                stringToBuild.append(houseStr(typeOfRCB,intOfRCB)+" except for "+firstSquareCoor+" & "+secondSquareCoor+".");

                stringToBuild.append("\n\n");

                break;

            case Trip:

                stringToBuild.append("Naked Triple: Within "+houseStr(typeOfRCB,intOfRCB)+", squares "+firstSquareCoor+", "+secondSquareCoor+", & "+thirdSquareCoor+" ");
                stringToBuild.append("must not contain any number besides "+possInSubset.get(0)+", "+possInSubset.get(1)+", or "+possInSubset.get(2)+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate numbers "+possInSubset.get(0)+", "+possInSubset.get(1)+", & "+possInSubset.get(2)+" from all squares ");
                stringToBuild.append("in "+houseStr(typeOfRCB,intOfRCB)+" except for "+firstSquareCoor+", "+secondSquareCoor+", & "+thirdSquareCoor+".");

                stringToBuild.append("\n\n");

                break;

            case Quad:

                stringToBuild.append("Naked Quad: Within "+houseStr(typeOfRCB,intOfRCB)+", squares ");
                stringToBuild.append(firstSquareCoor+", "+secondSquareCoor+", "+thirdSquareCoor+", & "+fourthSquareCoor+" must not contain any number besides ");
                stringToBuild.append(possInSubset.get(0)+", "+possInSubset.get(1)+", "+possInSubset.get(2)+", or "+possInSubset.get(3)+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate numbers "+possInSubset.get(0)+", "+possInSubset.get(1)+", "+possInSubset.get(2)+", & "+possInSubset.get(3)+" ");
                stringToBuild.append("from all squares in "+houseStr(typeOfRCB,intOfRCB)+" except for ");
                stringToBuild.append(firstSquareCoor+", "+secondSquareCoor+", "+thirdSquareCoor+", & "+fourthSquareCoor+".");

                stringToBuild.append("\n\n");

                break;
        }

        stringToBuild = addElimStrings(stringToBuild,getSolveMethod(),elimList);

        return stringToBuild.toString();
    }
}