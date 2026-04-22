import java.util.List;

public class Result_HiddenPairsTripsQuads extends StepInfo
{
    final PTQ subsetType;
    final HouseType typeOfRCB;
    final int intOfRCB;

    final List<Square> squaresInSubset;
    final List<Integer> possInSubset;

    final List<SquaresOfKilledPoss> elimList;

    Result_HiddenPairsTripsQuads(PTQ subsetType, HouseType typeOfRCB, int intOfRCB, List<Square> squaresInSubset, List<Integer> possInSubset, List<SquaresOfKilledPoss> elimList)
    {
        this.subsetType = subsetType;
        this.typeOfRCB = typeOfRCB;
        this.intOfRCB = intOfRCB;
        this.squaresInSubset = squaresInSubset;
        this.possInSubset = possInSubset;
        this.elimList = elimList;
    }

    @Override
    SolveMethod getSolveMethod()
    {
        switch(subsetType)
        {
            case Pair: return SolveMethod.HiddenPair;
            case Trip: return SolveMethod.HiddenTrip;
            case Quad: return SolveMethod.HiddenQuad;
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

                stringToBuild.append("Hidden Pair: Within "+houseStr(typeOfRCB,intOfRCB)+", numbers "+possInSubset.get(0)+" & "+possInSubset.get(1)+" ");
                stringToBuild.append("must not appear in any square besides "+firstSquareCoor+" or "+secondSquareCoor+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate all numbers except "+possInSubset.get(0)+" & "+possInSubset.get(1)+" from squares "+firstSquareCoor+" & "+secondSquareCoor+".");

                stringToBuild.append("\n\n");

                break;

            case Trip:

                stringToBuild.append("Hidden Triple: Within "+houseStr(typeOfRCB,intOfRCB)+", numbers "+possInSubset.get(0)+", "+possInSubset.get(1)+", & "+possInSubset.get(2)+" ");
                stringToBuild.append("must not appear in any square besides "+firstSquareCoor+", "+secondSquareCoor+", or "+thirdSquareCoor+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate all numbers except "+possInSubset.get(0)+", "+possInSubset.get(1)+", & "+possInSubset.get(2)+" ");
                stringToBuild.append("from squares "+firstSquareCoor+", "+secondSquareCoor+", & "+thirdSquareCoor+".");

                stringToBuild.append("\n\n");

                break;

            case Quad:

                stringToBuild.append("Hidden Quad: Within "+houseStr(typeOfRCB,intOfRCB)+", numbers ");
                stringToBuild.append(possInSubset.get(0)+", "+possInSubset.get(1)+", "+possInSubset.get(2)+", & "+possInSubset.get(3)+" ");
                stringToBuild.append("must not appear in any square besides "+firstSquareCoor+", "+secondSquareCoor+", "+thirdSquareCoor+", or "+fourthSquareCoor+".");

                stringToBuild.append("\n\n");

                stringToBuild.append("Eliminate all numbers except "+possInSubset.get(0)+", "+possInSubset.get(1)+", "+possInSubset.get(2)+", & "+possInSubset.get(3)+" ");
                stringToBuild.append("from squares "+firstSquareCoor+", "+secondSquareCoor+", "+thirdSquareCoor+", & "+fourthSquareCoor+".");

                stringToBuild.append("\n\n");

                break;
        }

        stringToBuild = addElimStrings(stringToBuild,elimList);

        return stringToBuild.toString();
    }
}