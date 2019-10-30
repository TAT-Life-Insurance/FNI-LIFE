package org.ace.insurance.web.manage.medical.proposal.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.KeyFactorIDConfig;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.PassportType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.proposal.MedicalKeyFactorValue;
import org.ace.insurance.medical.proposal.MedicalPersonHistoryRecord;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.PersonProductHistory;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.addon.AddOn;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.bankBranch.service.interfaces.IBankBranchService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.insurance.system.common.coinsurancecompany.service.interfaces.ICoinsuranceCompanyService;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.service.interfaces.IPaymentTypeService;
import org.ace.insurance.system.common.province.service.interfaces.IProvinceService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.religion.service.interfaces.IReligionService;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.DTOValidator;
import org.ace.insurance.web.common.ErrorMessage;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.ValidationResult;
import org.ace.insurance.web.manage.medical.proposal.CustomerDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProGuardianDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAddOnDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuBeneDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.CustomerFactory;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalInsuredPersonFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "MedicalProposalActionBean")
public class MedicalProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProposalBeneficiariesValidator}")
	private DTOValidator<MedProInsuBeneDTO> beneficiariesValidator;

	public void setBeneficiariesValidator(DTOValidator<MedProInsuBeneDTO> beneficiariesValidator) {
		this.beneficiariesValidator = beneficiariesValidator;
	}

	@ManagedProperty(value = "#{ProposalInsuredPersonValidator}")
	private DTOValidator<MedProInsuDTO> insuredpersonValidator;

	public void setInsuredpersonValidator(DTOValidator<MedProInsuDTO> insuredpersonValidator) {
		this.insuredpersonValidator = insuredpersonValidator;
	}

	@ManagedProperty(value = "#{CustomerValidator}")
	private DTOValidator<CustomerDTO> customerValidator;

	public void setCustomerValidator(DTOValidator<CustomerDTO> customerValidator) {
		this.customerValidator = customerValidator;
	}

	@ManagedProperty(value = "#{GuardianPersonValidator}")
	private DTOValidator<MedProGuardianDTO> guardianValidator;

	public void setGuardianValidator(DTOValidator<MedProGuardianDTO> guardianValidator) {
		this.guardianValidator = guardianValidator;
	}

	@ManagedProperty(value = "#{ReligionService}")
	protected IReligionService religionService;

	public void setReligionService(IReligionService religionService) {
		this.religionService = religionService;
	}

	@ManagedProperty(value = "#{BankBranchService}")
	protected IBankBranchService bankBranchService;

	public void setBankBranchService(IBankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{PaymentTypeService}")
	private IPaymentTypeService paymentTypeService;

	public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{CoinsuranceCompanyService}")
	private ICoinsuranceCompanyService coinsuranceCompanyService;

	public void setCoinsuranceCompanyService(ICoinsuranceCompanyService coinsuranceCompanyService) {
		this.coinsuranceCompanyService = coinsuranceCompanyService;
	}

	@ManagedProperty(value = "#{ProvinceService}")
	private IProvinceService provinceService;

	public void setProvinceService(IProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	private String remark;
	private static final String formID = "medicalProposalEntryForm";

	private boolean createNewAddOn;
	private boolean createEdit;
	private boolean createNewBeneficiariesInfo;
	private boolean createNewIsuredPersonInfo;
	private boolean createNewGuradianPersonInfo;
	private boolean existingCustomer;
	private boolean isEditProposal;
	private boolean surveyAgain;
	private boolean isOldPolicy;
	private boolean isEnquiryEdit;
	private boolean isEditHistoryRecord;
	private boolean isOrganization;

	private String loginBranchId;
	private User user;
	private Product product;
	private User responsiblePerson;
	private MedicalProposal medicalProposal;
	private MedProInsuDTO insuredPersonDTO;
	private MedProInsuBeneDTO beneficiariesInfoDTO;
	private MedicalPersonHistoryRecord historyRecord;
	private MedProGuardianDTO medProGuardianDTO;
	private List<Product> productList;
	private List<Product> selectedProductList;
	private List<Country> countryList;
	private List<Religion> religionList;
	private List<Industry> industryList;
	private List<BankBranch> bankBranchList;
	private List<RelationShip> relationshipList;
	private List<CoinsuranceCompany> companyList;
	private List<String> stateCodeList = new ArrayList<>();
	private List<String> townshipCodeList = new ArrayList<>();
	private Map<String, MedProInsuBeneDTO> beneficiariesInfoDTOMap;
	private Map<String, MedProInsuDTO> insuredPersonDTOMap;
	private Map<String, MedicalPersonHistoryRecord> historyRecordMap;
	private List<MedProInsuAddOnDTO> insuredPersonAddOnList;
	private boolean guardianInfoDisable;
	private boolean sameCustomer;
	private String headerLabel;
	private MedProInsuBeneDTO beneficaryCloneDTO;

	@PreDestroy
	public void destroy() {
		removeParam("medicalProposal");
		removeParam("medicalPolicy");
		removeParam("editMedicalProposal");
		removeParam("enquiryEditMedicalProposal");
		removeParam("healthProposalLabel");
	}

	/************ init ************/
	@PostConstruct
	public void init() {
		isEditHistoryRecord = false;
		guardianInfoDisable = true;
		insuredPersonDTOMap = new HashMap<String, MedProInsuDTO>();
		historyRecord = new MedicalPersonHistoryRecord();
		historyRecordMap = new HashMap<String, MedicalPersonHistoryRecord>();

		initializeInjection();
		loadData();

		createNewMedicalInsuredPerson();
		createNewBeneficiariesInfo();
		createNewGuradianPersonInfo();

		destroy();
	}

	public void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		loginBranchId = user.getLoginBranch().getId();
		headerLabel = (String) getParam("healthProposalLabel");

		/* clone medical policy */
		if (isExistParam("medicalPolicy")) {
			isOldPolicy = true;
			MedicalPolicy medicalPolicy = (MedicalPolicy) getParam("medicalPolicy");
			medicalProposal = new MedicalProposal(medicalPolicy);
			medicalProposal.setSubmittedDate(new Date());
			medicalProposal.setStartDate(new Date());
			loadCustomerIdNo(medicalProposal.getCustomer());
			loadProduct();
		} else if (isExistParam("editMedicalProposal")) {
			isEditProposal = true;
			medicalProposal = (MedicalProposal) getParam("editMedicalProposal");
			loadProduct();
		} else if (isExistParam("enquiryEditMedicalProposal")) {
			isEnquiryEdit = true;
			medicalProposal = (MedicalProposal) getParam("enquiryEditMedicalProposal");
			loadProduct();
		} else {
			createNewMedicalProposal();
			medicalProposal.setBranch(user.getBranch());
		}
		if (isEditProposal || isEnquiryEdit) {
			isOldPolicy = medicalProposal.getOldMedicalPolicy() != null;
			loadCustomerIdNo(medicalProposal.getCustomer());
		}
		initDataList();
		if (medicalProposal.getOrganization() != null)
			isOrganization = true;
	}

	private void initDataList() {
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			MedProInsuDTO personDTO = MedicalProposalInsuredPersonFactory.getMedProInsuDTO(person);
			insuredPersonDTOMap.put(personDTO.getTempId(), personDTO);
		}
	}

	private void loadIdNo(CustomerDTO cusDTO) {
		cusDTO.loadTransientIdNo();
		loadTownshipCodeByStateCode(cusDTO.getStateCode());
	}

	private void loadCustomerIdNo(Customer customer) {
		if (customer != null) {
			customer.loadTransientIdNo();
			loadTownshipCodeByStateCode(customer.getStateCode());
		}
	}

	public void loadProduct() {
		switch (medicalProposal.getHealthType()) {
			case CRITICALILLNESS:
				if (isIndividual()) {
					product = productService.findProductById(KeyFactorIDConfig.getIndividualCriticalIllness_Id());
				} else {
					product = productService.findProductById(KeyFactorIDConfig.getGroupCriticalIllness_Id());
				}
				break;
			case HEALTH:
				if (isIndividual()) {
					product = productService.findProductById(KeyFactorIDConfig.getIndividualHealthInsuranceId());
				} else {
					product = productService.findProductById(KeyFactorIDConfig.getGroupHealthInsuranceId());
				}
				break;
			case MICROHEALTH:
				product = productService.findProductById(KeyFactorIDConfig.getMicroHealthInsurance());
				break;
			default:
				break;
		}
		initAddOnsConfig();
	}

	private void loadData() {
		relationshipList = relationShipService.findAllRelationShip();
		religionList = religionService.findAllReligion();
		bankBranchList = bankBranchService.findAllBankBranch();
		stateCodeList = provinceService.findAllProvinceNo();
		companyList = coinsuranceCompanyService.findAll();
		productList = productService.findProductByInsuranceType(InsuranceType.HEALTH);
	}

	/************ init end ************/

	/************ UI event ************/
	public void changeStateCode(AjaxBehaviorEvent e) {
		String stateCode = (String) ((UIOutput) e.getSource()).getValue();
		loadTownshipCodeByStateCode(stateCode);
	}

	/************ UI event end ************/

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("InsuredPersonInfo".equals(event.getOldStep()) && "customerTab".equals(event.getNewStep())) {
			if (medicalProposal.getCustomer() != null) {
				loadCustomerIdNo(medicalProposal.getCustomer());
			}
		}
		if ("InsuredPersonInfo".equals(event.getOldStep()) && "PremiumDetails".equals(event.getNewStep())) {
			if (insuredPersonDTOMap.size() < 1) {
				addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.REQUIRE_INSUREDPERSON);
				valid = false;
			}
			if (valid) {
				try {
					loadAllKeyFactorValue();
					medicalProposal.setMedicalProposalInsuredPersonList(getProposalInsuredPersonList());
					medicalProposal = medicalProposalService.calculatePremium(medicalProposal);
				} catch (SystemException ex) {
					valid = false;
					RequestContext.getCurrentInstance().update(formID + ":growl");
					handelSysException(ex);
				}
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	private List<MedicalProposalInsuredPerson> getProposalInsuredPersonList() {
		List<MedicalProposalInsuredPerson> insuredPersonList = new ArrayList<MedicalProposalInsuredPerson>();
		for (MedProInsuDTO dto : getMedicalInsuredPersonDTOList()) {
			insuredPersonList.add(MedicalProposalInsuredPersonFactory.getProposalInsuredPerson(dto));
		}
		return insuredPersonList;
	}

	private void loadAllKeyFactorValue() {
		for (MedProInsuDTO inPerson : getMedicalInsuredPersonDTOList()) {
			/* Entry of MedicalKeyFactorValue data */
			loadKeyFactorValueList(inPerson);
		}
	}

	/********** proposal info **************/

	public void createNewMedicalProposal() {
		medicalProposal = new MedicalProposal();
		medicalProposal.setSubmittedDate(new Date());
		medicalProposal.setStartDate(new Date());
		medicalProposal.setCustomerType(CustomerType.INDIVIDUALCUSTOMER);
		medicalProposal.setPeriodMonth(12);
		resetCustomer();
	}

	public void changeCustomerType(AjaxBehaviorEvent event) {
		medicalProposal.setPaymentType(null);
		insuredPersonDTOMap.clear();
		loadProduct();
		createNewMedicalInsuredPerson();
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		medicalProposal.setAgent(null);
	}

	/********** proposal info end **************/

	/********** customer info **************/

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		medicalProposal.setCustomer(customer);
		loadCustomerIdNo(medicalProposal.getCustomer());
		/* reset relationship of insured person */
		for (MedProInsuDTO insuDTO : getMedicalInsuredPersonDTOList()) {
			if (medicalProposal.getCustomer().getId().equals(insuDTO.getCustomer().getId())) {
				insuDTO.setRelationship(getSelfRelationship());
			} else if (getSelfRelationship().equals(insuDTO.getRelationship())) {
				insuDTO.setRelationship(null);
			}
		}
		createNewMedicalInsuredPerson();
		createEdit = false;
	}

	public void resetCustomer() {
		Customer customer = new Customer();
		customer.setGender(Gender.MALE);
		customer.setIdType(IdType.NRCNO);
		customer.setMaritalStatus(MaritalStatus.SINGLE);
		customer.setPassportType(PassportType.WORKING);
		medicalProposal.setCustomer(customer);
		existingCustomer = false;
	}

	public void changeIdType(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		resetIdNoInfo(idType, "C");
	}

	/********** customer info end **************/

	/********** insured person info **************/
	private void createNewMedicalInsuredPerson() {
		createNewIsuredPersonInfo = true;
		guardianInfoDisable = true;
		createEdit = false;
		createNewInsuredPerson();
		createNewBeneficiariesInfoDTOMap();
	}

	private void initAddOnsConfig() {
		insuredPersonAddOnList = new ArrayList<>();
		MedProInsuAddOnDTO medAddOnDTO;
		if (product != null)
			for (AddOn addOn : product.getAddOnList()) {
				medAddOnDTO = new MedProInsuAddOnDTO(addOn);
				medAddOnDTO.setKeyFactorValueList(createNewKeyFactorValueList(addOn));
				insuredPersonAddOnList.add(medAddOnDTO);
			}
	}

	/* create KeyfactorValue of addOn */
	private List<MedicalKeyFactorValue> createNewKeyFactorValueList(AddOn addOn) {
		List<MedicalKeyFactorValue> addOnKeyFactorValueList = new ArrayList<MedicalKeyFactorValue>();
		MedicalKeyFactorValue fkv;
		for (KeyFactor kf : addOn.getKeyFactorList()) {
			fkv = new MedicalKeyFactorValue(kf);
			addOnKeyFactorValueList.add(fkv);
		}
		return addOnKeyFactorValueList;
	}

	public void returnInsuredPersonCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		boolean isAlreadyHave = false;
		for (MedProInsuDTO insuDTO : getMedicalInsuredPersonDTOList()) {
			if (customer.getId().equals(insuDTO.getCustomer().getId())) {
				addErrorMessage(formID + ":customerRegInsu", MessageId.ALREADY_ADD_INSUREDPERSON);
				isAlreadyHave = true;
			}
		}
		if (!isAlreadyHave) {
			if (medicalProposal.getCustomer() != null && customer.getId().equals(medicalProposal.getCustomer().getId())) {
				changeCusToInsuredPerson();
			} else {
				changeSelectedCusToInsuredPerson(CustomerFactory.getCustomerDTO(customer));
			}
			loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		}
		handleInsuredPersonAge();
	}

	public void handleInsuredPersonAge() {
		guardianInfoDisable = true;
		Date dateOfBirth = insuredPersonDTO.getCustomer().getDateOfBirth();
		Date startDate = medicalProposal.getStartDate();
		if (dateOfBirth != null && Utils.getAgeForNextYear(dateOfBirth, startDate) < 18) {
			guardianInfoDisable = false;
		} else {
			insuredPersonDTO.setGuardianDTO(null);
		}
	}

	public void resetInsuredPerson() {
		guardianInfoDisable = true;
		String tempId = insuredPersonDTO.getTempId();
		createNewInsuredPerson();
		insuredPersonDTO.setRelationship(null);
		if (createEdit) {
			insuredPersonDTO.setTempId(tempId);
		}
	}

	private void createNewInsuredPerson() {
		insuredPersonDTO = new MedProInsuDTO();
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setGender(Gender.MALE);
		customerDTO.setIdType(IdType.NRCNO);
		customerDTO.setMaritalStatus(MaritalStatus.SINGLE);
		customerDTO.setpType(PassportType.WORKING);
		insuredPersonDTO.setCustomer(customerDTO);
		initAddOnsConfig();
	}

	public void handleInsuredPersonRelationship() {
		if (insuredPersonDTO.getRelationship() != null) {
			boolean isContainSelfRS = false;
			if (getSelfRelationship().equals(insuredPersonDTO.getRelationship())) {
				for (MedProInsuDTO dto : getMedicalInsuredPersonDTOList()) {
					if (getSelfRelationship().equals(dto.getRelationship())) {
						isContainSelfRS = true;
						break;
					}
				}
				if (isContainSelfRS) {
					insuredPersonDTO.setRelationship(null);
					addErrorMessage(formID + ":beneficiaryRelationShip", MessageId.ALREADY_ADD_SELF_RELATION);
				} else {
					changeCusToInsuredPerson();
				}
				PrimeFaces.current().ajax().update(formID + ":insuredPersonInfoWizardPanel");
			} else {
				insuredPersonDTO.setSameInsuredPerson(false);
			}
		}
	}

	private void changeSelectedCusToInsuredPerson(CustomerDTO customer) {
		insuredPersonDTO.setRelationship(null);
		insuredPersonDTO.setSameInsuredPerson(false);
		loadIdNo(customer);
		insuredPersonDTO.setCustomer(customer);
	}

	private void changeCusToInsuredPerson() {
		Customer customer = medicalProposal.getCustomer();
		insuredPersonDTO.setRelationship(getSelfRelationship());
		insuredPersonDTO.setSameInsuredPerson(true);
		loadCustomerIdNo(customer);
		insuredPersonDTO.setCustomer(CustomerFactory.getCustomerDTO(customer));
	}

	public void prepareAddNewInsuredPersonInfo() {
		createNewMedicalInsuredPerson();
		createNewBeneficiariesInfoDTOMap();
	}

	public void saveMedicalInsuredPerson() {
		for (MedProInsuBeneDTO beneInfoDTO : beneficiariesInfoDTOMap.values()) {
			beneInfoDTO.setIdNo(beneInfoDTO.setFullIdNo());
		}
		insuredPersonDTO.setInsuredPersonBeneficiariesList(new ArrayList<MedProInsuBeneDTO>(sortByValue(beneficiariesInfoDTOMap).values()));
		insuredPersonDTO.setProduct(product);
		insuredPersonDTO.setInsuredPersonBeneficiariesList(getProposalInsuredPersonBeneficiariesList());
		insuredPersonDTO.setInsuredPersonAddOnList(getSelectedAddOnDTOList());
		ValidationResult result = insuredpersonValidator.validate(insuredPersonDTO);
		if (result.isVerified()) {
			beneficaryCloneDTO = getProposalInsuredPersonBeneficiariesList().get(0);
			Date dateOfBirth = insuredPersonDTO.getCustomer().getDateOfBirth();
			Date startDate = medicalProposal.getStartDate();
			insuredPersonDTO.setAge(Utils.getAgeForNextYear(dateOfBirth, startDate));
			insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			createNewMedicalInsuredPerson();
		} else if (!result.isVerified()) {
			for (ErrorMessage message : result.getErrorMeesages()) {
				addErrorMessage(message);
			}
		}
	}

	private List<MedProInsuAddOnDTO> getSelectedAddOnDTOList() {
		List<MedProInsuAddOnDTO> selectFireAddOnList = new ArrayList<MedProInsuAddOnDTO>();
		for (MedProInsuAddOnDTO mAddOn : insuredPersonAddOnList) {
			if (mAddOn.isInclude()) {
				selectFireAddOnList.add(mAddOn);
			}
		}
		return selectFireAddOnList;
	}

	/* set keyfactorValue of product and addOn */
	private void loadKeyFactorValueList(MedProInsuDTO insuredPersonDTO) {
		Date dateOfBirth = insuredPersonDTO.getCustomer().getDateOfBirth();
		Date startDate = medicalProposal.getStartDate();
		int age = Utils.getAgeForNextYear(dateOfBirth, startDate);
		insuredPersonDTO.setAge(age);
		Gender gender = insuredPersonDTO.getCustomer().getGender();
		PaymentType paymentType = medicalProposal.getPaymentType();
		for (MedicalKeyFactorValue kfv : insuredPersonDTO.getKeyFactorValueList()) {
			if (KeyFactorChecker.isGender(kfv.getKeyFactor())) {
				kfv.setValue(gender + "");
			} else if (KeyFactorChecker.isMedicalAge(kfv.getKeyFactor())) {
				kfv.setValue(age + "");
			} else if (KeyFactorChecker.isPaymentType(kfv.getKeyFactor())) {
				kfv.setValue(paymentType.getId());
			}
		}
		for (MedProInsuAddOnDTO mAddon : insuredPersonDTO.getInsuredPersonAddOnList()) {
			for (MedicalKeyFactorValue akv : mAddon.getKeyFactorValueList()) {
				if (KeyFactorChecker.isGender(akv.getKeyFactor())) {
					akv.setValue(gender + "");
				} else if (KeyFactorChecker.isMedicalAge(akv.getKeyFactor())) {
					akv.setValue(age + "");
				} else if (KeyFactorChecker.isPaymentType(akv.getKeyFactor())) {
					akv.setValue(paymentType.getId());
				}
			}
		}
	}

	public void prepareEditInsuredPersonInfo(MedProInsuDTO insuredPersonInfo) {
		this.createNewIsuredPersonInfo = false;
		this.createEdit = true;
		this.insuredPersonDTO = new MedProInsuDTO(insuredPersonInfo);
		prepareEidtAddOnList();
		if (insuredPersonDTO.getInsuredPersonBeneficiariesList() != null) {
			createNewBeneficiariesInfoDTOMap();
			for (MedProInsuBeneDTO bdto : insuredPersonDTO.getInsuredPersonBeneficiariesList()) {
				beneficiariesInfoDTOMap.put(bdto.getTempId(), bdto);
			}
		}
		if (insuredPersonDTO.getGuardianDTO() != null) {
			guardianInfoDisable = false;
		}
		loadIdNo(insuredPersonDTO.getCustomer());
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
	}

	private void prepareEidtAddOnList() {
		insuredPersonAddOnList = new ArrayList<MedProInsuAddOnDTO>();
		MedProInsuAddOnDTO medicalAddOn;
		for (AddOn addOn : product.getSortedAddOnList()) {
			medicalAddOn = new MedProInsuAddOnDTO(addOn);
			for (MedProInsuAddOnDTO dto : insuredPersonDTO.getInsuredPersonAddOnList()) {
				if (dto.getAddOn().equals(addOn)) {
					medicalAddOn.setKeyFactorValueList(dto.getKeyFactorValueList());
					medicalAddOn.setUnit(dto.getUnit());
					medicalAddOn.setExistsEntity(dto.isExistsEntity());
					medicalAddOn.setId(dto.getId());
					medicalAddOn.setVersion(dto.getVersion());
					medicalAddOn.setInclude(true);
					break;
				}
			}
			if (!medicalAddOn.isInclude())
				medicalAddOn.setKeyFactorValueList(createNewKeyFactorValueList(addOn));
			insuredPersonAddOnList.add(medicalAddOn);
		}
	}

	public void removeInsuredPersonInfo(MedProInsuDTO insuredPersonInfo) {
		insuredPersonDTOMap.remove(insuredPersonInfo.getTempId());
		createNewGuradianPersonInfo();
		createNewMedicalInsuredPerson();
	}

	/********** insured person info end **************/

	/************ guardian info **************/
	private void createNewGuradianPersonInfo() {
		medProGuardianDTO = new MedProGuardianDTO();
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setGender(Gender.MALE);
		customerDTO.setIdType(IdType.NRCNO);
		customerDTO.setMaritalStatus(MaritalStatus.SINGLE);
		customerDTO.setpType(PassportType.WORKING);
		medProGuardianDTO.setCustomer(customerDTO);
	}

	public void prepareAddNewGuradianInfo() {
		createNewGuradianPersonInfo();
	}

	public void handleSameCustomer() {
		CustomerDTO cus = medProGuardianDTO.getCustomer();
		if (medicalProposal.getCustomer() != null && medProGuardianDTO.isSameCustomer()) {
			cus = CustomerFactory.getCustomerDTO(medicalProposal.getCustomer());
			cus.loadTransientIdNo();
			medProGuardianDTO.setCustomer(cus);
			loadTownshipCodeByStateCode(cus.getStateCode());
		} else {
			createNewGuradianPersonInfo();
		}
	}

	// TODO test createEdit
	public void resetGuradianPerson() {
		String tempId = medProGuardianDTO.getTempId();
		createNewGuradianPersonInfo();
		medProGuardianDTO.setRelationship(null);
		if (createEdit) {
			medProGuardianDTO.setTempId(tempId);
		}
	}

	public void handleGuardianPersonRelationship() {
		if (medProGuardianDTO.getRelationship() != null) {
			if (getSelfRelationship().equals(medProGuardianDTO.getRelationship())) {
				// TODO THROW ERROR , GUARDIAN CAN"T BE SELF
			}
		}
	}

	public void returnGuradianPersonCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		CustomerDTO custDto = CustomerFactory.getCustomerDTO(customer);
		loadIdNo(custDto);
		medProGuardianDTO.setCustomer(custDto);
		if (medicalProposal.getCustomer() != null && customer.getId().equals(medicalProposal.getCustomer().getId())) {
			medProGuardianDTO.setSameCustomer(true);
		}
	}

	public void saveGuardianInfo() {
		ValidationResult result = guardianValidator.validate(medProGuardianDTO);
		if (result.isVerified()) {
			insuredPersonDTO.setGuardianDTO(medProGuardianDTO);
			createNewGuradianPersonInfo();
			hideGuradianDialog();
		} else if (!result.isVerified()) {
			for (ErrorMessage message : result.getErrorMeesages()) {
				addErrorMessage(message);
			}
		}
	}

	public void hideGuradianDialog() {
		PrimeFaces.current().executeScript("PF('guradianInfoEntryDialog').hide()");
	}

	public void prepareEditGuradianInfo(MedProGuardianDTO guradianInfo) {
		this.medProGuardianDTO = guradianInfo;
		this.createNewGuradianPersonInfo = false;
		loadTownshipCodeByStateCode(guradianInfo.getCustomer().getStateCode());
	}

	public void removeGuradianInfo(MedProGuardianDTO guradianInfo) {
		insuredPersonDTO.setGuardianDTO(null);
		createNewGuradianPersonInfo();
	}

	public List<MedProGuardianDTO> getGuradianPersonList() {
		if (insuredPersonDTO.getGuardianDTO() != null) {
			return Arrays.asList(insuredPersonDTO.getGuardianDTO());
		} else {
			return null;
		}
	}

	/************ guardian info end **************/

	/************ beneficiary info **************/
	private void createNewBeneficiariesInfo() {
		beneficiariesInfoDTO = new MedProInsuBeneDTO();
		beneficiariesInfoDTO.setIdType(IdType.NRCNO);
		beneficiariesInfoDTO.setPercentage(100.0f);
	}

	public void createNewBeneficiariesInfoDTOMap() {
		beneficiariesInfoDTOMap = new HashMap<String, MedProInsuBeneDTO>();
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
		createNewBeneficiariesInfo = true;
		if (beneficaryCloneDTO != null) {
			beneficiariesInfoDTO = new MedProInsuBeneDTO(beneficaryCloneDTO);
			loadTownshipCodeByStateCode(beneficaryCloneDTO.getStateCode());
		}
	}

	public void saveBeneficiariesInfo() {
		ValidationResult result = validBeneficiary(beneficiariesInfoDTO);
		if (result.isVerified()) {
			beneficiariesInfoDTO.setFullIdNo();
			beneficiariesInfoDTOMap.put(beneficiariesInfoDTO.getTempId(), beneficiariesInfoDTO);
			createNewBeneficiariesInfo();
			hideBeneficiaryDialog();
		} else {
			for (ErrorMessage message : result.getErrorMeesages()) {
				addErrorMessage(message);
			}
		}
	}

	public void hideBeneficiaryDialog() {
		loadTownshipCodeByStateCode(insuredPersonDTO.getCustomer().getStateCode());
		PrimeFaces.current().executeScript("PF('beneficiariesInfoEntryDialog').hide()");
	}

	public void prepareEditBeneficiariesInfo(MedProInsuBeneDTO beneficiariesInfo) {
		beneficiariesInfo.loadTransientIdNo();
		this.beneficiariesInfoDTO = beneficiariesInfo;
		createNewBeneficiariesInfo = false;
		loadTownshipCodeByStateCode(beneficiariesInfo.getStateCode());
	}

	public void removeBeneficiariesInfo(MedProInsuBeneDTO beneficiariesInfo) {
		beneficiariesInfoDTOMap.remove(beneficiariesInfo.getTempId());
		createNewBeneficiariesInfo();
	}

	private ValidationResult validBeneficiary(MedProInsuBeneDTO beneficiariesInfo) {
		ValidationResult result = beneficiariesValidator.validate(beneficiariesInfo);
		if (beneficiariesInfoDTOMap.size() > 0) {
			int percentage = 0;
			for (MedProInsuBeneDTO beneficiary : beneficiariesInfoDTOMap.values()) {
				percentage += beneficiary.getPercentage();
			}
			if (!beneficiariesInfoDTOMap.containsKey(beneficiariesInfo.getTempId())) {
				percentage += beneficiariesInfo.getPercentage();
			}
			if (percentage > 100) {
				result.addErrorMessage("beneficiaryInfoEntryForm" + ":percentage", MessageId.OVER_BENEFICIARY_PERCENTAGE);
			}
		}
		return result;
	}

	public List<MedProInsuBeneDTO> getProposalInsuredPersonBeneficiariesList() {
		return new ArrayList<MedProInsuBeneDTO>(sortByValue(beneficiariesInfoDTOMap).values());
	}

	private Map<String, MedProInsuBeneDTO> sortByValue(Map<String, MedProInsuBeneDTO> map) {
		List<Map.Entry<String, MedProInsuBeneDTO>> list = new LinkedList<Map.Entry<String, MedProInsuBeneDTO>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, MedProInsuBeneDTO>>() {
			public int compare(Map.Entry<String, MedProInsuBeneDTO> o1, Map.Entry<String, MedProInsuBeneDTO> o2) {
				try {
					Long l1 = Long.parseLong(o1.getKey());
					Long l2 = Long.parseLong(o2.getKey());
					if (l1 > l2) {
						return 1;
					} else if (l1 < l2) {
						return -1;
					} else {
						return 0;
					}
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		});

		Map<String, MedProInsuBeneDTO> result = new LinkedHashMap<String, MedProInsuBeneDTO>();
		for (Map.Entry<String, MedProInsuBeneDTO> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/************ beneficiary info end **************/

	/************ history record info **/
	public void saveHistoryRecord() {
		for (Product product : selectedProductList) {
			PersonProductHistory productHisotry = new PersonProductHistory();
			productHisotry.setProduct(product);
			historyRecord.addPersonProductHistory(productHisotry);
		}
		historyRecordMap.put(historyRecord.getTempId(), historyRecord);
		selectedProductList.clear();
		newHistoryRecord();
	}

	public void newHistoryRecord() {
		selectedProductList.clear();
		historyRecord = new MedicalPersonHistoryRecord();
	}

	public void addHistoryRecord() {
		if (getHistoryRecordList() == null || getHistoryRecordList().isEmpty()) {
			addErrorMessage("addOnConfigForm1:hitoryRecordTable", MessageId.REQUIRE_HISTORY_RECORD);
		} else {
			insuredPersonDTO.setHistoryRecordList(getHistoryRecordList());
			insuredPersonDTOMap.put(insuredPersonDTO.getTempId(), insuredPersonDTO);
			createNewMedicalInsuredPerson();
			PrimeFaces.current().executeScript("PF('historyRecordDialog').hide();");
		}
	}

	public void handleHistoryRecordDialog() {
		isEditHistoryRecord = false;
		createNewMedicalInsuredPerson();
		PrimeFaces.current().executeScript("PF('historyRecordDialog').hide();");
	}

	public void updateHistoryRecord() {
		for (Product product : selectedProductList) {
			PersonProductHistory productHisotry = new PersonProductHistory();
			productHisotry.setProduct(product);
			historyRecord.addPersonProductHistory(productHisotry);
		}
		historyRecordMap.put(historyRecord.getTempId(), historyRecord);
		newHistoryRecord();
	}

	public void prepareEditHistoryRecord(MedicalPersonHistoryRecord historyRecord) {
		isEditHistoryRecord = true;
		this.historyRecord = historyRecord;
		for (PersonProductHistory productHistory : historyRecord.getProductList()) {
			selectedProductList.add(productHistory.getProduct());
		}
	}

	public void removeHistoryRecord(MedicalPersonHistoryRecord historyRecord) {
		historyRecordMap.remove(historyRecord.getTempId());
		newHistoryRecord();
	}

	public void prepareHistoryRecord(MedProInsuDTO insuredPersonDTO) {
		this.insuredPersonDTO = insuredPersonDTO;
		createEdit = false;
		historyRecordMap = new HashMap<String, MedicalPersonHistoryRecord>();
		if (insuredPersonDTO.getHistoryRecordList() != null) {
			for (MedicalPersonHistoryRecord record : insuredPersonDTO.getHistoryRecordList()) {
				historyRecordMap.put(record.getTempId(), record);
			}
		}
	}

	/************ history record info end **/

	public void checkBeneficiariesIDNo(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		resetIdNoInfo(idType, "B");
	}

	public void changeIdTypeInsuredPerson(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		resetIdNoInfo(idType, "I");
	}

	public void changeIdTypeGuardianPerson(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		resetIdNoInfo(idType, "G");
	}

	public void resetIdNoInfo(IdType idType, String customerType) {
		switch (idType) {
			case STILL_APPLYING:
				if ("C".equals(customerType)) {
					medicalProposal.getCustomer().setFullIdNo(null);
					medicalProposal.getCustomer().setStateCode(null);
					medicalProposal.getCustomer().setTownshipCode(null);
					medicalProposal.getCustomer().setIdConditionType(null);
					medicalProposal.getCustomer().setIdNo("");
				} else if ("I".equals(customerType)) {
					insuredPersonDTO.getCustomer().setFullIdNo(null);
					insuredPersonDTO.getCustomer().setStateCode(null);
					insuredPersonDTO.getCustomer().setTownshipCode(null);
					insuredPersonDTO.getCustomer().setIdConditionType(null);
					insuredPersonDTO.getCustomer().setIdNo("");
				} else if ("B".equals(customerType)) {
					beneficiariesInfoDTO.setFullIdNo(null);
					beneficiariesInfoDTO.setStateCode(null);
					beneficiariesInfoDTO.setTownshipCode(null);
					beneficiariesInfoDTO.setIdConditionType(null);
					beneficiariesInfoDTO.setIdNo("");
				} else if ("G".equals(customerType)) {
					medProGuardianDTO.getCustomer().setFullIdNo(null);
					medProGuardianDTO.getCustomer().setStateCode(null);
					medProGuardianDTO.getCustomer().setTownshipCode(null);
					medProGuardianDTO.getCustomer().setIdConditionType(null);
					medProGuardianDTO.getCustomer().setIdNo("");
				}
				break;
			default:
				break;
		}
	}

	public String addNewMedicalProposal() {
		String result = null;
		try {
			medicalProposal.setProposalType(ProposalType.UNDERWRITING);
			ExternalContext extContext = getFacesContext().getExternalContext();
			ReferenceType referenceType = getReferenceType(medicalProposal.getHealthType());
			WorkflowTask workFlowTask = surveyAgain ? WorkflowTask.SURVEY : WorkflowTask.APPROVAL;

			WorkFlowDTO workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), loginBranchId, remark, workFlowTask, referenceType, TransactionType.UNDERWRITING, user,
					responsiblePerson);
			if (isEditProposal) {
				medicalProposal = medicalProposalService.updateMedicalProposal(medicalProposal, workFlowDTO);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_PROPOSAL_PROCESS_SUCCESS);
			} else if (isEnquiryEdit) {
				medicalProposal = medicalProposalService.updateMedicalProposal(medicalProposal, null);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.EDIT_PROPOSAL_PROCESS_SUCCESS);
			} else {
				if (medicalProposal.getHealthType().equals(HealthType.MICROHEALTH)) {
					workFlowTask = WorkflowTask.APPROVAL;
				} else {
					workFlowTask = WorkflowTask.SURVEY;
				}
				workFlowDTO = new WorkFlowDTO(medicalProposal.getId(), loginBranchId, remark, workFlowTask, referenceType, TransactionType.UNDERWRITING, user, responsiblePerson);
				medicalProposal = medicalProposalService.addNewMedicalProposal(medicalProposal, workFlowDTO);
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			}
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, medicalProposal.getProposalNo());
			createNewMedicalProposal();
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private ReferenceType getReferenceType(HealthType healthType) {
		ReferenceType referenceType = null;
		switch (healthType) {
			case HEALTH:
				referenceType = ReferenceType.HEALTH;
				break;
			case CRITICALILLNESS:
				referenceType = ReferenceType.CRITICAL_ILLNESS;
				break;
			case MICROHEALTH:
				referenceType = ReferenceType.MICRO_HEALTH;
				break;
			default:
				break;
		}
		return referenceType;
	}

	public void selectUser() {
		if (isEditProposal) {
			if (surveyAgain) {
				selectUser(WorkflowTask.SURVEY, WorkFlowType.MEDICAL_INSURANCE, TransactionType.UNDERWRITING, loginBranchId, null);
			} else {
				selectUser(WorkflowTask.APPROVAL, WorkFlowType.MEDICAL_INSURANCE, TransactionType.UNDERWRITING, loginBranchId, null);
			}
		} else {
			if (medicalProposal.getHealthType().equals(HealthType.MICROHEALTH)) {
				selectUser(WorkflowTask.APPROVAL, WorkFlowType.MEDICAL_INSURANCE, TransactionType.UNDERWRITING, loginBranchId, null);
			} else {
				selectUser(WorkflowTask.SURVEY, WorkFlowType.MEDICAL_INSURANCE, TransactionType.UNDERWRITING, loginBranchId, null);

			}
		}
	}

	private void loadTownshipCodeByStateCode(String stateCode) {
		if (stateCode != null)
			townshipCodeList = townshipService.findTspShortNameByProvinceNo(stateCode);
	}

	public boolean isEditProposal() {
		return isEditProposal;
	}

	public boolean isSurveyAgain() {
		return surveyAgain;
	}

	public void setSurveyAgain(boolean surveyAgain) {
		this.surveyAgain = surveyAgain;
	}

	public boolean isOldPolicy() {
		return isOldPolicy;
	}

	public boolean isEnquiryEdit() {
		return isEnquiryEdit;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public boolean isEditHistoryRecord() {
		return isEditHistoryRecord;
	}

	public ArrayList<MedicalPersonHistoryRecord> getHistoryRecordList() {
		return new ArrayList<MedicalPersonHistoryRecord>(historyRecordMap.values());
	}

	public String getPageHeader() {
		return (isEditProposal ? "Confirm Edit" : isEnquiryEdit ? "Enquiry Edit" : isOldPolicy ? "Edit" : "Add New Health") + " " + (isOldPolicy ? "Policy" : "Proposal");
	}

	public MedProGuardianDTO getMedProGuardianDTO() {
		return medProGuardianDTO;
	}

	public boolean getGuardianInfoDisable() {
		return guardianInfoDisable;
	}

	public boolean isSameCustomer() {
		return sameCustomer;
	}

	public void setSameCustomer(boolean sameCustomer) {
		this.sameCustomer = sameCustomer;
	}

	public boolean isDisableInsureInfo() {
		if (HealthType.MICROHEALTH.equals(medicalProposal.getHealthType())) {
			return false;
		} else {
			return isIndividual() && getMedicalInsuredPersonDTOList().size() > 0 && createNewIsuredPersonInfo;
		}
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	/* sort by addOn Name */
	public List<MedProInsuAddOnDTO> getInsuredPersonAddOnList() {
		if (insuredPersonAddOnList != null && !insuredPersonAddOnList.isEmpty()) {
			RegNoSorter<MedProInsuAddOnDTO> sorter = new RegNoSorter<MedProInsuAddOnDTO>(insuredPersonAddOnList);
			return sorter.getSortedList();
		}
		return insuredPersonAddOnList;
	}

	public void setInsuredPersonAddOnList(List<MedProInsuAddOnDTO> insuredPersonAddOnList) {
		this.insuredPersonAddOnList = insuredPersonAddOnList;
	}

	public List<String> getStateCodeList() {
		return stateCodeList;
	}

	public List<String> getTownshipCodeList() {
		return townshipCodeList;
	}

	public String getHeaderLabel() {
		return headerLabel;
	}

	public List<SaleChannelType> getSaleChannel() {
		return Arrays.asList(SaleChannelType.AGENT, SaleChannelType.WALKIN, SaleChannelType.DIRECTMARKETING);
	}

	public List<HealthType> getHealthType() {
		return Arrays.asList(HealthType.HEALTH, HealthType.CRITICALILLNESS, HealthType.MICROHEALTH);
	}

	public List<Product> getSelectedProductList() {
		return selectedProductList;
	}

	public void setSelectedProductList(List<Product> selectedProductList) {
		this.selectedProductList = selectedProductList;
	}

	public MedicalPersonHistoryRecord getHistoryRecord() {
		return historyRecord;
	}

	public void setHistoryRecord(MedicalPersonHistoryRecord historyRecord) {
		this.historyRecord = historyRecord;
	}

	public List<CoinsuranceCompany> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<CoinsuranceCompany> companyList) {
		this.companyList = companyList;
	}

	public boolean isSelfRelation() {
		if (insuredPersonDTO.getRelationship() != null && "SELF".equalsIgnoreCase(insuredPersonDTO.getRelationship().getName()))
			return true;
		else
			return false;
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public IdConditionType[] getIdConditionType() {
		return IdConditionType.values();
	}

	public MaritalStatus[] getMaritalStatus() {
		return MaritalStatus.values();
	}

	public PassportType[] getPassportTypes() {
		return PassportType.values();
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public MedProInsuBeneDTO getBeneficiariesInfoDTO() {
		return beneficiariesInfoDTO;
	}

	public void setBeneficiariesInfoDTO(MedProInsuBeneDTO beneficiariesInfoDTO) {
		this.beneficiariesInfoDTO = beneficiariesInfoDTO;
	}

	public boolean isCreateNewIsuredPersonInfo() {
		return createNewIsuredPersonInfo;
	}

	public void setCreateNewIsuredPersonInfo(boolean createNewIsuredPersonInfo) {
		this.createNewIsuredPersonInfo = createNewIsuredPersonInfo;
	}

	public boolean isCreateNewGuradianPersonInfo() {
		return createNewGuradianPersonInfo;
	}

	public void setCreateNewGuradianPersonInfo(boolean createNewGuradianPersonInfo) {
		this.createNewGuradianPersonInfo = createNewGuradianPersonInfo;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isCreateNewAddOn() {
		return createNewAddOn;
	}

	public void setCreateNewAddOn(boolean createNewAddOn) {
		this.createNewAddOn = createNewAddOn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	private RelationShip getSelfRelationship() {
		RelationShip result = null;
		for (RelationShip rs : relationshipList) {
			if ("SELF".equalsIgnoreCase(rs.getName().trim())) {
				result = rs;
			}
		}
		return result;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public MedProInsuDTO getInsuredPersonDTO() {
		return insuredPersonDTO;
	}

	public void setInsuredPersonDTO(MedProInsuDTO insuredPersonDTO) {
		this.insuredPersonDTO = insuredPersonDTO;
	}

	public void selectPaymentType() {
		selectPaymentType(product);
	}

	public List<MedProInsuDTO> getMedicalInsuredPersonDTOList() {
		return new ArrayList<MedProInsuDTO>(insuredPersonDTOMap.values());
	}

	public CustomerType[] getCustomerTypes() {
		return CustomerType.values();
	}

	public List<Country> getCountryList() {
		return countryList;
	}

	public List<Religion> getReligionList() {
		return religionList;
	}

	public List<Industry> getIndustryList() {
		return industryList;
	}

	public List<BankBranch> getBankBranchList() {
		return bankBranchList;
	}

	public boolean isCreateEdit() {
		return createEdit;
	}

	public void setCreateEdit(boolean createEdit) {
		this.createEdit = createEdit;
	}

	public boolean isExistingCustomer() {
		return existingCustomer;
	}

	public boolean isOrganization() {
		return isOrganization;
	}

	public void changeIsOrganization(AjaxBehaviorEvent e) {
		boolean isOrganization = (boolean) ((UIOutput) e.getSource()).getValue();
		if (isOrganization) {
			medicalProposal.setCustomer(null);
		} else {
			medicalProposal.setOrganization(null);
			resetCustomer();
		}
	}

	public void setOrganization(boolean isOrganization) {
		this.isOrganization = isOrganization;
	}

	private boolean isIndividual() {
		if (CustomerType.INDIVIDUALCUSTOMER.equals(medicalProposal.getCustomerType())) {
			return true;
		}
		return false;
	}

	public MaritalStatus[] getMaritalStatusList() {
		return MaritalStatus.values();
	}

	public List<Branch> getBranchList() {
		return user.getAccessBranchList();
	}

	public void returnCustomerType(SelectEvent event) {
		CustomerType customerType = (CustomerType) event.getObject();
		medicalProposal.setCustomerType(customerType);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		medicalProposal.setOrganization(organization);
	}

	public void returnSalesPoints(SelectEvent event) {
		SalesPoints salesPoints = (SalesPoints) event.getObject();
		medicalProposal.setSalesPoints(salesPoints);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		medicalProposal.setPaymentType(paymentType);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		medicalProposal.setAgent(agent);
	}

	public void returnOccupationForCustomer(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medicalProposal.getCustomer().setOccupation(occupation);
	}

	public void returnNationalityForCustomer(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		medicalProposal.getCustomer().setCountry(nationality);
	}

	public void returnEmployeeOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medicalProposal.getCustomer().setOccupation(occupation);
	}

	public void returnQualification(SelectEvent event) {
		Qualification qualification = (Qualification) event.getObject();
		medicalProposal.getCustomer().setQualification(qualification);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalProposal.getCustomer().getResidentAddress().setTownship(township);
	}

	public void returnPermanentTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalProposal.getCustomer().getPermanentAddress().setTownship(township);
	}

	public void returnOfficeTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		medicalProposal.getCustomer().getOfficeAddress().setTownship(township);
	}

	public void returnInsuredPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		insuredPersonDTO.getCustomer().getResidentAddress().setTownship(townShip);
	}

	public void returnOccupation(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		insuredPersonDTO.getCustomer().setOccupation(occupation);
	}

	public void returnNationalityForInsuredPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		insuredPersonDTO.getCustomer().setCountry(nationality);
	}

	public void returnGuradianPersonTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		medProGuardianDTO.getCustomer().getResidentAddress().setTownship(townShip);
	}

	public void returnOccupationForGuardian(SelectEvent event) {
		Occupation occupation = (Occupation) event.getObject();
		medProGuardianDTO.getCustomer().setOccupation(occupation);
	}

	public void returnNationalityForGuardianPerson(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		medProGuardianDTO.getCustomer().setCountry(nationality);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township townShip = (Township) event.getObject();
		beneficiariesInfoDTO.getResidentAddress().setTownship(townShip);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

}
