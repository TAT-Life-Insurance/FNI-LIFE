package org.ace.insurance.web.common.document.life;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.life.factory.PolicyInsuredPersonFactory;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonDTO;
import org.ace.insurance.life.policy.SportManTravelAbroad;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionCashReceiptDTO;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class LifeDocumentBuilder extends JasperFactory {

	/** Public,ShortTerm,GroupLife Acceptance Letter */
	public static List<JasperPrint> generateLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		int numberOfEmployee = 0;
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		boolean isPublicLife;
		boolean isShortermEndownment;
		isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPublicLife = KeyFactorChecker.isPublicLife(product);
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				numberOfEmployee += 1;
			}
		}
		String stampFee = Utils.formattedCurrency(acceptedInfo.getStampFeesAmount());
		Calendar cal = Calendar.getInstance();
		String period = "";
		int periodMonths = lifeProposal.getPeriodMonth();
		cal.add(Calendar.YEAR, lifeProposal.getPeriodMonth());
		period = periodMonths / 12 + "";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
		paramMap.put("customerName", lifeProposal.getCustomerName());
		paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
		paramMap.put("phoneNo", lifeProposal.getPhoneNo());
		paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
		paramMap.put("proposalNo", lifeProposal.getProposalNo());
		paramMap.put("periodYears", period);
		paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
		paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
		paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
		paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium() - acceptedInfo.getStampFeesAmount()));
		paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
		paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
		paramMap.put("totalEmployee", numberOfEmployee);
		paramMap.put("stempFee", stampFee);
		if (lifeProposal.getAgent() != null) {
			paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
		} else {
			paramMap.put("agent", "( - )");
		}
		JasperPrint jprint = new JasperPrint();
		if (isPublicLife) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.ENDOWMENTLIFE_ACCEPTANCE_LETTER, new JREmptyDataSource());
		} else if (isShortermEndownment) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SHORTTERM_ENDOWNLIFE_ACCEPTANCE_LETTER, new JREmptyDataSource());
		} else {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.GROUPLIFE_ACCEPTANCE_LETTER, new JREmptyDataSource());
		}
		return Arrays.asList(jprint);
	}

	/** SportMan Acceptance Letter */
	public static List<JasperPrint> generateSportManAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
		paramMap.put("customerName", lifeProposal.getCustomerName());
		paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
		paramMap.put("phoneNo", lifeProposal.getPhoneNo());
		paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
		paramMap.put("proposalNo", lifeProposal.getProposalNo());
		paramMap.put("periodYears", lifeProposal.getPeriodOfYears());
		paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getSuminsuredPerUnit()));
		paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
		paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
		paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
		paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
		paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
		if (lifeProposal.getAgent() != null) {
			paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
		} else {
			paramMap.put("agent", "( - )");
		}
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORTMAN_ACCEPTANCE_LETTER, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** Farmer Acceptance Letter */
	public static List<JasperPrint> generateFarmerAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
		paramMap.put("customerName", lifeProposal.getCustomerName());
		paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
		paramMap.put("phoneNo", lifeProposal.getPhoneNo());
		paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
		paramMap.put("proposalNo", lifeProposal.getProposalNo());
		paramMap.put("periodYears", lifeProposal.getPeriodOfYears());
		paramMap.put("approvedSumInsured", lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured());
		paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
		if (lifeProposal.getAgent() != null) {
			paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
		} else {
			paramMap.put("agent", "( - )");
		}
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.FARMER_ACCEPTANCE_LETTER, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** SnakeBike Acceptance Letter */
	public static List<JasperPrint> generateSnakeBikeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getPeriodOfYears());
			paramMap.put("approvedSumInsured", lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured());
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SNAKEBIKE_ACCEPTANCE_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	/** PA Acceptance Letter */
	public static List<JasperPrint> generatePersonalAccidentAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		int numberOfEmployee = 0;
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				numberOfEmployee += 1;
			}
		}
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getPeriodMonth());
			paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
			paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
			paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("totalEmployee", numberOfEmployee);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			paramMap.put("isUSD", KeyFactorChecker.isPersonalAccidentUSD(product));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( NA )");
			}
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_ACCEPTANCE_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	/** PA Reject Letter */
	public static List<JasperPrint> generatePersonalAccidentRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("imformedDate", acceptedInfo.getInformDate());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	/** Farmer Reject Letter */
	public static List<JasperPrint> generateFarmerRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.FARMER_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	/** Life Reject Letter (not PA and Farmer) */
	public static List<JasperPrint> generateLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	/** GroupLife,PA,SportMan,Farmer,SnakeBike Invoice Letter */
	public static List<JasperPrint> generateLifeNewInvoice(LifeProposal lifeProposal, PaymentDTO payment) {
		int noOfInsured = 0;
		int noOfUnit = 0;
		double premiumRate = 0.0;
		String period = "";
		String typeOfInsurance = "";
		String insuredPersonName = "";
		String startDate;
		double totolSumInsured = 0.0;
		int periodMonths = lifeProposal.getPeriodOfInsurance();
		String proposalNo = lifeProposal.getProposalNo();
		String customerName = lifeProposal.getCustomerName();
		String customerAddress = lifeProposal.getCustomerAddress();
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		String insuredPersonAddress = insuredPerson.getFullAddress();
		Product product = insuredPerson.getProduct();
		int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
		if (numberOfInsuredPerson > 1) {
			insuredPersonName = insuredPerson.getFullName() + "  +" + (numberOfInsuredPerson - 1);
		} else {
			insuredPersonName = insuredPerson.getFullName();
		}
		String agentRegNo = lifeProposal.getAgentLiscenseNo();
		String agentName = lifeProposal.getAgentName();
		String invoice = payment.getInvoiceNo();
		String invoiceDate = Utils.formattedDate(payment.getConfirmDate());
		Calendar cal = Calendar.getInstance();
		if (lifeProposal.getStartDate() == null) {
			cal.setTime(new Date());
			startDate = Utils.formattedDate(new Date());
		} else {
			cal.setTime(lifeProposal.getStartDate());
			startDate = Utils.formattedDate(lifeProposal.getStartDate());
		}

		if (KeyFactorChecker.isGroupLife(product)) {
			typeOfInsurance = "Group Life Insurance";
			noOfInsured = lifeProposal.getProposalInsuredPersonList().size();
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			period = periodMonths + " Year";
			insuredPersonName = customerName;
			insuredPersonAddress = customerAddress;
		} else if (KeyFactorChecker.isPersonalAccident(product)) {
			typeOfInsurance = "Personal Accident Insurance";
			period = periodMonths + " Months";
			cal.add(Calendar.MONTH, lifeProposal.getPeriodMonth());
			premiumRate = insuredPerson.getPremiumRate();
		} else if (KeyFactorChecker.isFarmer(product)) {
			period = periodMonths + " Year";
			typeOfInsurance = "Farmers Life Insurance";
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
		} else if (KeyFactorChecker.isSnakeBite(product.getId())) {
			period = periodMonths + " Year";
			typeOfInsurance = "Snake Bite Life Insurance";
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			noOfUnit = lifeProposal.getUnits();
		} else if (KeyFactorChecker.isSportMan(product)) {
			period = periodMonths + " Year";
			typeOfInsurance = "Sportsmen Life Insurance";
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			noOfUnit = lifeProposal.getUnits();
		}

		String endDate = Utils.formattedDate(cal.getTime());
		if (KeyFactorChecker.isSnakeBite(product.getId()) || KeyFactorChecker.isSportMan(product))
			totolSumInsured = lifeProposal.getSuminsuredPerUnit();
		else
			totolSumInsured = lifeProposal.getTotalSumInsured();

		double totalPremium = payment.getTotalAmount();
		double discount = payment.getTotalDiscountAmount();
		double stampFee = payment.getStampFees();
		double netPremium = payment.getTotalPremium();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("noOfUnit", noOfUnit);
		paramMap.put("noOfInsured", noOfInsured);
		paramMap.put("rate", premiumRate);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("period", period);
		paramMap.put("fromDate", startDate);
		paramMap.put("toDate", endDate);
		paramMap.put("insurdName", insuredPersonName);
		paramMap.put("address", insuredPersonAddress);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("netPremium", Utils.formattedCurrency(netPremium));
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", Utils.formattedCurrency(stampFee));
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("totalSumInsured", totolSumInsured);
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint;
		if (KeyFactorChecker.isFarmer(product))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_Farmer, new JREmptyDataSource());
		else if (KeyFactorChecker.isPersonalAccidentUSD(product))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_PA_USD, new JREmptyDataSource());
		else
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_GroupPASnakeSport, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** PublicLife,ShortTerm Invoice Letter */
	public static List<JasperPrint> generateLifePaymentInvoice(LifeProposal lifeProposal, PaymentDTO payment) {
		String invoiceDate = Utils.formattedDate(payment.getConfirmDate());
		String invoice = payment.getInvoiceNo();
		String proposalNo = lifeProposal.getProposalNo();
		double sumInsured = lifeProposal.getTotalSumInsured();
		int periodMonths = lifeProposal.getPeriodOfInsurance();
		String period1 = periodMonths + " Years";
		String paymentType = lifeProposal.getPaymentType().getName();

		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		String startDate = Utils.formattedDate(lifeProposal.getSubmittedDate());
		String customerName = insuredPerson.getFullName();
		String address = insuredPerson.getFullAddress();
		String agentRegNo = lifeProposal.getAgentLiscenseNo();
		String agentName = lifeProposal.getAgentName();
		double premium = lifeProposal.getPremium();
		double termPremium = lifeProposal.getTotalTermPremium();
		double discount = payment.getTotalDiscountAmount();
		double stampFee = payment.getStampFees();
		double totalPremium = payment.getTotalAmount();
		Product product = insuredPerson.getProduct();
		String typeOfInsurance = "";
		if (KeyFactorChecker.isPublicLife(product)) {
			typeOfInsurance = "Endowment Life Insurance";
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			typeOfInsurance = "ShortTerm Endowment Life Insurance";
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("section", typeOfInsurance);
		paramMap.put("proposalNo", proposalNo);

		paramMap.put("duenum", "1 ");
		paramMap.put("period1", period1);
		paramMap.put("period", paymentType);
		paramMap.put("fromDate", startDate);

		paramMap.put("insurdName", customerName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("policyType", "New Policy");
		paramMap.put("premium", premium);
		paramMap.put("termPremium", termPremium);
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("sumInsured", sumInsured);
		paramMap.put("stampFee", stampFee);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.Life_PAYMENT_INVOICE, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** All Life Receipt Letter */
	public static List<JasperPrint> generateLifeReceiptLetter(LifeProposal lifeProposal, Payment payment, Boolean isSportsMan) {
		List<JasperPrint> printList = new ArrayList<>();
		String insuredPersonName = "";
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		Product product = insuredPerson.getProduct();
		String receiptNo = payment.getReceiptNo();
		Date paymentDate = payment.getPaymentDate();
		String customerName = lifeProposal.getCustomerName();
		String address = lifeProposal.getCustomerAddress();
		int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
		if (numberOfInsuredPerson > 1) {
			insuredPersonName = insuredPerson.getFullName() + "  +" + (numberOfInsuredPerson - 1);
		} else {
			insuredPersonName = insuredPerson.getFullName();
		}
		String insuredPersonAddress = insuredPerson.getFullAddress();
		String policyNo = lifeProposal.getProposalNo();

		String agentInfo = lifeProposal.getAgentNameNLiscenseNo();
		String proposalType = null;
		double discount = payment.getTotalDiscount();

		String paymentType = lifeProposal.getPaymentType().getName();
		String period = KeyFactorChecker.isPersonalAccident(product) ? lifeProposal.getPeriodOfInsurance() + " Months"
				: (lifeProposal.getPeriodOfInsurance() > 1 ? lifeProposal.getPeriodOfInsurance() + " Years" : lifeProposal.getPeriodOfInsurance() + " Year");
		String startDate = DateUtils.formatDateToString(lifeProposal.getStartDate());
		String endDate = DateUtils.formatDateToString(lifeProposal.getEndDate());
		double totalSI = lifeProposal.getTotalSumInsured();
		int units = lifeProposal.getUnits();
		double suminsuredPerUnit = lifeProposal.getSuminsuredPerUnit();
		double premium = payment.getTotalPremium();
		double stampFee = payment.getStampFees();
		double totalPremium = payment.getTotalAmount();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String premiumInword = convertor.getNameWithDecimal(totalPremium);
		String usdPremiumInword = convertor.getNameWithDollarDecimal(payment.getTotalPremium());
		if (stampFee > 0.0) {
			String stampFeeInword = convertor.getNameWithDecimal(stampFee);
			usdPremiumInword = usdPremiumInword.substring(0, usdPremiumInword.length() - 4);
			usdPremiumInword = convertor.getConcateDollorAndStampFee(usdPremiumInword, stampFeeInword);
		}
		String premiumRate = lifeProposal.getPremiumRateStr();
		boolean isPublicLife = false;
		boolean isGroupLife = false;
		boolean isFarmer = false;
		boolean isShortterm = false;
		boolean isPersonAccidKYT = false;
		boolean isPersonAccidUSD = false;
		boolean isSportMan = false;
		boolean isSnakeBite = false;

		if (KeyFactorChecker.isPublicLife(product))
			isPublicLife = true;
		else if (KeyFactorChecker.isShortTermEndowment(product.getId()))
			isShortterm = true;
		else if (KeyFactorChecker.isGroupLife(product)) {
			isGroupLife = true;
			insuredPersonName = customerName;
			insuredPersonAddress = address;
		} else if (KeyFactorChecker.isPersonalAccidentKYT(product))
			isPersonAccidKYT = true;
		else if (KeyFactorChecker.isPersonalAccidentUSD(product))
			isPersonAccidUSD = true;
		else if (KeyFactorChecker.isFarmer(product))
			isFarmer = true;
		else if (KeyFactorChecker.isSportMan(product)) {
			isSportMan = true;
			insuredPersonName = customerName;
			insuredPersonAddress = address;
		} else
			isSnakeBite = true;

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isFarmer", isFarmer);
		paramMap.put("isGroupLife", isGroupLife);
		paramMap.put("isPublicLife", isPublicLife);
		paramMap.put("isSnakeBite", isSnakeBite);
		paramMap.put("paymentType", paymentType);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", paymentDate);
		paramMap.put("customerName", insuredPersonName);
		paramMap.put("address", insuredPersonAddress);
		paramMap.put("proposalNo", policyNo);
		paramMap.put("agent", agentInfo);
		paramMap.put("proposalType", proposalType);
		paramMap.put("policyPeriod", period);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("totalSi", totalSI == 0.00 ? suminsuredPerUnit : totalSI);
		paramMap.put("premium", premium);
		paramMap.put("adjustAmount", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", stampFee);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("premiumInWord", premiumInword);
		paramMap.put("usdPremiumInword", usdPremiumInword);
		paramMap.put("billCollectTerm", "1 ");
		paramMap.put("suminsuredPerUnit", suminsuredPerUnit);
		paramMap.put("unit", units);
		paramMap.put("premiumRate", premiumRate);
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());

		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (isPersonAccidUSD) {
				paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size());
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PAUSD, new JREmptyDataSource());
			} else if (isGroupLife || isPersonAccidKYT) {
				paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size());
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_GROUPANDPA, new JREmptyDataSource());
			} else if (isPublicLife || isShortterm)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
			else if (isFarmer || isSportMan || isSnakeBite)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_FARMER_SPORTMAN_SNB, new JREmptyDataSource());
			printList.add(jprint);
		}
		return printList;
	}

	public static List<JasperPrint> generateSportManAbroadCertificatAndInvoice(List<SportManTravelAbroad> sportManTravelAbroadList, Payment payment) {
		List<JasperPrint> printList = new ArrayList<JasperPrint>();
		List<JasperPrint> print1 = new ArrayList<JasperPrint>();
		List<JasperPrint> print2 = new ArrayList<JasperPrint>();
		LifePolicy lifePolicy = sportManTravelAbroadList.get(0).getPolicyInsuredPerson().getLifePolicy();
		print1 = generateSportManAbroadInvoice(lifePolicy, payment);
		printList.addAll(print1);
		print2 = generateSportManAbroadCertificate(sportManTravelAbroadList, payment);
		printList.addAll(print2);
		return printList;
	}

	private static List<JasperPrint> generateSportManAbroadInvoice(LifePolicy lifePolicy, Payment payment) {
		int noOfInsured = 0;
		int noOfUnit = 0;
		double rate = 0.0;
		double totolSumInsured = 1000000.0;
		String proposalNo = lifePolicy.getProposalNo();
		String customerName = lifePolicy.getCustomerName();
		String address = lifePolicy.getCustomerAddress();
		String agentRegNo = lifePolicy.getAgent() == null ? " " : lifePolicy.getAgent().getLiscenseNo();
		String agentName = lifePolicy.getAgent() == null ? " " : lifePolicy.getAgent().getFullName();
		String invoice = payment.getInvoiceNo();
		String invoiceDate = Utils.formattedDate(payment.getConfirmDate());
		String typeOfInsurance = "Sportsmen Life Insurance";
		noOfUnit = lifePolicy.getTotalUnit();
		double totalPremium = payment.getTotalAmount();
		double stampFee = payment.getStampFees();
		double netPremium = payment.getTotalPremium();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("noOfUnit", noOfUnit);
		paramMap.put("noOfInsured", noOfInsured);
		paramMap.put("rate", rate);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("period", lifePolicy.getPeriodOfInsurance() + " Year");
		paramMap.put("fromDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("toDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insurdName", customerName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("netPremium", netPremium);
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("stampFee", stampFee);
		paramMap.put("discount", payment.getTotalDiscount());
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("totalSumInsured", totolSumInsured);
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint;
		jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_GroupPASnakeSport, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	private static List<JasperPrint> generateSportManAbroadCertificate(List<SportManTravelAbroad> sportManTravelAbroadList, Payment payment) {
		LifePolicy lifePolicy = sportManTravelAbroadList.get(0).getPolicyInsuredPerson().getLifePolicy();
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		String policyNo = lifePolicy.getPolicyNo();
		JasperPrint jprint = null;
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		for (SportManTravelAbroad sportMan : sportManTravelAbroadList) {
			PolicyInsuredPerson insuredPerson = sportMan.getPolicyInsuredPerson();
			PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
			double premium = sportMan.getTotalPremium();
			String premiumInword = convertor.getNameWithDecimal(premium);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("agenyName", agentName);
			paramMap.put("agenyLicenseNo", agentLicenseNo);
			paramMap.put("policyNo", policyNo);
			paramMap.put("startDate", Utils.formattedDate(sportMan.getTravelStartDate()));
			paramMap.put("endDate", Utils.formattedDate(sportMan.getTravelEndDate()));
			paramMap.put("travelPath", sportMan.getFromCity() + " To " + sportMan.getToCity());
			paramMap.put("insuredName", insuredPerson.getFullName());
			paramMap.put("insuredNrc", insuredPerson.getIdNo());
			paramMap.put("typeOfSport", insuredPerson.getTypesOfSport() != null ? insuredPerson.getTypesOfSport().getName() : null);
			paramMap.put("insuredAddress",
					insuredPerson.getResidentAddress() != null ? insuredPerson.getResidentAddress().getFullResidentAddress() : lifePolicy.getCustomerAddress());
			String phno = insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo();
			paramMap.put("phno", phno);
			paramMap.put("premium", Utils.formattedCurrency(premium));
			paramMap.put("premiumInword", premiumInword);
			paramMap.put("benefiName", beneifitPerson.getFullName());
			paramMap.put("benefiNrc", beneifitPerson.getIdNo());
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
			paramMap.put("benifiPhon", phno);
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORT_MAN_ABROAD_CERTIFICATE, new JREmptyDataSource());
			jprintList.add(jprint);
		}
		return jprintList;
	}

	/** ShortTerm Policy Letter */
	public static List<JasperPrint> generateShortTermLifePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		List<PolicyInsuredPersonBeneficiaries> beneifitPersonList = insuredPerson.getPolicyInsuredPersonBeneficiariesList();
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insuredName", insuredPerson.getFullName());
		paramMap.put("nextYrAge", insuredPerson.getAge());
		paramMap.put("insuredAddress", lifePolicy.getCustomerAddress());
		if (insuredPerson.getOccupation() != null)
			paramMap.put("occupation", insuredPerson.getOccupation().getName());
		else
			paramMap.put("occupation", " ");

		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("sumInsured", lifePolicy.getTotalSumInsured());
		paramMap.put("paymentType", lifePolicy.getPaymentType().getMonth());
		if (lifePolicy.getPaymentType().getMonth() > 1) {
			paramMap.put("isYearly", true);
		} else {
			paramMap.put("isYearly", false);

		}
		paramMap.put("termPremium", lifePolicy.getTotalPremium());
		paramMap.put("invoicePremium", lifePolicy.getTotalTermPremium());
		paramMap.put("coverageDateString", lifePolicy.getTimeSlotListStr());
		int size = beneifitPersonList.size();
		JasperPrint benefitJprint = null;
		if (size > 2) {
			paramMap.put("benefiName", "AS PER ATTACHMENT");
			paramMap.put("relationship", "AS PER ATTACHMENT");
			Map<String, Object> paramMapBenefit = new HashMap<String, Object>();
			paramMapBenefit.put("policyNo", lifePolicy.getPolicyNo());
			paramMapBenefit.put("idNo", insuredPerson.getIdNo());
			paramMapBenefit.put("insuredPersonName", lifePolicy.getCustomerName());
			paramMapBenefit.put("nrc", insuredPerson.getIdNo());
			paramMapBenefit.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			paramMapBenefit.put("listDataSource", new JRBeanCollectionDataSource(beneifitPersonList));
			benefitJprint = JasperFactory.generateJasperPrint(paramMapBenefit, JasperTemplate.BENEFICIARY_ATTACH, new JREmptyDataSource());
		} else {
			PolicyInsuredPersonBeneficiaries beneifitPerson = beneifitPersonList.get(0);
			paramMap.put("benefiName", beneifitPerson.getFullName());
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
			paramMap.put("percentage", beneifitPerson.getPercentage());
			if (size > 1) {
				PolicyInsuredPersonBeneficiaries beneifitPerson2 = beneifitPersonList.get(1);
				paramMap.put("benefiName2", beneifitPerson2.getFullName());
				paramMap.put("relationship2", beneifitPerson2.getRelationship().getName());
				paramMap.put("percentage2", beneifitPerson2.getPercentage());
			}
		}
		paramMap.put("currencySymbol", "Ks-");
		paramMap.put("endDateString", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate(), "dd-MMMM_yyyy"));
		paramMap.put("premiumEndDate", Utils.formattedDate(lifePolicy.abstractDatyByPaymentType(), "dd-MMMM_yyyy"));
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SHT_LIFE_POLICY, new JREmptyDataSource());
		jasperPrintList.add(jprint);
		if (benefitJprint != null) {
			jasperPrintList.add(benefitJprint);
		}

		JasperPrint termAndCjprint = JasperFactory.generateJasperPrint(null, JasperTemplate.STL_TERM_CONDITION, new JREmptyDataSource());
		JasperPrint termAndCjprint2 = JasperFactory.generateJasperPrint(null, JasperTemplate.STL_TERM_CONDITION2, new JREmptyDataSource());
		JasperPrint termAndCjprint3 = JasperFactory.generateJasperPrint(null, JasperTemplate.STL_TERM_CONDITION3, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCjprint);
		Utils.removeBlankPages(termAndCjprint2);
		Utils.removeBlankPages(termAndCjprint3);
		jasperPrintList.add(termAndCjprint);
		jasperPrintList.add(termAndCjprint2);
		jasperPrintList.add(termAndCjprint3);

		return jasperPrintList;
	}

	/** GroupLife Policy Letter */
	public static List<JasperPrint> generateGroupLifePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		Date endDate = lifePolicy.getActivedPolicyEndDate();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(startDate));
		paramMap.put("insuredName", lifePolicy.getOwnerName());
		paramMap.put("organizationName", lifePolicy.getOrganizationName());
		paramMap.put("insuredAddress", lifePolicy.getCustomerAddress());
		paramMap.put("phno", lifePolicy.getCustomerPhoneNo());
		paramMap.put("numberOfperson", lifePolicy.getInsuredPersonInfo().size());
		paramMap.put("sumInsured", lifePolicy.getTotalSumInsured());
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("termPremium", lifePolicy.getTotalPremium());
		paramMap.put("currencySymbol", "Kyats");
		paramMap.put("endDate", Utils.formattedDate(endDate));
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.GP_LIFE_POLICY, new JREmptyDataSource());
		jasperPrintList.add(jprint);
		Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
		insuredPesronMap_2.put("startDate", Utils.formattedDate(startDate));
		insuredPesronMap_2.put("endDate", Utils.formattedDate(endDate));
		List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
		for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
			perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
		}
		insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
		insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.GP_LIFE_PERSON_ATT, new JREmptyDataSource());
		jasperPrintList.add(jprint2);
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.GL_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jasperPrintList.add(termAndCJprint);
		return jasperPrintList;
	}

	/** PA Policy Letter */
	public static List<JasperPrint> generatePersonalAccidentPolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		boolean isPAUSD = KeyFactorChecker.isPersonalAccidentUSD(insuredPerson.getProduct());
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		double sumInsured = lifePolicy.getTotalSumInsured();
		double premium = paymentDTO.getTotalPremium();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String sumInsuredInword = convertor.getNameWithDecimal(sumInsured);
		String premiumInword = convertor.getNameWithDecimal(premium);
		boolean isMultiplePerson = false;
		int insuredPersonSize = lifePolicy.getInsuredPersonInfo().size();
		if (insuredPersonSize > 1) {
			isMultiplePerson = true;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insuredName", insuredPerson.getFullName() + (isMultiplePerson ? " + " + (insuredPersonSize - 1) : ""));
		paramMap.put("insuredNrc", insuredPerson.getIdNo());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress().getFullResidentAddress());
		paramMap.put("occupation", insuredPerson.getOccupationName());
		paramMap.put("phno", insuredPerson.getPhoneForView());
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("premium", Utils.formattedCurrency(premium));
		paramMap.put("sumInsuredInword", sumInsuredInword);
		paramMap.put("premiumInword", premiumInword);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNoForView());
		paramMap.put("organizationName", lifePolicy.getOrganization() != null ? lifePolicy.getOrganization().getName() : " ");
		paramMap.put("age", beneifitPerson.getAge());
		paramMap.put("dateOfBirth", beneifitPerson.getDateOfBirthForView());
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		paramMap.put("benefAddress", beneifitPerson.getFullAddress());
		paramMap.put("benifiPhon", beneifitPerson.getPhoneForView());
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		String policyJasperPath = isPAUSD ? JasperTemplate.PA_POLICY_ENG : JasperTemplate.PA_POLICY;
		String personAttJasperPath = isPAUSD ? JasperTemplate.PA_POLICY_PERSON_ATT_ENG : JasperTemplate.PA_POLICY_PERSON_ATT;
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, policyJasperPath, new JREmptyDataSource());
		jasperPrintList.add(jprint);
		if (isMultiplePerson) {
			Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
			insuredPesronMap_2.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			insuredPesronMap_2.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
				perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
			}
			insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, personAttJasperPath, new JREmptyDataSource());
			jasperPrintList.add(jprint2);
		}
		if (isPAUSD) {
			JasperPrint termAndCEngJprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_TERM_CONDITION_ENG, new JREmptyDataSource());
			JasperPrint termAndCEngJprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_TERM_CONDITION_ENG1, new JREmptyDataSource());
			Utils.removeBlankPages(termAndCEngJprint);
			Utils.removeBlankPages(termAndCEngJprint1);
			jasperPrintList.add(termAndCEngJprint);
			jasperPrintList.add(termAndCEngJprint1);
		} else {
			JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.PA_TERM_CONDITION, new JREmptyDataSource());
			JasperPrint termAndCJprint1 = JasperFactory.generateJasperPrint(null, JasperTemplate.PA_TERM_CONDITION1, new JREmptyDataSource());
			Utils.removeBlankPages(termAndCJprint);
			Utils.removeBlankPages(termAndCJprint1);
			jasperPrintList.add(termAndCJprint);
			jasperPrintList.add(termAndCJprint1);
		}
		return jasperPrintList;
	}

	/** Farmer Policy Letter */
	public static List<JasperPrint> generateFarmerPolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", lifePolicy.getActivedPolicyStartDate());
		paramMap.put("insuredName", insuredPerson.getFullName());
		paramMap.put("insuredFatherName", insuredPerson.getFatherName());
		paramMap.put("age", insuredPerson.getAge());
		paramMap.put("insuredNrc", insuredPerson.getIdNo());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress() != null ? insuredPerson.getResidentAddress().getFullResidentAddress() : lifePolicy.getCustomerAddress());
		paramMap.put("dateOfBirth", insuredPerson.getDateOfBirth());
		paramMap.put("phno", insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo());
		paramMap.put("sumInsured", Utils.formattedCurrency(lifePolicy.getTotalSumInsured()));
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNo());
		paramMap.put("benefiAddress", beneifitPerson.getResidentAddress() == null ? " " : beneifitPerson.getResidentAddress().getFullResidentAddress());
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.FARMER_POLICY, new JREmptyDataSource());
		Utils.removeBlankPages(jprint);
		jprintList.add(jprint);
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.FM_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jprintList.add(termAndCJprint);
		return jprintList;
	}

	/** SnakeBike Policy Letter */
	public static List<JasperPrint> generateSnakeBitePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		double sumInsured = lifePolicy.getLifeProposal().getSuminsuredPerUnit();
		double premium = lifePolicy.getTotalPremium();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String sumInsuredInword = convertor.getNameWithDecimal(sumInsured);
		String premiumInword = convertor.getNameWithDecimal(premium);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insuredName", insuredPerson.getFullName());
		paramMap.put("fatherName", insuredPerson.getFatherName());
		paramMap.put("insuredNrc", insuredPerson.getIdNo());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress() != null ? insuredPerson.getResidentAddress().getFullResidentAddress() : lifePolicy.getCustomerAddress());
		paramMap.put("dateOfBirth", insuredPerson.getDateOfBirth());
		String phno = insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo();
		paramMap.put("phno", phno);
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("premium", Utils.formattedCurrency(premium));
		paramMap.put("sumInsuredInword", sumInsuredInword);
		paramMap.put("premiumInword", premiumInword);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNoForView());
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		paramMap.put("benifiPhon", phno);
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SNAKEBITE_POLICY, new JREmptyDataSource());
		jprintList.add(jprint);
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.SNB_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jprintList.add(termAndCJprint);
		return jprintList;
	}

	/** SportMan Policy Letter */
	public static List<JasperPrint> generateSportManPolicyLetter(LifePolicy lifePolicy) {
		List<PolicyInsuredPerson> personList = lifePolicy.getPolicyInsuredPersonList();
		int insuredPersonSize = personList.size();
		boolean isMultiple = insuredPersonSize > 1 ? true : false;
		String startDate = Utils.formattedDate(lifePolicy.getActivedPolicyStartDate());
		String endDate = Utils.formattedDate(lifePolicy.getActivedPolicyEndDate());
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		double sumInsured = lifePolicy.getTotalSumInsured();
		double premium = lifePolicy.getTotalPremium();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String sumInsuredInword = sumInsured > 0.00 ? convertor.getNameWithDecimal(sumInsured) : "0.00";
		String premiumInword = convertor.getNameWithDecimal(premium);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("insuredName", insuredPerson.getFullName() + (isMultiple ? " + " + (insuredPersonSize - 1) : ""));
		paramMap.put("insuredNrc", insuredPerson.getIdNoForView());
		paramMap.put("typeOfSport", insuredPerson.getTypesOfSport() != null ? insuredPerson.getTypesOfSport().getName() : null);
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress().getFullResidentAddress());
		String phno = insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo();
		paramMap.put("phno", phno);
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("premium", Utils.formattedCurrency(premium));
		paramMap.put("sumInsuredInword", sumInsuredInword);
		paramMap.put("premiumInword", premiumInword);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNoForView());
		paramMap.put("organizationName", lifePolicy.getOrganization() != null ? lifePolicy.getOrganization().getName() : " ");
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		paramMap.put("benifiPhon", beneifitPerson.getPhoneForView());
		paramMap.put("benifiAddress", beneifitPerson.getFullAddress());

		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORT_MAN_POLICY, new JREmptyDataSource());
		jprintList.add(jprint);
		if (isMultiple) {
			Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
			insuredPesronMap_2.put("startDate", startDate);
			insuredPesronMap_2.put("endDate", endDate);
			insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
				perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
			}
			insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATT, new JREmptyDataSource());
			jprintList.add(jprint2);
		}
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.SPTM_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jprintList.add(termAndCJprint);
		return jprintList;
	}

	/** PublicLife Policy Letter */
	public static List<JasperPrint> generatePublicLifePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		List<PolicyInsuredPersonBeneficiaries> beneifitPersonList = insuredPerson.getPolicyInsuredPersonBeneficiariesList();
		String policyNo = lifePolicy.getPolicyNo();
		double sumInsured = lifePolicy.getTotalSumInsured();
		String insuredName = insuredPerson.getFullName();
		int nextYrAge = insuredPerson.getAge();
		String insuredAddress = lifePolicy.getCustomerAddress();
		String occupation = insuredPerson.getOccupation() != null ? insuredPerson.getOccupation().getName() : "";
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		String endDate = Utils.formattedDate(lifePolicy.getActivedPolicyEndDate());
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMapCover = new HashMap<String, Object>();
		paramMapCover.put("policyNo", policyNo);
		paramMapCover.put("customerName", lifePolicy.getCustomerName());
		paramMapCover.put("totalSumInsured", Utils.formattedCurrency(lifePolicy.getSumInsured()));
		JasperPrint jprintCover = JasperFactory.generateJasperPrint(paramMapCover, JasperTemplate.PUBLIC_LIFE_POLICY_COVER, new JREmptyDataSource());
		jasperPrintList.add(jprintCover);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(startDate));
		paramMap.put("insuredName", insuredName);
		paramMap.put("nextYrAge", nextYrAge);
		paramMap.put("insuredAddress", insuredAddress);
		paramMap.put("occupation", occupation);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("sumInsured", sumInsured);
		paramMap.put("paymentType", lifePolicy.getPaymentType().getMonth());
		paramMap.put("termPremium", lifePolicy.getTotalPremium());
		paramMap.put("invoicePremium", lifePolicy.getTotalTermPremium());
		paramMap.put("coverageDateString", lifePolicy.getTimeSlotListStr());
		if (lifePolicy.getPaymentType().getMonth() > 1) {
			paramMap.put("isYearly", true);
		} else {
			paramMap.put("isYearly", false);

		}
		JasperPrint benefitJprint = null;
		int size = beneifitPersonList.size();
		PolicyInsuredPersonBeneficiaries beneifitPerson = null;
		if (size > 2) {
			paramMap.put("benefiName", "AS PER ATTACHMENT");
			paramMap.put("relationship", "AS PER ATTACHMENT");
			Map<String, Object> paramMapBenefit = new HashMap<String, Object>();
			paramMapBenefit.put("policyNo", lifePolicy.getPolicyNo());
			paramMapBenefit.put("idNo", insuredPerson.getIdNo());
			paramMapBenefit.put("insuredPersonName", lifePolicy.getCustomerName());
			paramMapBenefit.put("nrc", insuredPerson.getIdNo());
			paramMapBenefit.put("listDataSource", new JRBeanCollectionDataSource(beneifitPersonList));
			paramMapBenefit.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			benefitJprint = JasperFactory.generateJasperPrint(paramMapBenefit, JasperTemplate.BENEFICIARY_ATTACH, new JREmptyDataSource());
		} else {
			beneifitPerson = beneifitPersonList.get(0);
			paramMap.put("benefiName", beneifitPerson.getFullName());
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
			paramMap.put("percentage", beneifitPerson.getPercentage());
			if (size > 1) {
				PolicyInsuredPersonBeneficiaries beneifitPerson2 = beneifitPersonList.get(1);
				paramMap.put("benefiName2", beneifitPerson2.getFullName());
				paramMap.put("relationship2", beneifitPerson2.getRelationship().getName());
				paramMap.put("percentage2", beneifitPerson2.getPercentage());
			}
		}
		paramMap.put("currencySymbol", "Ks-");
		paramMap.put("endDateString", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate(), "dd-MMMM_yyyy"));
		paramMap.put("premiumEndDate", Utils.formattedDate(lifePolicy.abstractDatyByPaymentType(), "dd-MMMM_yyyy"));
		paramMap.put("endDate", endDate);
		JasperPrint jprintPolicy = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PUBLIC_LIFE_POLICY, new JREmptyDataSource());
		jasperPrintList.add(jprintPolicy);
		if (benefitJprint != null) {
			jasperPrintList.add(benefitJprint);
		}
		Map<String, Object> paramMapSummary = new HashMap<String, Object>();
		paramMapSummary.put("policyNo", policyNo);
		paramMapSummary.put("policyPeriod", lifePolicy.getPeriodOfYears());
		paramMapSummary.put("sumInsured", sumInsured);
		paramMapSummary.put("agentInfo", agentName + " ( " + agentLicenseNo + " )");
		paramMapSummary.put("name", insuredName);
		paramMapSummary.put("occupation", occupation);
		paramMapSummary.put("address", insuredAddress);
		paramMapSummary.put("nextYearAge", nextYrAge);
		paramMapSummary.put("submittedDate", lifePolicy.getLifeProposal().getSubmittedDate());
		paramMapSummary.put("startDate", startDate);
		paramMapSummary.put("premiumRate", null);
		paramMapSummary.put("premium", lifePolicy.getTotalPremium());
		paramMapSummary.put("paymentType", lifePolicy.getPaymentType().getName());
		paramMapSummary.put("paidDate", lifePolicy.getCoverageDate());
		paramMapSummary.put("endDate", endDate);
		paramMapSummary.put("benificaryName", beneifitPerson.getFullName());
		paramMapSummary.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprintPolicySummary = JasperFactory.generateJasperPrint(paramMapSummary, JasperTemplate.PUBLIC_LIFE_POLICY_SUMMARY, new JREmptyDataSource());
		jasperPrintList.add(jprintPolicySummary);
		JasperPrint termAndCJpring = JasperFactory.generateJasperPrint(null, JasperTemplate.PL_TERM_CONDITION, new JREmptyDataSource());
		JasperPrint termAndCJpring1 = JasperFactory.generateJasperPrint(null, JasperTemplate.PL_TERM_CONDITION1, new JREmptyDataSource());
		JasperPrint termAndCJpring2 = JasperFactory.generateJasperPrint(null, JasperTemplate.PL_TERM_CONDITION2, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJpring);
		Utils.removeBlankPages(termAndCJpring1);
		Utils.removeBlankPages(termAndCJpring2);
		jasperPrintList.add(termAndCJpring);
		jasperPrintList.add(termAndCJpring1);
		jasperPrintList.add(termAndCJpring2);
		return jasperPrintList;
	}

	/** Life Bill Collection Noti Letter */
	public static List<JasperPrint> generateLifePolicyNotification(List<LifePolicy> policies, String dirPath, String fileName) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			for (LifePolicy lifePolicy : policies) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				Date activePolicyStartDate = lifePolicy.getActivedPolicyStartDate();
				PolicyInsuredPerson insuredPerson = lifePolicy.getPolicyInsuredPersonList().get(0);
				Date activePolicyEndDate = lifePolicy.getActivedPolicyEndDate();
				int policyPeriodMonths = org.ace.insurance.common.Utils.monthsBetween(activePolicyStartDate, activePolicyEndDate, false, false);
				if (policyPeriodMonths > 12) {
					policyPeriodYears = policyPeriodMonths / 12;
				}
				int month = lifePolicy.getPaymentType().getMonth();
				int remainingTerm = 0;
				int currentTerm = 0;
				if (month > 0) {
					if (policyPeriodMonths / month != lifePolicy.getLastPaymentTerm()) {
						remainingTerm = policyPeriodMonths / month - lifePolicy.getLastPaymentTerm();
						currentTerm = lifePolicy.getLastPaymentTerm() + 1;
					}
				} else {
					currentTerm = lifePolicy.getLastPaymentTerm() + 1;
				}

				Calendar cal = Calendar.getInstance();
				paramMap.put("insuredPersonName", lifePolicy.getInsuredPersonInfo().get(0).getFullName());
				paramMap.put("insuredPersonAddress", lifePolicy.getCustomerAddress());
				paramMap.put("phoneNo", lifePolicy.getCustomerPhoneNo());
				paramMap.put("salePersonName", lifePolicy.getAgentName());
				paramMap.put("sumInsured", lifePolicy.getTotalSumInsuredString());
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("basicTermPremium", lifePolicy.getTotalBasicTermPremiumString());
				paramMap.put("activePolicyStartDate", Utils.formattedDate(activePolicyStartDate));
				paramMap.put("activePolicyEndDate", Utils.formattedDate(activePolicyEndDate));
				paramMap.put("policyLifeTime", policyPeriodYears);
				paramMap.put("month", month);
				paramMap.put("currentTerm", currentTerm);
				paramMap.put("remainingTerm", remainingTerm);
				paramMap.put("currentYear", cal.get(Calendar.YEAR));
				paramMap.put("dueDay", DateUtils.getDayFromDate(lifePolicy.getCoverageDate()));
				paramMap.put("dueMonth", (DateUtils.getMonthFromDate(lifePolicy.getCoverageDate())) + 1);
				paramMap.put("dueYear", DateUtils.getYearFromDate(lifePolicy.getCoverageDate()));
				paramMap.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(new Date()));
				paramMap.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(new Date()));
				paramMap.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(new Date()));
				paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
				paramMap.put("isShortTerm", KeyFactorChecker.isShortTermEndowment(insuredPerson.getProduct().getId()));

				InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_NOTIFICATION_LETTER);
				JasperReport report = JasperCompileManager.compileReport(inputStr);
				JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
				jpList.add(print);
			}
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePolicyNotification", e);
		}
		return jpList;
	}

	/** Life Bill Collection Invoice Letter */
	public static List<JasperPrint> generateLifeBillPaymentInvoice(List<BillCollectionCashReceiptDTO> cashReceiptDTOs) {
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Product product = cashReceiptDTOs.get(0).getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
		String typeOfInsurance = "";
		if (KeyFactorChecker.isPublicLife(product)) {
			typeOfInsurance = "Endowment Life Insurance";
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			typeOfInsurance = "ShortTerm Endowment Life Insurance";
		}
		Agent agent;
		int periodMonths;
		String period1;
		String paymentType;
		String agentRegNo;
		String agentName;
		for (BillCollectionCashReceiptDTO cashReceiptDTO : cashReceiptDTOs) {
			Payment payment = cashReceiptDTO.getPayment();
			LifePolicy policy = cashReceiptDTO.getLifePolicy();
			periodMonths = policy.getPeriodOfYears();
			period1 = periodMonths + " Years";
			paymentType = policy.getPaymentType().getName();
			agent = policy.getAgent();
			agentRegNo = agent == null ? " " : agent.getLiscenseNo();
			agentName = agent == null ? SaleChannelType.WALKIN.getLabel() : agent.getFullName();
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("invoiceDate", Utils.formattedDate(payment.getConfirmDate()));
			paramMap.put("invoiceNo", payment.getInvoiceNo());
			paramMap.put("section", typeOfInsurance);
			paramMap.put("proposalNo", policy.getPolicyNo());
			paramMap.put("duenum", payment.getPaymentTermStrings());
			paramMap.put("period1", period1);
			paramMap.put("period", paymentType);
			paramMap.put("fromDate", Utils.formattedDate(policy.getCoverageDate()));
			paramMap.put("insurdName", policy.getCustomerName());
			paramMap.put("address", policy.getCustomerAddress());
			paramMap.put("agentRegNo", agentRegNo);
			paramMap.put("agentName", agentName);
			paramMap.put("policyType", "New Policy");
			paramMap.put("premium", policy.getTotalPremium());
			paramMap.put("termPremium", payment.getTotalPremium());
			paramMap.put("adjPlus", 0.00);
			paramMap.put("adjMinus", 0.00);
			paramMap.put("discount", payment.getSpecialDiscount());
			paramMap.put("sumInsured", policy.getTotalSumInsured());
			paramMap.put("stampFee", payment.getStampFees());
			paramMap.put("totalPremium", payment.getTotalAmount());
			paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.Life_PAYMENT_INVOICE, new JREmptyDataSource());
			jasperPrintList.add(jprint);
		}
		return jasperPrintList;
	}

	/** Life Bill Collection Receipt Letter */
	public static List<JasperPrint> generateLifeBillCashReceipt(LifePolicy lifePolicy, Payment payment) {
		List<JasperPrint> printList = new ArrayList<>();
		String receiptNo = payment.getReceiptNo();
		Date paymentDate = payment.getPaymentDate();
		String customerName = lifePolicy.getCustomerName();
		String address = lifePolicy.getCustomerAddress();
		String policyNo = lifePolicy.getProposalNo();
		String agentInfo = lifePolicy.getAgentNameNLiscenseNo();
		String proposalType = null;
		double discount = payment.getSpecialDiscount();
		String paymentType = lifePolicy.getPaymentType().getName();
		String period = lifePolicy.getPeriodOfInsurance() + " Years";
		String startDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyStartDate());
		String endDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyEndDate());
		double totalSI = lifePolicy.getTotalSumInsured();
		double premium = payment.getTotalPremium();
		double stampFee = payment.getStampFees();
		double totalPremium = payment.getTotalAmount();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String premiumInword = convertor.getNameWithDecimal(totalPremium);
		boolean isPublicLife = false;
		boolean isShortterm = false;
		Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
		if (KeyFactorChecker.isPublicLife(product))
			isPublicLife = true;
		else if (KeyFactorChecker.isShortTermEndowment(product.getId()))
			isShortterm = true;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isPublicLife", isPublicLife);
		paramMap.put("paymentType", paymentType);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", paymentDate);
		paramMap.put("customerName", customerName);
		paramMap.put("address", address);
		paramMap.put("proposalNo", policyNo);
		paramMap.put("agent", agentInfo);
		paramMap.put("proposalType", proposalType);
		paramMap.put("policyPeriod", period);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("totalSi", totalSI);
		paramMap.put("premium", premium);
		paramMap.put("adjustAmount", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", stampFee);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("premiumInWord", premiumInword);
		paramMap.put("billCollectTerm", payment.getPaymentTermStrings());
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());

		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (isPublicLife || isShortterm)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
			printList.add(jprint);
		}
		return printList;
	}

}
