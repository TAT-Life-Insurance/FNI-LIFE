package org.ace.insurance.web.manage.enquires.life;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.LifePolicyAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonAttachment;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddAttachmentLifeProposalActionBean")
public class AddAttachmentLifeProposalActionBean extends BaseBean {

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private LifeProposal lifeProposal;
	private ProposalInsuredPerson insuredPerson;
	private Map<String, String> proposalUploadedFileMap;
	private Map<String, Map<String, String>> insuredPersonUploadedFileMap;
	private Map<String, String> insuredPersonAttachmentMap;
	private Map<String, ProposalInsuredPerson> insuredPersonMap;
	private String temporyDir;
	private String lifeProposalId;
	private String PROPOSAL_DIR;
	private boolean showEntry;
	private boolean showButton;

	@PostConstruct
	private void init() {
		initialization();
		createNew();
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		PROPOSAL_DIR = "/upload/life-proposal/";
		if (lifeProposal == null)
			createNewLifeProposal();
		lifeProposalId = lifeProposal.getId();
		loadOldAttachments();
	}

	private void createNew() {
		proposalUploadedFileMap = new HashMap<>();
		insuredPersonUploadedFileMap = new HashMap<>();
		insuredPersonAttachmentMap = new HashMap<>();
		insuredPersonMap = new HashMap<>();
	}

	private void loadOldAttachments() {
		String srcPath = getUploadPath() + PROPOSAL_DIR + lifeProposalId;
		String destPath = getUploadPath() + temporyDir + lifeProposalId;
		try {
			FileUtils.copyDirectory(new File(srcPath), new File(destPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String filePath = null;
		for (LifeProposalAttachment proposalAttachment : lifeProposal.getAttachmentList()) {
			filePath = proposalAttachment.getFilePath();
			filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
			proposalAttachment.setFilePath(filePath);
			proposalUploadedFileMap.put(proposalAttachment.getName(), proposalAttachment.getFilePath());
		}
		for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (insuredPerson.getAttachmentList().size() > 0) {
				Map<String, String> insuredPersonAttachmentMap = new HashMap<String, String>();
				for (InsuredPersonAttachment insuredPersonAttach : insuredPerson.getAttachmentList()) {
					filePath = insuredPersonAttach.getFilePath();
					filePath = filePath.replaceAll("/upload/life-proposal/", temporyDir);
					insuredPersonAttach.setFilePath(filePath);
					insuredPersonAttachmentMap.put(insuredPersonAttach.getName(), insuredPersonAttach.getFilePath());
				}
				insuredPersonMap.put(insuredPerson.getId(), insuredPerson);
				insuredPersonUploadedFileMap.put(insuredPerson.getId(), insuredPersonAttachmentMap);
			}
		}
	}

	private void createNewLifeProposal() {
		lifeProposal = new LifeProposal();
	}

	private void initialization() {
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
	}

	public void handleProposalAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		if (!proposalUploadedFileMap.containsKey(fileName)) {
			String filePath = temporyDir + lifeProposalId + "/" + fileName;
			proposalUploadedFileMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	public void handleInsurePersonAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		Map<String, String> insuredPersonAttachmentMap = insuredPersonUploadedFileMap.get(insuredPerson.getId());
		if (insuredPersonAttachmentMap == null)
			insuredPersonAttachmentMap = new HashMap<String, String>();
		if (!insuredPersonAttachmentMap.containsKey(fileName)) {
			String filePath = temporyDir + lifeProposalId + "/" + insuredPerson.getId() + "/" + fileName;
			insuredPersonAttachmentMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
		insuredPersonMap.put(insuredPerson.getId(), insuredPerson);
		insuredPersonUploadedFileMap.put(insuredPerson.getId(), insuredPersonAttachmentMap);
	}

	public void removeProposalUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			proposalUploadedFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (proposalUploadedFileMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeInsuredPersonUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			Map<String, String> insuredPersonAttachmentMap = insuredPersonUploadedFileMap.get(insuredPerson.getId());
			insuredPersonAttachmentMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (insuredPersonAttachmentMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void preparePersonAttachment(ProposalInsuredPerson proposalPerson) {
		this.insuredPerson = proposalPerson;
		showEntry = true;
	}

	private void moveUploadedFiles() {
		try {
			FileHandler.moveFiles(getUploadPath(), temporyDir + lifeProposalId, PROPOSAL_DIR + lifeProposalId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLifeAttachment() {
		try {
			loadAttachment();
			lifeProposalService.updateLifeProposalAttachment(lifeProposal);
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposal.getId());
			if (lifePolicy != null) {
				copyLifePolicyAttachment(lifePolicy);
				lifePolicyService.updateLifePolicyAttachment(lifePolicy);
			}
			moveUploadedFiles();
			this.showButton = true;
			addInfoMessage(null, MessageId.ATTACHMENT_PROCESS_SUCCESS, lifeProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	private void copyLifePolicyAttachment(LifePolicy lifePolicy) {
		lifePolicy.getAttachmentList().clear();
		for (LifeProposalAttachment proposalAttachment : lifeProposal.getAttachmentList()) {
			LifePolicyAttachment policyAttachment = new LifePolicyAttachment(proposalAttachment);
			lifePolicy.addLifePolicyAttachment(policyAttachment);
		}

		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			for (PolicyInsuredPerson policyInsuredPerson : lifePolicy.getPolicyInsuredPersonList()) {
				if (proposalInsuredPerson.getInsPersonCodeNo().equals(policyInsuredPerson.getInsPersonCodeNo())) {
					policyInsuredPerson.getAttachmentList().clear();
					for (InsuredPersonAttachment insuredPersonAttach : proposalInsuredPerson.getAttachmentList()) {
						PolicyInsuredPersonAttachment policyInsuPersonAttach = new PolicyInsuredPersonAttachment(insuredPersonAttach);
						policyInsuredPerson.addAttachment(policyInsuPersonAttach);
					}
					break;
				}
			}
		}
	}

	private void loadAttachment() {
		lifeProposal.getAttachmentList().clear();
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + lifeProposalId + "/" + fileName;
			lifeProposal.addAttachment(new LifeProposalAttachment(fileName, filePath));
		}

		if (insuredPersonUploadedFileMap.keySet() != null) {
			for (String personId : insuredPersonUploadedFileMap.keySet()) {
				Map<String, String> insuredPersonAttachMap = insuredPersonUploadedFileMap.get(personId);
				if (insuredPersonAttachMap != null) {
					insuredPersonMap.get(personId).getAttachmentList().clear();
					for (String fileName : insuredPersonAttachMap.keySet()) {
						String filePath = PROPOSAL_DIR + lifeProposalId + "/" + personId + "/" + fileName;
						insuredPersonMap.get(personId).addAttachment(new InsuredPersonAttachment(fileName, filePath));
					}
				}
			}
		}
	}

	public void openTemplateDialog() {
		putParam("lifeProposalDetail", lifeProposal);
		putParam("workFlowList", getWorkFlowList());
		openLifeProposalInfoTemplate();
	}

	public boolean isEmptyAtt(ProposalInsuredPerson proposalInsuredPerson) {
		String vehId = proposalInsuredPerson.getId();
		Map<String, String> personFileMap = insuredPersonUploadedFileMap.get(vehId);
		if (personFileMap == null || personFileMap.isEmpty()) {
			return true;
		}
		return false;
	}

	public String getPageHeader() {
		return "Life Proposal Attachment";
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public List<String> getInsuredPersonAttachmentList() {
		if (insuredPerson != null) {
			Map<String, String> insuredPersonAttachmentMap = insuredPersonUploadedFileMap.get(insuredPerson.getId());
			if (insuredPersonAttachmentMap == null)
				insuredPersonAttachmentMap = new HashMap<String, String>();
			else
				return new ArrayList<String>(insuredPersonAttachmentMap.values());
		}
		return new ArrayList<String>();
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public boolean isShowEntry() {
		return showEntry;
	}

	public boolean isShowButton() {
		return showButton;
	}

}
