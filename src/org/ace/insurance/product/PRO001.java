package org.ace.insurance.product;

import java.io.Serializable;

import org.ace.insurance.common.Utils;

public class PRO001 implements Serializable {
	private static final long serialVersionUID = 403570860946827514L;
	private String id;
	private String mainCoverName;
	private double maxValue;
	private double minValue;
	private String maxTerm;
	private String minTerm;
	private String premiumRateType;

	public PRO001() {
	}

	public PRO001(String id, String name, double maxValue, double minValue, int maxTerm, int minTerm, PremiumRateType premiumRateType) {
		this.id = id;
		this.mainCoverName = name;
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.maxTerm = maxTerm + "";
		this.minTerm = minTerm + "";
		this.premiumRateType = Utils.isNull(premiumRateType) ? "" : premiumRateType.getLabel();
	}

	// public PRO001(String id, String name, double maxValue, double minValue,
	// int maxTerm , int minTerm) {
	// this.id = id;
	// this.mainCoverName = name;
	// this.maxValue = maxValue;
	// this.minValue = minValue;
	// this.maxTerm = maxTerm +"" ;
	// this.minTerm = minTerm + "";
	// }
	/**
	 * @param id
	 * @param mainCoverName
	 * @param currency
	 * @param maxValue
	 * @param minValue
	 * @param maxTerm
	 * @param minTerm
	 * @param premiumRateType
	 */
	/*
	 * public PRO001(String id, String mainCoverName, double maxValue, double
	 * minValue, int maxTerm, PeriodType maxTermType, int minTerm, PeriodType
	 * minTermType, PremiumRateType premiumRateType) { this.id = id;
	 * this.mainCoverName = mainCoverName; this.maxValue = maxValue;
	 * this.minValue = minValue; this.maxTerm = maxTerm + " - " +
	 * maxTermType.getLabel(); this.minTerm = minTerm + " - " +
	 * minTermType.getLabel(); this.premiumRateType =
	 * premiumRateType.getLabel(); }
	 */

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the mainCoverName
	 */
	public String getMainCoverName() {
		return mainCoverName;
	}

	/**
	 * @param mainCoverName
	 *            the mainCoverName to set
	 */
	public void setMainCoverName(String mainCoverName) {
		this.mainCoverName = mainCoverName;
	}

	/**
	 * @return the maxValue
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue
	 *            the maxValue to set
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return the minValue
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue
	 *            the minValue to set
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	/**
	 * @return the maxTerm
	 */
	public String getMaxTerm() {
		return maxTerm;
	}

	/**
	 * @param maxTerm
	 *            the maxTerm to set
	 */
	public void setMaxTerm(String maxTerm) {
		this.maxTerm = maxTerm;
	}

	/**
	 * @return the minTerm
	 */
	public String getMinTerm() {
		return minTerm;
	}

	/**
	 * @param minTerm
	 *            the minTerm to set
	 */
	public void setMinTerm(String minTerm) {
		this.minTerm = minTerm;
	}

	/**
	 * @return the premiumRateType
	 */
	public String getPremiumRateType() {
		return premiumRateType;
	}

	/**
	 * @param premiumRateType
	 *            the premiumRateType to set
	 */
	public void setPremiumRateType(String premiumRateType) {
		this.premiumRateType = premiumRateType;
	}

}
