package org.ace.insurance.web.common.document.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.life.endorsement.InsuredPersonEndorseStatus;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.factory.PolicyInsuredPersonFactory;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonDTO;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.PolicyInsuredPersonHistory;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.java.component.persistence.BasicDAO;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class LifeEndorsementDocumentBuilder extends BasicDAO {

	/** Group Life Endorse Letter */
	public static List<JasperPrint> EndorseChangesLetters(LifePolicy lifePolicy, LifePolicyHistory policyHistory, LifeEndorseInfo endorseInfo) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		Map<String, Object> insuPerson_Map;
		Map<String, Object> letter_Map;
		List<String> newInsuPersonCodeNoList = new ArrayList<>();
		List<String> replaceInsuPersonCodeNoList = new ArrayList<>();
		List<String> deleteInsuPersonCodeNoList = new ArrayList<>();
		for (LifeEndorseInsuredPerson result : endorseInfo.getLifeEndorseInsuredPersonInfoList()) {
			if (result.getInsuredPersonEndorseStatus().equals(InsuredPersonEndorseStatus.NEW))
				newInsuPersonCodeNoList.add(result.getInsuredPersonCodeNo());
			else if (result.getInsuredPersonEndorseStatus().equals(InsuredPersonEndorseStatus.REPLACE)) {
				replaceInsuPersonCodeNoList.add(result.getInsuredPersonCodeNo());
				deleteInsuPersonCodeNoList.add(result.getInsuredPersonCodeNo());
			}
		}

		// New Insured Person List
		if (newInsuPersonCodeNoList.size() > 0) {
			insuPerson_Map = new HashMap<>();
			List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			// get increased List By InsuredPersonCodeNo & Change to DTO
			for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
				if (newInsuPersonCodeNoList.contains(person.getInsPersonCodeNo()))
					perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
			}
			insuPerson_Map.put("policyNo", lifePolicy.getPolicyNo());
			insuPerson_Map.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
			JasperPrint jprint = JasperFactory.generateJasperPrint(insuPerson_Map, JasperTemplate.GP_LIFE_ENDORSE_PERSON_INCREASE, new JREmptyDataSource());

			int oldNOP = lifePolicy.getPolicyInsuredPersonList().size() - newInsuPersonCodeNoList.size();
			letter_Map = new HashMap<>();
			letter_Map.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			letter_Map.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			letter_Map.put("policyNo", lifePolicy.getPolicyNo());
			letter_Map.put("endorseNo", lifePolicy.getEndorsementNo());
			letter_Map.put("insuredName", lifePolicy.getCustomerName() == null ? lifePolicy.getOrganizationName() : lifePolicy.getCustomerName());
			letter_Map.put("organizationName", lifePolicy.getCustomerName() == null ? lifePolicy.getOrganizationName() : lifePolicy.getCustomerName());
			letter_Map.put("increaseNOP", "(" + newInsuPersonCodeNoList.size() + ")");
			letter_Map.put("oldNOP", "(" + oldNOP + ")");
			letter_Map.put("totalNOP", "(" + lifePolicy.getPolicyInsuredPersonList().size() + ")");
			letter_Map.put("year", DateUtils.getYearWithMyanmarLanguage(lifePolicy.getEndorsementConfirmDate()));
			letter_Map.put("month", DateUtils.getMonthWithMyanmarLanguage(lifePolicy.getEndorsementConfirmDate()));
			letter_Map.put("day", DateUtils.getDayWithMyanmarLanguage(lifePolicy.getEndorsementConfirmDate()));
			jpList.add(jprint);
			JasperPrint jprint1 = JasperFactory.generateJasperPrint(letter_Map, JasperTemplate.GP_LIFE_ENDORSE_PERSON_INCREASE_LETTER, new JREmptyDataSource());
			jpList.add(jprint1);
		}

		// Removed Insured Person List
		if (replaceInsuPersonCodeNoList.size() > 0) {
			insuPerson_Map = new HashMap<>();
			List<PolicyInsuredPersonDTO> replaceDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			List<PolicyInsuredPersonDTO> deletePerDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
				if (replaceInsuPersonCodeNoList.contains(person.getInsPersonCodeNo()))
					replaceDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
			}
			for (PolicyInsuredPersonHistory personHistory : policyHistory.getPolicyInsuredPersonList()) {
				if (deleteInsuPersonCodeNoList.contains(personHistory.getInPersonCodeNo())) {
					deletePerDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(personHistory));
				}
			}
			insuPerson_Map.put("policyNo", lifePolicy.getPolicyNo());
			insuPerson_Map.put("listDataSource", new JRBeanCollectionDataSource(replaceDTOList));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuPerson_Map, JasperTemplate.GP_LIFE_ENDORSE_PERSON_REPLACE, new JREmptyDataSource());
			jpList.add(jprint2);

			letter_Map = new HashMap<>();
			letter_Map.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			letter_Map.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			letter_Map.put("policyNo", lifePolicy.getPolicyNo());
			letter_Map.put("endorseNo", lifePolicy.getEndorsementNo());
			letter_Map.put("endorseDate", Utils.formattedDate(lifePolicy.getEndorsementConfirmDate()));
			letter_Map.put("customerName", lifePolicy.getCustomerName() == null ? lifePolicy.getOrganizationName() : lifePolicy.getCustomerName());
			letter_Map.put("replacePerson", replaceInsuPersonCodeNoList.size() + "");
			letter_Map.put("deletePerson", deleteInsuPersonCodeNoList.size() + "");
			letter_Map.put("totalPerson", lifePolicy.getPolicyInsuredPersonList().size() + "");
			letter_Map.put("year", DateUtils.getYearWithMyanmarLanguage(lifePolicy.getEndorsementConfirmDate()));
			letter_Map.put("month", DateUtils.getMonthWithMyanmarLanguage(lifePolicy.getEndorsementConfirmDate()));
			letter_Map.put("day", DateUtils.getDayWithMyanmarLanguage(lifePolicy.getEndorsementConfirmDate()));
			JasperPrint jprint3 = JasperFactory.generateJasperPrint(letter_Map, JasperTemplate.GP_LIFE_ENDORSE_PERSON_REPLACE_LETTER, new JREmptyDataSource());
			jpList.add(jprint3);

			Map<String, Object> deleteInsuPerson_Map = new HashMap<>();
			deleteInsuPerson_Map.put("policyNo", lifePolicy.getPolicyNo());
			deleteInsuPerson_Map.put("listDataSource", new JRBeanCollectionDataSource(deletePerDTOList));
			JasperPrint jprint4 = JasperFactory.generateJasperPrint(deleteInsuPerson_Map, JasperTemplate.GP_LIFE_ENDORSE_PERSON_DELETE, new JREmptyDataSource());
			jpList.add(jprint4);
		}
		return jpList;
	}

}
