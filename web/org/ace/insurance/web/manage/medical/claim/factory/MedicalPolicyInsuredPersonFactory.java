package org.ace.insurance.web.manage.medical.claim.factory;

import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAttachment;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonBeneficiaryDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.CustomerFactory;
import org.ace.insurance.web.manage.medical.proposal.factory.GuardianFactory;

public class MedicalPolicyInsuredPersonFactory {

	public static MedicalPolicyInsuredPerson createMedicalPolicyInsuredPerson(MedicalPolicyInsuredPersonDTO insuredPersonDTO) {
		MedicalPolicyInsuredPerson insuredPerson = new MedicalPolicyInsuredPerson();

		if (insuredPersonDTO.isExitsEntity()) {
			insuredPerson.setId(insuredPersonDTO.getId());
			insuredPerson.setVersion(insuredPersonDTO.getVersion());
		}
		insuredPerson.setDeath(insuredPersonDTO.isDeath());
		insuredPerson.setAge(insuredPersonDTO.getAge());
		insuredPerson.setPremium(insuredPersonDTO.getPremium());
		insuredPerson.setDateOfBirth(insuredPersonDTO.getDateOfBirth());
		insuredPerson.setUnit(insuredPersonDTO.getUnit());
		insuredPerson.setClaimStatus(insuredPersonDTO.getClaimStatus());
		insuredPerson.setProduct(insuredPersonDTO.getProduct());
		insuredPerson.setCustomer(CustomerFactory.getCustomer(insuredPersonDTO.getCustomerDTO()));
		// insuredPerson.setMedicalPolicy(MedicalPolicyFactory.createMedicalPolicy(insuredPersonDTO.getMedicalPolicyDTO()));

		if (insuredPersonDTO.getGuardianDTO() != null) {
			insuredPerson.setGuardian(GuardianFactory.getPolicyGuardian(insuredPersonDTO.getGuardianDTO()));
		}

		if (insuredPersonDTO.getAttachmentList() != null && insuredPersonDTO.getAttachmentList().size() != 0) {
			for (MedicalPolicyInsuredPersonAttachment mpa : insuredPersonDTO.getAttachmentList()) {
				insuredPerson.addAttachment(mpa);
			}
		}

		if (insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList() != null && insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList().size() != 0) {
			for (MedicalPolicyInsuredPersonBeneficiaryDTO insuredPersonBeneficiaryDTO : insuredPersonDTO.getPolicyInsuredPersonBeneficiariesDtoList()) {
				insuredPerson.addInsuredPersonBeneficiaries(MedicalPolicyInsuredPersonBeneficiaryFactory.createMedicalPolicyInsuredPersonBeneficiary(insuredPersonBeneficiaryDTO));
			}
		}

		for (MedicalPolicyInsuredPersonAddOn addOnDTO : insuredPersonDTO.getAddOnList()) {
			insuredPerson.addInsuredPersonAddOn(addOnDTO);
		}
		if (insuredPersonDTO.getRecorder() != null) {
			insuredPerson.setRecorder(insuredPersonDTO.getRecorder());
		}
		return insuredPerson;
	}

	public static MedicalPolicyInsuredPersonDTO createMedicalPolicyInsuredPersonDTO(MedicalPolicyInsuredPerson insuredPerson) {
		MedicalPolicyInsuredPersonDTO insuredPersonDTO = new MedicalPolicyInsuredPersonDTO();

		if (insuredPerson.getId() != null && (!insuredPerson.getId().isEmpty())) {
			insuredPersonDTO.setId(insuredPerson.getId());
			insuredPersonDTO.setVersion(insuredPerson.getVersion());
			insuredPersonDTO.setExitsEntity(true);
		}
		insuredPersonDTO.setDeath(insuredPerson.isDeath());
		insuredPersonDTO.setAge(insuredPerson.getAge());
		insuredPersonDTO.setPremium(insuredPerson.getPremium());
		insuredPersonDTO.setDateOfBirth(insuredPerson.getDateOfBirth());
		insuredPersonDTO.setUnit(insuredPerson.getUnit());
		insuredPersonDTO.setClaimStatus(insuredPerson.getClaimStatus());
		insuredPersonDTO.setProduct(insuredPerson.getProduct());
		insuredPersonDTO.setRelationShip(insuredPerson.getRelationship());
		insuredPersonDTO.setCustomerDTO(CustomerFactory.getCustomerDTO(insuredPerson.getCustomer()));
		// insuredPersonDTO.setMedicalPolicyDTO(MedicalPolicyFactory.createMedicalPolicyDTO(insuredPerson.getMedicalPolicy()));
		if (insuredPerson.getGuardian() != null) {
			insuredPersonDTO.setGuardianDTO(GuardianFactory.getPolicyGuardianDTO(insuredPerson.getGuardian()));
		}

		if (insuredPerson.getAttachmentList() != null && insuredPerson.getAttachmentList().size() != 0) {
			for (MedicalPolicyInsuredPersonAttachment mpa : insuredPerson.getAttachmentList()) {
				insuredPersonDTO.addMedicalPolicyInsuredPersonAttachment(mpa);
			}
		}

		for (MedicalPolicyInsuredPersonAddOn addOnDTO : insuredPerson.getPolicyInsuredPersonAddOnList()) {
			insuredPersonDTO.addAddOn(addOnDTO);
		}

		if (insuredPerson.getPolicyInsuredPersonBeneficiariesList() != null && insuredPerson.getPolicyInsuredPersonBeneficiariesList().size() != 0) {
			for (MedicalPolicyInsuredPersonBeneficiaries insuredPersonBeneficiary : insuredPerson.getPolicyInsuredPersonBeneficiariesList()) {
				insuredPersonDTO.addMedicalPolicyInsuredPersonBeneficiaryDTO(
						MedicalPolicyInsuredPersonBeneficiaryFactory.createMedicalPolicyInsuredPersonBeneficiaryDTO(insuredPersonBeneficiary));
			}
		}
		if (insuredPerson.getRecorder() != null) {
			insuredPersonDTO.setRecorder(insuredPerson.getRecorder());
		}
		return insuredPersonDTO;
	}
}
