package org.ace.insurance.payment;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.enums.NarrationType;
import org.ace.insurance.system.common.PaymentChannel;

public class NarrationHandler {
	/*
	 * This two field are optional required base on product cover type ( Unit /
	 * sumInsured).
	 */
	private double unit;
	private double sumInsured;
	private double premium;
	private String customerName;
	private PaymentChannel paymentChannel;
	private PolicyReferenceType referenceType;
	private boolean isRenewal;
	private String receiptNo;

	/* This field is optional.If sale channel is agent, must be mandatory. */
	private String agentName;
	private double agentCommission;
	private String invoiceNo;

	/* SalePoint */
	private String salePointName;

	/** with no Agent Commission case */
	public NarrationHandler(TlfData data) {
		Payment payment = data.getPayment();
		this.customerName = data.getCustomerName();
		this.sumInsured = data.getSumInsured();
		this.unit = data.getUnit();
		if (payment != null) {
			this.premium = data.getPremium();
			this.paymentChannel = payment.getPaymentChannel();
			this.referenceType = payment.getReferenceType();
			this.receiptNo = payment.getReceiptNo();
		}
		this.isRenewal = data.isRenewal();
		this.salePointName = data.getSalePointName();
	}

	/** only Agent Commission case */
	public NarrationHandler(TlfData data, AgentCommission agentCommission) {
		Payment payment = data.getPayment();
		this.customerName = data.getCustomerName();
		this.sumInsured = data.getSumInsured();
		this.unit = data.getUnit();
		if (payment != null) {
			this.premium = data.getPremium();
			this.paymentChannel = payment.getPaymentChannel();
			this.referenceType = payment.getReferenceType();
			this.receiptNo = payment.getReceiptNo();
		}
		this.isRenewal = data.isRenewal();
		if (agentCommission != null) {
			this.agentName = agentCommission.getAgent().getFullName();
			this.agentCommission = agentCommission.getCommission();
			/* case of payment agentCommission */
			this.invoiceNo = agentCommission.getInvoiceNo();
			this.referenceType = this.referenceType == null ? agentCommission.getReferenceType() : this.referenceType;
		}
	}

	public String getInstance(NarrationType narrationType) {
		String result = null;
		switch (narrationType) {
			case PREMIUM:
				result = generatePremiumNarration();
				break;
			case AGENT_COMMISSION:
				result = generateAgentCommissionNarration();
				break;
			case SERVICE_CHARGES:
				result = generateServiceChargesNarration();
				break;
			case STAMP_FEE:
				result = generateStampNarration();
				break;
			case PAYMENT_AGENT_COMMISSION:
				result = generatePaymentAgentCommissionNarration();
				break;
			case RECEIVABLE_SP:
				result = generateReceivableSalePointNarration();
				break;
			default:
				break;
		}
		return result;
	}

	private String generatePremiumNarration() {
		StringBuffer buffer = new StringBuffer();
		if (PaymentChannel.CHEQUE.equals(paymentChannel))
			buffer.append("Accrued ");
		else
			buffer.append("Being amount of ");
		switch (referenceType) {
			case SHORT_ENDOWMENT_LIFE_POLICY:
				buffer.append("Short Term Life Premium ");
				break;
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				buffer.append("Short Term Life Bill Collection ");
				break;
			case GROUP_LIFE_POLICY:
				buffer.append("Group Life Premium ");
				break;
			case ENDOWNMENT_LIFE_POLICY:
				buffer.append("Endownment Life Premium ");
				break;
			case SNAKE_BITE_POLICY:
				buffer.append("Snake Bite Premium ");
				break;
			case FARMER_POLICY:
				buffer.append("Farmer Premium ");
				break;
			case SPORT_MAN_POLICY:
				buffer.append("Sport Man Premium ");
				break;
			case LIFE_BILL_COLLECTION:
				buffer.append("Life Bill Collection ");
				break;
			case PA_POLICY:
				buffer.append("Personal Accident Premium ");
				break;
			case HEALTH_POLICY:
				buffer.append("Health Premium ");
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				buffer.append("Health Bill Collection ");
				break;
			case MICRO_HEALTH_POLICY:
				buffer.append("Micro Health Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY:
				buffer.append("Critical Illness Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				buffer.append("Critical Illness Bill Collection ");
				break;
			default:
				break;
		}
		buffer.append("to be paid by " + receiptNo + " from " + customerName);
		if (sumInsured > 0)
			buffer.append(" for Sum Insured " + Utils.getCurrencyFormatString(sumInsured));
		else
			buffer.append(" for " + unit + " Unit(s)");
		buffer.append(" and the premium amount of ");
		buffer.append(Utils.getCurrencyFormatString(premium));
		buffer.append(". ");
		return buffer.toString();
	}

	private String generateAgentCommissionNarration() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Agent Commission Payable for ");
		switch (referenceType) {

			case SHORT_ENDOWMENT_LIFE_POLICY:
				buffer.append("Short Term Life Insurance, ");
				break;
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				buffer.append("Short Term Life Bill Collection, ");
				break;
			case GROUP_LIFE_POLICY:
				buffer.append("Group Life Insurance, ");
				break;
			case ENDOWNMENT_LIFE_POLICY:
				buffer.append("Endownment Life Insurance, ");
				break;
			case SNAKE_BITE_POLICY:
				buffer.append("Snake Bite Insurance, ");
				break;
			case FARMER_POLICY:
				buffer.append("Farmer Insurance, ");
				break;
			case SPORT_MAN_POLICY:
				buffer.append("Sport Man Insurance, ");
				break;
			case LIFE_BILL_COLLECTION:
				buffer.append("Life Bill collection, ");
				break;

			case HEALTH_POLICY:
				buffer.append("Health Insurance, ");
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				buffer.append("Health Bill Collection, ");
				break;
			case MICRO_HEALTH_POLICY:
				buffer.append("Micro Health Insurance, ");
				break;
			case CRITICAL_ILLNESS_POLICY:
				buffer.append("Critical Illness Insurance, ");
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				buffer.append("Critical Illness Bill Collection, ");
				break;

			case PA_POLICY:
				buffer.append("Personal Accident Insurance, ");
				break;
			default:
				break;
		}

		buffer.append(receiptNo + " to " + agentName + " for commission amount of " + Utils.getCurrencyFormatString(agentCommission) + ".");
		return buffer.toString();
	}

	private String generateServiceChargesNarration() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Being amount of service charges for ");
		switch (referenceType) {
			case SHORT_ENDOWMENT_LIFE_POLICY:
				buffer.append("Short Term Life Premium ");
				break;
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				buffer.append("Short Term Life Bill Collection ");
				break;
			case GROUP_LIFE_POLICY:
				buffer.append("Group Life Premium ");
				break;
			case ENDOWNMENT_LIFE_POLICY:
				buffer.append("Endownment Life Premium ");
				break;
			case LIFE_BILL_COLLECTION:
				buffer.append("Life Bill Collection ");
				break;
			case SNAKE_BITE_POLICY:
				buffer.append("Snake Bite Premium ");
				break;
			case FARMER_POLICY:
				buffer.append("Farmer Premium ");
				break;
			case SPORT_MAN_POLICY:
				buffer.append("Sport Man Premium ");
				break;
			case PA_POLICY:
				buffer.append(isRenewal ? "Personal Accident Renewal Premium " : "Personal Accident Premium ");
				break;
			case HEALTH_POLICY:
				buffer.append("Health Premium ");
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				buffer.append("Health Bill Collection ");
				break;
			case MICRO_HEALTH_POLICY:
				buffer.append("Micro Health Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY:
				buffer.append("Critical Illness Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				buffer.append("Critical Illness Bill Collection ");
				break;
			default:
				break;
		}

		buffer.append(" for Receipt No " + receiptNo + ". ");
		return buffer.toString();
	}

	private String generateStampNarration() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Being amount of stamp fee for ");
		switch (referenceType) {
			case SHORT_ENDOWMENT_LIFE_POLICY:
				buffer.append("Short Term Life Premium ");
				break;
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				buffer.append("Short Term Life Bill Collection ");
				break;
			case GROUP_LIFE_POLICY:
				buffer.append("Group Life Premium ");
				break;
			case ENDOWNMENT_LIFE_POLICY:
				buffer.append("Endownment Life Premium ");
				break;
			case LIFE_BILL_COLLECTION:
				buffer.append("Life Bill Collection ");
				break;
			case SNAKE_BITE_POLICY:
				buffer.append("Snake Bite Premium ");
				break;
			case FARMER_POLICY:
				buffer.append("Farmer Premium ");
				break;
			case SPORT_MAN_POLICY:
				buffer.append("Sport Man Premium ");
				break;
			case PA_POLICY:
				buffer.append(isRenewal ? "Personal Accident Renewal Premium " : "Personal Accident Premium ");
				break;

			case HEALTH_POLICY:
				buffer.append("Health Premium ");
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				buffer.append("Health Bill Collection ");
				break;
			case MICRO_HEALTH_POLICY:
				buffer.append("Micro Health Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY:
				buffer.append("Critical Illness Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				buffer.append("Critical Illness Bill Collection ");
				break;
			default:
				break;
		}
		buffer.append(" for Receipt No " + receiptNo + ". ");
		return buffer.toString();
	}

	private String generatePaymentAgentCommissionNarration() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Commission Payment on " + referenceType.getLabel() + " for " + invoiceNo);
		buffer.append(" amount of " + agentCommission + " as per attach, to " + agentName);
		return buffer.toString();
	}

	private String generateReceivableSalePointNarration() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Receivable from " + salePointName + " for ");
		switch (referenceType) {
			case SHORT_ENDOWMENT_LIFE_POLICY:
				buffer.append("Short Term Life Premium ");
				break;
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				buffer.append("Short Term Life Bill Collection ");
				break;
			case GROUP_LIFE_POLICY:
				buffer.append("Group Life Premium ");
				break;
			case ENDOWNMENT_LIFE_POLICY:
				buffer.append("Endownment Life Premium ");
				break;
			case SNAKE_BITE_POLICY:
				buffer.append("Snake Bite Premium ");
				break;
			case FARMER_POLICY:
				buffer.append("Farmer Premium ");
				break;
			case SPORT_MAN_POLICY:
				buffer.append("Sport Man Premium ");
				break;
			case LIFE_BILL_COLLECTION:
				buffer.append("Life Bill Collection ");
				break;
			case PA_POLICY:
				buffer.append("Personal Accident Premium ");
				break;
			case HEALTH_POLICY:
				buffer.append("Health Premium ");
				break;
			case HEALTH_POLICY_BILL_COLLECTION:
				buffer.append("Health Bill Collection ");
				break;
			case MICRO_HEALTH_POLICY:
				buffer.append("Micro Health Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY:
				buffer.append("Critical Illness Premium ");
				break;
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				buffer.append("Critical Illness Bill Collection ");
				break;
			default:
				break;
		}
		buffer.append("with Receipt No " + receiptNo + ". ");
		return buffer.toString();
	}

}
