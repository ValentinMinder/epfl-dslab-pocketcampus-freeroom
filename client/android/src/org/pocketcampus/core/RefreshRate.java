package org.pocketcampus.core;

public class RefreshRate {
	private RateType rateType = RateType.ABSOLUTE;
	private int absoluteRate = 5;
	private RelativeRate relativeRate = RelativeRate.MEDIUM;
	private int rateModifier_ = 1; // TODO implement modifier accessors

	public enum RateType {
		ABSOLUTE, RELATIVE
	}

	public enum RelativeRate {
		NEVER, SLOW, MEDIUM, KIND_OF_FAST, FAST, REALLY_FAST, REALTIME
	}

	public RefreshRate(int rate) {
		rateType = RateType.ABSOLUTE;
	}

	public RefreshRate(RelativeRate rate) {
		rateType = RateType.RELATIVE;
	}

	public int rateInSeconds() {
		if(rateType == RateType.ABSOLUTE) {
			return absoluteRate;
		} else {
			return relativeRateToSeconds(relativeRate);
		}
	}

	private int relativeRateToSeconds(RelativeRate relRate) {
		int rate = 0;
		
		switch(relRate) {
			case NEVER:
				return -1;
				
			case SLOW:
				rate = 60 * 15;
				
			case MEDIUM:
				rate = 60 * 3;
				
			case KIND_OF_FAST:
				rate = 45;
				
			case FAST:
				rate = 15;
				
			case REALLY_FAST:
				rate = 5;
				
			case REALTIME:
				rate = 1;
		}

		return rate * rateModifier_ ;
	}
}
