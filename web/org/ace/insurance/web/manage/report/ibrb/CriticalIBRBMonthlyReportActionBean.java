package org.ace.insurance.web.manage.report.ibrb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.report.common.ReportCriteria;
import org.ace.insurance.report.ibrb.CriticalIBRBMonthlyReport;
import org.ace.insurance.report.ibrb.service.interfaces.IIBRBReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;
import org.ace.java.web.common.BaseBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "CriticalIBRBMonthlyReportActionBean")
public class CriticalIBRBMonthlyReportActionBean extends BaseBean {

	@ManagedProperty(value = "#{IBRBReportService}")
	private IIBRBReportService IBRBReportService;

	public void setIBRBReportService(IIBRBReportService iBRBReportService) {
		IBRBReportService = iBRBReportService;
	}

	private User user;
	private ReportCriteria criteria;
	private List<CriticalIBRBMonthlyReport> criticalIllnessIBRBReportList;
	private boolean isCritical1;

	@PostConstruct
	private void init() {
		criticalIllnessIBRBReportList = new ArrayList<>();
		user = (User) getParam("LoginUser");
		resetCriteria();
	}

	public void filter() {
		criticalIllnessIBRBReportList = IBRBReportService.findCriticalIBRBMonthlyReports(criteria);
	}

	public void resetCriteria() {
		Date today = new Date();
		criteria = new ReportCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		int month = DateUtils.getMonthFromDate(today) - 1;
		int year = DateUtils.getYearFromDate(today);
		criteria.setStartDate(Utils.getStartDate(year, month));
		criteria.setEndDate(Utils.getEndDate(year, month));
		criteria.setSalePointName(null);
	}

	public void exportExcel(boolean isH1) {
		isCritical1 = isH1;
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String fileName = null;
		if (isCritical1) {
			fileName = "Critical IBRB Monthly.xlsx";
		} else {
			fileName = "Critical Monthly.xlsx";
		}
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try (OutputStream op = ec.getResponseOutputStream();) {
			ExportExcel criticalIBRBMonthlyExcel = new ExportExcel(criticalIllnessIBRBReportList);
			if (isCritical1) {
				criticalIBRBMonthlyExcel.generate(op);
			} else {
				criticalIBRBMonthlyExcel.generate2(op);
			}
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, isCritical1 ? "Failed to export Critical Monthly.xlsx" : "Failed to export Critical Monthly 2.xlsx", e);
		}
	}

	private class ExportExcel {
		private XSSFWorkbook wb;
		List<CriticalIBRBMonthlyReport> criticalIllnessIBRBReportList;

		public ExportExcel(List<CriticalIBRBMonthlyReport> criticalIllnessIBRBReportList) {
			this.criticalIllnessIBRBReportList = criticalIllnessIBRBReportList;
			load();
		}

		private void load() {
			try (InputStream inp = isCritical1 ? this.getClass().getResourceAsStream("/report-template/ibrb/Critical IBRB Monthly.xlsx")
					: this.getClass().getResourceAsStream("/report-template/ibrb/Critical IBRB Monthly2.xlsx");) {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR,
						isCritical1 ? "Failed to load Critical Illness IBRB Monthly.xlsx template" : "Failed to load Critical Illness IBRB Monthly2.xlsx template", e);
			}
		}

		private XSSFCellStyle getTitleCell() {
			XSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellStyle.setWrapText(true);
			Font font = wb.createFont();
			font.setFontName("Myanmar3");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			return cellStyle;
		}

		private XSSFCellStyle getTitleMonthCell() {
			XSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellStyle.setWrapText(true);
			Font font = wb.createFont();
			font.setFontName("Myanmar3");
			font.setFontHeightInPoints((short) 11);
			cellStyle.setFont(font);
			return cellStyle;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet1 = wb.getSheet("Critical Illness");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				String companyLabel = ApplicationSetting.getCompanyLabel();
				String year = DateUtils.getYearFromDate(criteria.getStartDate()) + "";
				String month = Utils.getMonthString(DateUtils.getMonthFromDate(criteria.getStartDate()));

				sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 19));
				row = sheet1.createRow(0);
				cell = row.createCell(0);
				cell.setCellValue(companyLabel);
				cell.setCellStyle(getTitleCell());

				row = sheet1.getRow(2);
				cell = row.createCell(0);
				cell.setCellValue(year);
				cell.setCellStyle(getTitleCell());

				cell = row.createCell(3);
				cell.setCellValue(month);
				cell.setCellStyle(getTitleMonthCell());

				sheet1.addMergedRegion(new CellRangeAddress(3, 3, 5, 19));
				row = sheet1.getRow(3);
				cell = row.createCell(5);
				cell.setCellValue(DateUtils.getDateFormatString(new Date()));
				cell.setCellStyle(getTitleCell());

				int i = 7;
				int index = 0;
				String premiumFormula = "";

				for (CriticalIBRBMonthlyReport criticalIllnessIBRBReport : criticalIllnessIBRBReportList) {
					i = i + 1;
					index = index + 1;
					row = sheet1.createRow(i);
					// index
					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					Date policyStartDate = criticalIllnessIBRBReport.getActivedPolicyStartDate();

					// policy start date day
					cell = row.createCell(1);
					cell.setCellValue(DateUtils.getDayFromDate(policyStartDate));
					cell.setCellStyle(defaultCellStyle);

					// policy start date month
					cell = row.createCell(2);
					cell.setCellValue(DateUtils.getMonthFromDate(policyStartDate) + 1);
					cell.setCellStyle(defaultCellStyle);

					// policy start date year
					cell = row.createCell(3);
					cell.setCellValue(DateUtils.getYearFromDate(policyStartDate));
					cell.setCellStyle(defaultCellStyle);

					// policyNo
					cell = row.createCell(4);
					cell.setCellValue(criticalIllnessIBRBReport.getPolicyNo());
					cell.setCellStyle(textCellStyle);

					// gender
					cell = row.createCell(5);
					cell.setCellValue(criticalIllnessIBRBReport.getGender());
					cell.setCellStyle(textCellStyle);

					// age
					cell = row.createCell(6);
					cell.setCellValue(criticalIllnessIBRBReport.getAge());
					cell.setCellStyle(textCellStyle);

					// occupation
					cell = row.createCell(7);
					cell.setCellValue(criticalIllnessIBRBReport.getOccupation());
					cell.setCellStyle(textCellStyle);

					// address
					cell = row.createCell(8);
					cell.setCellValue(criticalIllnessIBRBReport.getResidentAddress());
					cell.setCellStyle(textCellStyle);

					// province
					cell = row.createCell(9);
					cell.setCellValue(criticalIllnessIBRBReport.getProvince());
					cell.setCellStyle(textCellStyle);

					// township
					cell = row.createCell(10);
					cell.setCellValue(criticalIllnessIBRBReport.getTownship());
					cell.setCellStyle(textCellStyle);

					// paymentType
					cell = row.createCell(11);
					cell.setCellValue(criticalIllnessIBRBReport.getPaymentType());
					cell.setCellStyle(textCellStyle);

					// customerType
					cell = row.createCell(12);
					cell.setCellValue(criticalIllnessIBRBReport.getCustomerType().getLabel());
					cell.setCellStyle(textCellStyle);

					// premium
					cell = row.createCell(13);
					cell.setCellValue(criticalIllnessIBRBReport.getTotalPremium());
					cell.setCellStyle(currencyCellStyle);

					// benef info
					cell = row.createCell(14);
					cell.setCellValue(criticalIllnessIBRBReport.getBenefInfo());
					cell.setCellStyle(textCellStyle);

					// his info
					cell = row.createCell(15);
					cell.setCellValue(criticalIllnessIBRBReport.getHisInfo());
					cell.setCellStyle(textCellStyle);

					// his dis
					cell = row.createCell(16);
					cell.setCellValue(criticalIllnessIBRBReport.getHisDis());
					cell.setCellStyle(textCellStyle);

					// basicUnit
					cell = row.createCell(17);
					cell.setCellValue(criticalIllnessIBRBReport.getBasicUnit());
					cell.setCellStyle(defaultCellStyle);

				}

				i = i + 1;
				sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 12));
				row = sheet1.createRow(i);
				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 12), sheet1, wb);
				cell.setCellValue("Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(13);
				cell.setCellStyle(currencyCellStyle);
				premiumFormula = "SUM(N9:N" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(premiumFormula);

				wb.setPrintArea(0, 0, 17, 0, i);

				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void generate2(OutputStream op) {
			try {
				Sheet sheet1 = wb.getSheet("Critical Illness 2");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				String companyLabel = ApplicationSetting.getCompanyLabel();
				String year = DateUtils.getYearFromDate(criteria.getStartDate()) + "";
				String month = Utils.getMonthString(DateUtils.getMonthFromDate(criteria.getStartDate()));

				sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
				row = sheet1.createRow(0);
				cell = row.createCell(0);
				cell.setCellValue(companyLabel);
				cell.setCellStyle(getTitleCell());

				row = sheet1.getRow(2);
				cell = row.createCell(0);
				cell.setCellValue(year);
				cell.setCellStyle(getTitleCell());

				cell = row.createCell(3);
				cell.setCellValue(month);
				cell.setCellStyle(getTitleMonthCell());

				sheet1.addMergedRegion(new CellRangeAddress(3, 3, 5, 20));
				row = sheet1.getRow(3);
				cell = row.createCell(5);
				cell.setCellValue(DateUtils.getDateFormatString(new Date()));
				cell.setCellStyle(getTitleCell());

				int i = 7;
				int index = 0;
				String premiumFormula = "";

				for (CriticalIBRBMonthlyReport criticalIllnessIBRBReport : criticalIllnessIBRBReportList) {
					i = i + 1;
					index = index + 1;
					row = sheet1.createRow(i);
					// index
					cell = row.createCell(0);
					cell.setCellValue(index);
					cell.setCellStyle(defaultCellStyle);

					Date policyStartDate = criticalIllnessIBRBReport.getActivedPolicyStartDate();

					// policy start date day
					cell = row.createCell(1);
					cell.setCellValue(DateUtils.getDayFromDate(policyStartDate));
					cell.setCellStyle(defaultCellStyle);

					// policy start date month
					cell = row.createCell(2);
					cell.setCellValue(DateUtils.getMonthFromDate(policyStartDate) + 1);
					cell.setCellStyle(defaultCellStyle);

					// policy start date year
					cell = row.createCell(3);
					cell.setCellValue(DateUtils.getYearFromDate(policyStartDate));
					cell.setCellStyle(defaultCellStyle);

					// policyNo
					cell = row.createCell(4);
					cell.setCellValue(criticalIllnessIBRBReport.getPolicyNo());
					cell.setCellStyle(textCellStyle);

					// insuredPersonName
					cell = row.createCell(5);
					cell.setCellValue(criticalIllnessIBRBReport.getInsuredPersonName());
					cell.setCellStyle(textCellStyle);

					// gender
					cell = row.createCell(6);
					cell.setCellValue(criticalIllnessIBRBReport.getGender());
					cell.setCellStyle(textCellStyle);

					// age
					cell = row.createCell(7);
					cell.setCellValue(criticalIllnessIBRBReport.getAge());
					cell.setCellStyle(textCellStyle);

					// occupation
					cell = row.createCell(8);
					cell.setCellValue(criticalIllnessIBRBReport.getOccupation());
					cell.setCellStyle(textCellStyle);

					// address
					cell = row.createCell(9);
					cell.setCellValue(criticalIllnessIBRBReport.getResidentAddress());
					cell.setCellStyle(textCellStyle);

					// province
					cell = row.createCell(10);
					cell.setCellValue(criticalIllnessIBRBReport.getProvince());
					cell.setCellStyle(textCellStyle);

					// township
					cell = row.createCell(11);
					cell.setCellValue(criticalIllnessIBRBReport.getTownship());
					cell.setCellStyle(textCellStyle);

					// paymentType
					cell = row.createCell(12);
					cell.setCellValue(criticalIllnessIBRBReport.getPaymentType());
					cell.setCellStyle(textCellStyle);

					// customerType
					cell = row.createCell(13);
					cell.setCellValue(criticalIllnessIBRBReport.getCustomerType().getLabel());
					cell.setCellStyle(textCellStyle);

					// premium
					cell = row.createCell(14);
					cell.setCellValue(criticalIllnessIBRBReport.getTotalPremium());
					cell.setCellStyle(currencyCellStyle);

					// receiptNo
					cell = row.createCell(15);
					cell.setCellValue(criticalIllnessIBRBReport.getReceiptNo() + " \n [" + Utils.getDateFormatString(criticalIllnessIBRBReport.getPaymentDate()) + "]");
					cell.setCellStyle(currencyCellStyle);

					// benef info
					cell = row.createCell(16);
					cell.setCellValue(criticalIllnessIBRBReport.getBenefInfo());
					cell.setCellStyle(textCellStyle);

					// his info
					cell = row.createCell(17);
					cell.setCellValue(criticalIllnessIBRBReport.getHisInfo());
					cell.setCellStyle(textCellStyle);

					// his dis
					cell = row.createCell(18);
					cell.setCellValue(criticalIllnessIBRBReport.getHisDis());
					cell.setCellStyle(textCellStyle);

					// basicUnit
					cell = row.createCell(19);
					cell.setCellValue(criticalIllnessIBRBReport.getBasicUnit());
					cell.setCellStyle(defaultCellStyle);

				}

				i = i + 1;
				sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 13));
				row = sheet1.createRow(i);
				cell = row.createCell(0);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 13), sheet1, wb);
				cell.setCellValue("Total");
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(14);
				cell.setCellStyle(currencyCellStyle);
				premiumFormula = "SUM(O9:O" + i + ")";
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(premiumFormula);

				wb.setPrintArea(0, 0, 19, 0, i);

				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<CriticalIBRBMonthlyReport> getCriticalIllnessIBRBReportList() {
		return criticalIllnessIBRBReportList;
	}

	public ReportCriteria getCriteria() {
		return criteria;
	}

	public EnumSet<SaleChannelType> getSaleChannelType() {
		EnumSet<SaleChannelType> set = EnumSet.allOf(SaleChannelType.class);
		set.remove(SaleChannelType.AFP);
		set.remove(SaleChannelType.BANK);
		set.remove(SaleChannelType.COINSURANCE_INWARD);
		set.remove(SaleChannelType.REINSURANCE);
		return set;
	}

	public void returnSalesPoints(SelectEvent event) {
		SalesPoints salePoint = (SalesPoints) event.getObject();
		criteria.setSalePointId(salePoint.getId());
		criteria.setSalePointName(salePoint.getName());
	}

	public List<Branch> getBranchList() {
		return user.getAccessBranchList();
	}

}
