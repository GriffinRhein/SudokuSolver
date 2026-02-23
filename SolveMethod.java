public enum SolveMethod
{
	NakedSingle(MoreGeneral.Single),
	HiddenSingle(MoreGeneral.Single),
	PointingPairTriple(MoreGeneral.PCNH),
	ClaimingPairTriple(MoreGeneral.PCNH),
	NakedPair(MoreGeneral.PCNH),
	NakedTrip(MoreGeneral.PCNH),
	NakedQuad(MoreGeneral.PCNH),
	HiddenPair(MoreGeneral.PCNH),
	HiddenTrip(MoreGeneral.PCNH),
	HiddenQuad(MoreGeneral.PCNH),
	X_Wing(MoreGeneral.Fish),
	Swordfish(MoreGeneral.Fish),
	Jellyfish(MoreGeneral.Fish),
	XY_Wing(MoreGeneral.Wing);

	private SolveMethod(MoreGeneral a)
	{
		genMethod = a;
	}

	// For determining which String
	// to use in addElimStrings()

	enum MoreGeneral
	{
		Single,
		PCNH,
		Fish,
		Wing;
	}

	private final MoreGeneral genMethod;

	MoreGeneral getGenMethod()
	{
		return genMethod;
	}
}