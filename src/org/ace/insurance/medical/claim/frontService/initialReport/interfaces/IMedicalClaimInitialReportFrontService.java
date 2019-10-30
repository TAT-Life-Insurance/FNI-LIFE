package org.ace.insurance.medical.claim.frontService.initialReport.interfaces;

import org.ace.insurance.medical.claim.ClaimStatus;

public interface IMedicalClaimInitialReportFrontService {
//MZP
	// public ClaimInitialReport
	// addNewMedicalClaimInitialRep(MedicalClaimInitialReportDTO
	// medicalClaimInitialReportDTO, List<ICD10> icdList);

	// public ClaimInitialReport
	// updateMedicalClaimInitialRep(MedicalClaimInitialReportDTO
	// medicalClaimInitialReportDTO, List<ICD10> icdList);

	// public void updateMedicalClaimStatus(MedicalClaimInitialReportDTO
	// medicalClaimInitialReportDTO);

	public void updateByInsuredPersonId(String id, ClaimStatus claimStatus);
}
