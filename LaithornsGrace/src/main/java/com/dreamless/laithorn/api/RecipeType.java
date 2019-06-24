package com.dreamless.laithorn.api;

public enum RecipeType {
	CENTERED, ONE_FRAGMENT, TWO_FRAGMENTS, THREE_FRAGMENTS, FOUR_FRAGMENTS, FIVE_FRAGMENTS, SIX_FRAGMENTS,
	SEVEN_FRAGMENTS, EIGHT_FRAGMENTS, OTHER;

	public final int getNumberOfFragments() {
		switch (this) {
		case CENTERED:
			return 8;
		case ONE_FRAGMENT:
			return 1;
		case TWO_FRAGMENTS:
			return 2;
		case THREE_FRAGMENTS:
			return 3;
		case FOUR_FRAGMENTS:
			return 4;
		case FIVE_FRAGMENTS:
			return 5;
		case SIX_FRAGMENTS:
			return 6;
		case SEVEN_FRAGMENTS:
			return 7;
		case EIGHT_FRAGMENTS:
			return 8;
		case OTHER:
			return 0;
		default:
			return 0;
		}
	}
}
