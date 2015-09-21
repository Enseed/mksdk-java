package com.enseed.mksdk.domain.dto;

public class RationalDTO {
	public int numerator;
	public int denominator;

	public double toDouble()
	{
		return (double)numerator/(double)denominator;
	}
}
