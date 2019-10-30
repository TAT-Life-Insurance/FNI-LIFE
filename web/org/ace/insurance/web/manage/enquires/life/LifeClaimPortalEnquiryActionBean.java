package org.ace.insurance.web.manage.enquires.life;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.LifePolicyCriteriaItems;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.claim.LCL001;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifePolicyCriteria;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeClaimPortalEnquiryActionBean")
public class LifeClaimPortalEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimService}")
	private ILifeClaimService lifeClaimService;

	public void setLifeClaimService(ILifeClaimService lifeClaimService) {
		this.lifeClaimService = lifeClaimService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	private LifeClaim lifeClaim;
	private List<LCL001> lifeClaimList;
	private LCL001 criteria;
	private LifePolicyCriteria lifePolicyCriteria;
	private List<LifePolicySearch> policySearchList;

	@PostConstruct
	public void init() {
		resetCriteria();
	}

	public void reset() {
		// policySearchList.clear();
		lifePolicyCriteria = new LifePolicyCriteria();
		criteria.setPolicyId(null);
		criteria.setPolicyNo(null);
	}

	public void resetCriteria() {
		criteria = new LCL001();
		lifePolicyCriteria = new LifePolicyCriteria();
	}

	public void search() {
		lifeClaimList = lifeClaimService.findLifeClaimByEnquiryCriteria(criteria);
	}

	public String editLifeClaim(LCL001 lifeClaimDto) {
		if (allowToEdit(lifeClaimDto.getId())) {
			lifeClaim = lifeClaimService.findLifeClaimPortalById(lifeClaimDto.getId());
			return lifeClaimDto.getClaimRole().equals("CLAIM") ? "lifeDeathClaim" : (lifeClaimDto.getClaimRole().equals("DISABILITY") ? "lifeDisabilityClaim" : null);
		} else {
			return null;
		}

	}

	private boolean allowToEdit(String id) {
		boolean flag = true;
		String status = lifeClaimService.findStatusById(id);
		if (status != null) {
			if (status.equals(RequestStatus.PENDING.toString())) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable state. It's currently at " + status + " state";
				RequestContext.getCurrentInstance().execute("informationDialog.show()");
			}
		}
		return flag;
	}

	public void searchPolicyCriteria() {
		policySearchList = lifePolicyService.findLifePolicyForClaimByCriteria(lifePolicyCriteria);
	}

	public void resetPolicyCriteria() {
		lifePolicyCriteria.setCriteriaValue(null);
		lifePolicyCriteria.setLifePolicyCriteriaItems(null);
		policySearchList = lifePolicyService.findLifePolicyForClaimByCriteria(null);
	}

	public LifePolicyCriteriaItems[] getLifePolicyCriteriaList() {
		return LifePolicyCriteriaItems.values();
	}

	// Policy No Select Ajax Listener
	public void changePolicyNoValue(LifePolicySearch policy) {
		String formID = "lifeClaimPortalEquiryForm";
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(policy.getPolicyNo());
		if (Utils.isNotNull(lifePolicy)) {
			criteria.setPolicyNo(lifePolicy.getPolicyNo());
			criteria.setPolicyId(lifePolicy.getId());
		} else {
			addErrorMessage(formID + ":policyNo", MessageId.POLICY_NUMBER_NOT_EXIST);
		}
	}

	private String message;

	public String getMessage() {
		return message;
	}

	public LifeClaim getLifeClaim() {
		return lifeClaim;
	}

	public void setLifeClaim(LifeClaim lifeClaim) {
		this.lifeClaim = lifeClaim;
	}

	public List<LCL001> getLifeClaimList() {
		return lifeClaimList;
	}

	public void setLifeClaimList(List<LCL001> lifeClaimList) {
		this.lifeClaimList = lifeClaimList;
	}

	public LCL001 getCriteria() {
		return criteria;
	}

	public void setCriteria(LCL001 criteria) {
		this.criteria = criteria;
	}

	public LifePolicyCriteria getLifePolicyCriteria() {
		return lifePolicyCriteria;
	}

	public void setLifePolicyCriteria(LifePolicyCriteria lifePolicyCriteria) {
		this.lifePolicyCriteria = lifePolicyCriteria;
	}

	public List<LifePolicySearch> getPolicySearchList() {
		return policySearchList;
	}

	public void setPolicySearchList(List<LifePolicySearch> policySearchList) {
		this.policySearchList = policySearchList;
	}

	public void returnLifePolicyNo(SelectEvent event) {
		LifePolicySearch lifePolicy = (LifePolicySearch) event.getObject();
		criteria.setPolicyNo(lifePolicy.getPolicyNo());
	}

}
