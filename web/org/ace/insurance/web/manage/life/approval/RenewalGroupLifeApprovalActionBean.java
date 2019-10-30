package org.ace.insurance.web.manage.life.approval;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.renewal.service.interfaces.IRenewalGroupLifeProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "RenewalGroupLifeApprovalActionBean")
public class RenewalGroupLifeApprovalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RenewalGroupLifeProposalService}")
	private IRenewalGroupLifeProposalService renewalGroupLifeProposalService;

	public void setRenewalGroupLifeProposalService(IRenewalGroupLifeProposalService renewalGroupLifeProposalService) {
		this.renewalGroupLifeProposalService = renewalGroupLifeProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private LifeProposal lifeProposal;
	private ProposalInsuredPerson proposalInsuredPerson;
	private String remark;
	private User responsiblePerson;
	private boolean approved;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void selectUser() {
		selectUser(WorkflowTask.INFORM, WorkFlowType.LIFE, TransactionType.RENEWAL, approved ? user.getLoginBranch().getId() : null,
				approved ? null : user.getLoginBranch().getId());
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeProposal = (lifeProposal == null) ? (LifeProposal) getParam("lifeProposal") : lifeProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		/* redriect */
		double userAuthority = user.getAuthority();
		double sumInsured = lifeProposal.getSumInsured();
		if (userAuthority >= sumInsured) {
			approved = true;
		}
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId());
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public ProposalInsuredPerson getProposalInsuredperson() {
		return proposalInsuredPerson;
	}

	public void setProposalInsuredperson(ProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
	}

	public void prepareApproveInsuredperson(ProposalInsuredPerson proposalInsuredPerson) {
		this.proposalInsuredPerson = proposalInsuredPerson;
	}

	private boolean validFireApproval() {
		boolean valid = true;
		String formID = "lifeApprovalForm";
		if (responsiblePerson == null) {
			addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	public String addNewLifeApproval() {
		String result = null;
		WorkFlowDTO workFlowDTO = null;
		try {
			if (!validFireApproval()) {
				return null;
			}
			// FIXME CHECK REFTYPE
			workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), lifeProposal.getBranch().getId(), remark, WorkflowTask.INFORM, ReferenceType.ENDOWMENT_LIFE,
					TransactionType.RENEWAL, user, responsiblePerson);
			renewalGroupLifeProposalService.approveLifeProposal(lifeProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.APPROVAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void openTemplateDialog() {
		putParam("lifeProposalDetail", lifeProposal);
		putParam("workFlowList", getWorkFlowList());
		openLifeProposalInfoTemplate();
	}

	public String redriectLifeApproval() {
		String result = null;
		try {
			if (!validFireApproval()) {
				return null;
			}
			// FIXME CHECK REFTYPE
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), lifeProposal.getBranch().getId(), remark, WorkflowTask.APPROVAL, ReferenceType.ENDOWMENT_LIFE,
					TransactionType.RENEWAL, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REDIRECT_PROCESS_SUCCESS);
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public boolean isApproved() {
		return approved;
	}

	public void changeBooleanApprove() {
		for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (insuredPerson.isApproved()) {

				insuredPerson.setApprovedSumInsured(insuredPerson.getProposedSumInsured());
			} else {

				insuredPerson.setApprovedSumInsured(0.0);
			}
		}
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}
}
