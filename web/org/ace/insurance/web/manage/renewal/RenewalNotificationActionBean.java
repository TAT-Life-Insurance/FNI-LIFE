package org.ace.insurance.web.manage.renewal;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;
import org.ace.insurance.renewal.service.interfaces.IRenewalNotificationService;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "RenewalNotificationActionBean")
public class RenewalNotificationActionBean extends BaseBean {

	@ManagedProperty(value = "#{RenewalNotificationService}")
	private IRenewalNotificationService notificationService;

	public void setNotificationService(IRenewalNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	private List<RenewalNotification> notificationList;
	private List<RenewalNotification> selectedList;
	private List<PolicyReferenceType> policyReferenceTypes;
	private RenewalNotificationCriteria criteria;

	@PostConstruct
	public void init() {
		createNewCriteria();
		loadPolicyReferenceType();
	}

	private void createNewCriteria() {
		criteria = new RenewalNotificationCriteria();
	}

	public void search() {
		notificationList = notificationService.findPoliciesByCriteria(criteria);
	}

	public void reset() {
		notificationList = null;
		selectedList = null;
		createNewCriteria();
	}

	public List<RenewalNotification> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<RenewalNotification> selectedList) {
		this.selectedList = selectedList;
	}

	public List<RenewalNotification> getNotificationList() {
		return notificationList;
	}

	public RenewalNotificationCriteria getCriteria() {
		return criteria;
	}

	public List<PolicyReferenceType> getPolicyReferenceTypes() {
		return policyReferenceTypes;
	}

	private void loadPolicyReferenceType() {
		policyReferenceTypes = Arrays.asList(PolicyReferenceType.PA_POLICY, PolicyReferenceType.FARMER_POLICY, PolicyReferenceType.SNAKE_BITE_POLICY,
				PolicyReferenceType.GROUP_LIFE_POLICY, PolicyReferenceType.SPORT_MAN_POLICY, PolicyReferenceType.ENDOWNMENT_LIFE_POLICY,
				PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.HEALTH_POLICY, PolicyReferenceType.MICRO_HEALTH_POLICY,
				PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
	}

}
