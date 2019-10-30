package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.LifeDailyIncomeReportDTO;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;

public interface ILifeDailyIncomeReportService {
	public List<LifeDailyIncomeReportDTO> findLifeDailyIncome(LifeDailyIncomeReportCriteria lifeDailyIncomeCriteria);

	public void generateLifeDailyIncomeReport(List<LifeDailyIncomeReportDTO> reportList, String dirPath, String fileName, String branch);
}
