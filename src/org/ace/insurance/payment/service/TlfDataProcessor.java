package org.ace.insurance.payment.service;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.TlfData;
import org.ace.insurance.payment.service.interfaces.ITlfDataProcessor;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.COACode;
import org.ace.java.component.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "TlfDataProcessor")
public class TlfDataProcessor implements ITlfDataProcessor {

	public TlfData getInstance(PolicyReferenceType type, IPolicy policy, Payment payment, List<AgentCommission> agentCommissionList, boolean isRenewal) throws SystemException {
		TlfData tlfData = null;
		try {
			boolean isAgent = policy.getAgent() != null ? true : false;
			double sumInsured = policy.getTotalSumInsured();

			/* load Data */
			tlfData = loadTlfDataAccCode(type, payment.getPaymentChannel(), isAgent);
			String customerId = policy.getCustomerId();
			String customerName = policy.getCustomerName();
			tlfData.setSumInsured(sumInsured);
			tlfData.setCustomerId(customerId);
			tlfData.setCustomerName(customerName);
			tlfData.setPayment(payment);
			tlfData.setReceivableDr(payment.getSalesPoints().getReceivableAcName());
			tlfData.setSalePointName(payment.getSalesPoints().getName());
			tlfData.setAgentCommissionList(agentCommissionList);
			//tlfData.setCoinsu001(coinsu001);
			tlfData.setRenewal(isRenewal);
			tlfData.setUnit(policy.getTotalUnit());
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), "Faield to add a new TLF", e);
		}

		return tlfData;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TlfData getAgentCommissionInstance(Payment payment, AgentCommission agentCommission) throws SystemException {
		TlfData tlfData = null;
		try {
			List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
			agentCommissionList.add(agentCommission);
			/* load Data */
			tlfData = loadAgentCommissionTlfDataAccCode(agentCommission.getReferenceType(), payment);
			String customerId = null;
			String customerName = null;
			if (agentCommission.getCustomer() != null) {
				customerId = agentCommission.getCustomer().getId();
				customerName = agentCommission.getCustomer().getFullName();
			} else {
				customerId = agentCommission.getOrganization().getId();
				customerName = agentCommission.getOrganization().getName();
			}
			tlfData.setCustomerId(customerId);
			tlfData.setCustomerName(customerName);
			tlfData.setAgentCommissionList(agentCommissionList);
			tlfData.setPayment(payment);
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), "Faield to add a new TLF", e);
		}
		return tlfData;
	}

	private TlfData loadAgentCommissionTlfDataAccCode(PolicyReferenceType refType, Payment payment) {
		TlfData tlfData = new TlfData();
		String agentCodeCr = null;
		String agentCodeDr = null;
		switch (refType) {

			case ENDOWNMENT_LIFE_POLICY:
			case LIFE_BILL_COLLECTION:
				agentCodeDr = COACode.PUBLIC_LIFE_AGENT_PAYABLE;
				break;
			case SHORT_ENDOWMENT_LIFE_POLICY:
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				agentCodeDr = COACode.SHORT_ENDOWMENT_AGENT_PAYABLE;
				break;
			case FARMER_POLICY:
				agentCodeDr = COACode.FARMER_AGENT_PAYABLE;
				break;
			case PA_POLICY:
				agentCodeDr = COACode.PA_AGENT_PAYABLE;
				break;
			case SNAKE_BITE_POLICY:
				agentCodeDr = COACode.SNAKE_BITE_AGENT_PAYABLE;
				break;
			case GROUP_LIFE_POLICY:
				agentCodeDr = COACode.GROUP_LIFE_AGENT_PAYABLE;
				break;
			case SPORT_MAN_POLICY:
				agentCodeDr = COACode.SPORT_MAN_AGENT_PAYABLE;
				break;
			case HEALTH_POLICY:
			case HEALTH_POLICY_BILL_COLLECTION:
				agentCodeDr = COACode.HEALTH_AGENT_PAYABLE;
				break;
			case CRITICAL_ILLNESS_POLICY:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				agentCodeDr = COACode.CRITICAL_ILLNESS_AGENT_PAYABLE;
				break;
			case MICRO_HEALTH_POLICY:
				agentCodeDr = COACode.MICRO_HEALTH_AGENT_PAYABLE;
				break;
			default:
				break;
		}

		if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			agentCodeCr = payment.getAccountBank().getAcode();
		} else if (payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
			agentCodeCr = COACode.CASH;
		}

		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;

	}

	private TlfData loadTlfDataAccCode(PolicyReferenceType type, PaymentChannel channel, boolean isAgent) {
		TlfData tlfData = null;
		switch (type) {

			case ENDOWNMENT_LIFE_POLICY:
			case LIFE_BILL_COLLECTION:
				tlfData = loadEndownmentLifeTlfDataAccCode(channel, isAgent);
				break;
			case SHORT_ENDOWMENT_LIFE_POLICY:
			case SHORT_ENDOWMENT_LIFE_BILL_COLLECTION:
				tlfData = loadShortEndownmentTlfDataAccCode(channel, isAgent);
				break;
			case FARMER_POLICY:
				tlfData = loadFarmerTlfDataAccCode(channel, isAgent);
				break;
			case PA_POLICY:
				tlfData = loadPersonalAccidentTlfDataAccCode(channel, isAgent);
				break;
			case SNAKE_BITE_POLICY:
				tlfData = loadSnakebiteTlfDataAccCode(channel, isAgent);
				break;
			case GROUP_LIFE_POLICY:
				tlfData = loadGroupLifeTlfDataAccCode(channel, isAgent);
				break;
			case SPORT_MAN_POLICY:
				tlfData = loadSportManTlfDataAccCode(channel, isAgent);
				break;
			case HEALTH_POLICY:
			case HEALTH_POLICY_BILL_COLLECTION:
				tlfData = loadHealthTLFDataAccCode(channel, isAgent);
				break;
			case CRITICAL_ILLNESS_POLICY:
			case CRITICAL_ILLNESS_POLICY_BILL_COLLECTION:
				tlfData = loadCriticalIllnessTLFDataAccCode(channel, isAgent);
				break;
			case MICRO_HEALTH_POLICY:
				tlfData = loadMicroHealthTLFDataAccCode(channel, isAgent);
				break;
			default:
				break;
		}

		return tlfData;
	}

	private TlfData loadMotorTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.MOTOR_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MOTOR_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.MOTOR_SUNDRY : COACode.MOTOR_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.MOTOR_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MOTOR_RECEIVABLE : null;
		String servicesCodeCr = COACode.MOTOR_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MOTOR_RECEIVABLE : null;
		String stampCodeCr = COACode.MOTOR_STAMP_FEE;
		/* Co Insurance Outward */
		String coCodeDr = isCoOutward ? COACode.MOTOR_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.MOTOR_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.MOTOR_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.MOTOR_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.MOTOR_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.MOTOR_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;

	}

	private TlfData loadFireTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.FIRE_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.FIRE_SUNDRY : COACode.FIRE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		String servicesCodeCr = COACode.FIRE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		String stampCodeCr = COACode.FIRE_STAMP_FEES;
		/* Co Insurance */
		String coCodeDr = isCoOutward ? COACode.FIRE_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.FIRE_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.FIRE_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.FIRE_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.FIRE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.FIRE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadDeclarationTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.FIRE_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.FIRE_SUNDRY : COACode.FIRE_DECLARATION_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		String servicesCodeCr = COACode.FIRE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIRE_RECEIVABLE : null;
		String stampCodeCr = COACode.FIRE_STAMP_FEES;
		/* Co Insurance */
		String coCodeDr = isCoOutward ? COACode.FIRE_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.FIRE_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.FIRE_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.FIRE_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.FIRE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.FIRE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadCargoTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? "" : PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CARGO_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.CARGO_SUNDRY : COACode.CARGO_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.CARGO_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CARGO_RECEIVABLE : null;
		String servicesCodeCr = COACode.CARGO_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CARGO_RECEIVABLE : null;
		String stampCodeCr = COACode.CARGO_STAMP_FEE;
		/* Co Insurance Outward */
		String coCodeDr = isCoOutward ? COACode.CARGO_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.CARGO_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? "" : null;
		String coCommCodeCr = isCoInward ? "" : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.CARGO_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.CARGO_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;

	}

	private TlfData loadMarineHullTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.FIRE_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MARINE_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.MARINE_SUNDRY : COACode.MARINE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.MARINE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MARINE_RECEIVABLE : null;
		String servicesCodeCr = COACode.MARINE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MARINE_RECEIVABLE : null;
		String stampCodeCr = COACode.FIRE_STAMP_FEES;
		/* Co Insurance */
		String coCodeDr = isCoOutward ? COACode.MARINE_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.MARINE_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.FIRE_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.FIRE_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.MARINE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.MARINE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadCashInTransitTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.CASH_IN_TRANSIT_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_TRANSIT_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.CASH_IN_TRANSIT_SUNDRY : COACode.CASH_IN_TRANSIT_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_TRANSIT_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_TRANSIT_RECEIVABLE : null;
		String servicesCodeCr = COACode.CASH_IN_TRANSIT_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_TRANSIT_RECEIVABLE : null;
		String stampCodeCr = COACode.CASH_IN_TRANSIT_STAMP_FEES;
		/* Co Insurance */
		String coCodeDr = isCoOutward ? COACode.CASH_IN_TRANSIT_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.CASH_IN_TRANSIT_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.CASH_IN_TRANSIT_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.CASH_IN_TRANSIT_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.CASH_IN_TRANSIT_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.CASH_IN_TRANSIT_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadCashInSafeTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.CASH_IN_SAFE_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_TRANSIT_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.CASH_IN_SAFE_SUNDRY : COACode.CASH_IN_SAFE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_SAFE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_SAFE_RECEIVABLE : null;
		String servicesCodeCr = COACode.CASH_IN_SAFE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CASH_IN_SAFE_RECEIVABLE : null;
		String stampCodeCr = COACode.CASH_IN_SAFE_STAMP_FEES;
		/* Co Insurance */
		String coCodeDr = isCoOutward ? COACode.CASH_IN_SAFE_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.CASH_IN_SAFE_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.CASH_IN_SAFE_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.CASH_IN_SAFE_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.CASH_IN_SAFE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.CASH_IN_SAFE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadFidelityTlfDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? COACode.FIDELITY_COINWARD_RECEIVABLE
				: PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIDELITY_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? COACode.FIDELITY_SUNDRY : COACode.FIDELITY_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.FIDELITY_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIDELITY_RECEIVABLE : null;
		String servicesCodeCr = COACode.FIDELITY_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FIDELITY_RECEIVABLE : null;
		String stampCodeCr = COACode.FIDELITY_STAMP_FEES;
		/* Co Insurance */
		String coCodeDr = isCoOutward ? COACode.FIDELITY_SUNDRY : null;
		String coCodeCr = isCoOutward ? COACode.FIDELITY_PREMIUM : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? COACode.FIDELITY_COINWARD_COMMISSION : null;
		String coCommCodeCr = isCoInward ? COACode.FIDELITY_COINWARD_PAYABLE : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.FIDELITY_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.FIDELITY_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadShortEndownmentTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SHORT_ENDOWMENT_RECEIVABLE : null;
		String premiumCodeCr = COACode.SHORT_ENDOWMENT_LIFE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.SHORT_ENDOWMENT_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SHORT_ENDOWMENT_RECEIVABLE : null;
		String servicesCodeCr = COACode.SHORT_ENDOWMENT_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SHORT_ENDOWMENT_RECEIVABLE : null;
		String stampCodeCr = COACode.SHORT_ENDOWMENT_STAMP_FEE;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.SHORT_ENDOWMENT_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.SHORT_ENDOWMENT_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadFarmerTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FARMER_RECEIVABLE : null;
		String premiumCodeCr = COACode.FARMER_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.FARMER_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FARMER_RECEIVABLE : null;
		String servicesCodeCr = COACode.FARMER_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.FARMER_RECEIVABLE : null;
		String stampCodeCr = COACode.FARMER_STAMP_FEE;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.FARMER_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.FARMER_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadPersonalAccidentTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PA_RECEIVABLE : null;
		String premiumCodeCr = COACode.PA_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.PA_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PA_RECEIVABLE : null;
		String servicesCodeCr = COACode.PA_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PA_RECEIVABLE : null;
		String stampCodeCr = COACode.PA_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.PA_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.PA_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadSnakebiteTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SNAKE_BITE_RECEIVABLE : null;
		String premiumCodeCr = COACode.SNAKE_BITE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.SNAKE_BITE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SNAKE_BITE_RECEIVABLE : null;
		String servicesCodeCr = COACode.SNAKE_BITE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SNAKE_BITE_RECEIVABLE : null;
		String stampCodeCr = COACode.SNAKE_BITE_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.SNAKE_BITE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.SNAKE_BITE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadGroupLifeTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.GROUP_LIFE_RECEIVABLE : null;
		String premiumCodeCr = COACode.GROUP_LIFE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.GROUP_LIFE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.GROUP_LIFE_RECEIVABLE : null;
		String servicesCodeCr = COACode.GROUP_LIFE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.GROUP_LIFE_RECEIVABLE : null;
		String stampCodeCr = COACode.GROUP_LIFE_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.GROUP_LIFE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.GROUP_LIFE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadSportManTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SPORT_MAN_RECEIVABLE : null;
		String premiumCodeCr = COACode.SPORT_MAN_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.SPORT_MAN_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SPORT_MAN_RECEIVABLE : null;
		String servicesCodeCr = COACode.SPORT_MAN_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.SPORT_MAN_RECEIVABLE : null;
		String stampCodeCr = COACode.SPORT_MAN_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.SPORT_MAN_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.SPORT_MAN_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadEndownmentLifeTlfDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PUBLIC_LIFE_RECEIVABLE : null;
		String premiumCodeCr = COACode.PUBLIC_LIFE_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.PUBLIC_LIFE_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PUBLIC_LIFE_RECEIVABLE : null;
		String servicesCodeCr = COACode.PUBLIC_LIFE_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PUBLIC_LIFE_RECEIVABLE : null;
		String stampCodeCr = COACode.PUBLIC_LIFE_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.PUBLIC_LIFE_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.PUBLIC_LIFE_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadPersonTravelLifeTLFDataAccCode(PaymentChannel param, boolean isCoOutward, boolean isCoInward, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = isCoInward ? "" : PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PERSON_TRAVEL_RECEIVABLE : null;
		String premiumCodeCr = isCoOutward ? "" : COACode.GENERAL_TRAVEL_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.PERSON_TRAVEL_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PERSON_TRAVEL_RECEIVABLE : null;
		String servicesCodeCr = COACode.GENERAL_TRAVEL_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.PERSON_TRAVEL_RECEIVABLE : null;
		String stampCodeCr = COACode.STAMP_FEES;
		/* Co Insurance Outward */
		String coCodeDr = isCoOutward ? "" : null;
		String coCodeCr = isCoOutward ? "" : null;
		/* Co Insurance Inward */
		String coCommCodeDr = isCoInward ? "" : null;
		String coCommCodeCr = isCoInward ? "" : null;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.GENERAL_TRAVEL_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.PERSON_TRAVEL_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setCoCodeDr(coCodeDr);
		tlfData.setCoCodeCr(coCodeCr);
		tlfData.setCoCommCodeDr(coCommCodeDr);
		tlfData.setCoCommCodeCr(coCommCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadTravelLifeTLFDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.TRAVEL_RECEIVABLE : null;
		String premiumCodeCr = COACode.TRAVEL_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.TRAVEL_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.TRAVEL_RECEIVABLE : null;
		String servicesCodeCr = COACode.TRAVEL_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.TRAVEL_RECEIVABLE : null;
		String stampCodeCr = COACode.STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.TRAVEL_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.TRAVEL_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadHealthTLFDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.HEALTH_RECEIVABLE : null;
		String premiumCodeCr = COACode.HEALTH_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.HEALTH_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.HEALTH_RECEIVABLE : null;
		String servicesCodeCr = COACode.HEALTHL_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.HEALTH_RECEIVABLE : null;
		String stampCodeCr = COACode.HEALTHL_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.HEALTH_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.HEALTH_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadCriticalIllnessTLFDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CRITICAL_ILLNESS_RECEIVABLE : null;
		String premiumCodeCr = COACode.CRITICAL_ILLNESS_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.CRITICAL_ILLNESS_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CRITICAL_ILLNESS_RECEIVABLE : null;
		String servicesCodeCr = COACode.CRITICAL_ILLNESS_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.CRITICAL_ILLNESS_RECEIVABLE : null;
		String stampCodeCr = COACode.CRITICAL_ILLNESS_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.CRITICAL_ILLNESS_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.CRITICAL_ILLNESS_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	private TlfData loadMicroHealthTLFDataAccCode(PaymentChannel param, boolean isAgent) {
		TlfData tlfData = new TlfData();
		/* Premium */
		String premiumCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MICRO_HEALTH_RECEIVABLE : null;
		String premiumCodeCr = COACode.MICRO_HEALTH_PREMIUM;
		/* Cheque */
		String chequeCodeCr = PaymentChannel.CHEQUE.equals(param) ? COACode.MICRO_HEALTH_RECEIVABLE : null;
		/* Service Charges */
		String servicesCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MICRO_HEALTH_RECEIVABLE : null;
		String servicesCodeCr = COACode.MICRO_HEALTH_SERVICE_CHARGES;
		/* Stamp Fee */
		String stampCodeDr = PaymentChannel.CASHED.equals(param) ? COACode.CASH : PaymentChannel.CHEQUE.equals(param) ? COACode.MICRO_HEALTH_RECEIVABLE : null;
		String stampCodeCr = COACode.MICRO_HEALTH_STAMP_FEES;
		/* Agent */
		String agentCodeDr = isAgent ? COACode.MICRO_HEALTH_AGENT_COMMISSION : null;
		String agentCodeCr = isAgent ? COACode.MICRO_HEALTH_AGENT_PAYABLE : null;

		tlfData.setPremiumCodeDr(premiumCodeDr);
		tlfData.setPremiumCodeCr(premiumCodeCr);
		tlfData.setChequeCodeCr(chequeCodeCr);
		tlfData.setServicesCodeDr(servicesCodeDr);
		tlfData.setServicesCodeCr(servicesCodeCr);
		tlfData.setStampCodeDr(stampCodeDr);
		tlfData.setStampCodeCr(stampCodeCr);
		tlfData.setAgentCodeDr(agentCodeDr);
		tlfData.setAgentCodeCr(agentCodeCr);
		return tlfData;
	}

	/**
	 * AccCode for Claim
	 */

	private TlfData loadMotorClaimTlfDataAccCode() {
		TlfData tlfData = new TlfData();
		String claimOutstCodeDr = COACode.MOTOR_CLAIM_OUTSTANDING;
		String claimOutstCodeCr = COACode.MOTOR_CLAIM_OUTSTANDING;
		tlfData.setClaimOutstCodeDr(claimOutstCodeDr);
		tlfData.setClaimOutstCodeCr(claimOutstCodeCr);
		return tlfData;
	}
}
