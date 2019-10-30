package org.ace.insurance.life.proposal.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policyHistory.persistence.interfaces.ILifePolicyHistoryDAO;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.persistence.interfaces.IAgentDAO;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.persistence.interfaces.IOccupationDAO;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.persistence.interfaces.IPaymentTypeDAO;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.persistence.interfaces.IRelationShipDAO;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifeProposalDAO")
public class LifeProposalDAO extends BasicDAO implements ILifeProposalDAO {

	@Resource(name = "AgentDAO")
	private IAgentDAO agentDAO;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "TownshipDAO")
	private ITownshipDAO townshipDAO;

	@Resource(name = "OccupationDAO")
	private IOccupationDAO occupationDAO;

	@Resource(name = "ProductDAO")
	private IProductDAO productDAO;

	@Resource(name = "RelationShipDAO")
	private IRelationShipDAO relationshipDAO;

	@Resource(name = "PaymentTypeDAO")
	private IPaymentTypeDAO paymentTypeDAO;

	@Resource(name = "LifePolicyHistoryDAO")
	private ILifePolicyHistoryDAO lifePolicyHistoryDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal insert(LifeProposal lifeProposal) throws DAOException {
		try {
			em.persist(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeProposal", pe);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertSurvey(LifeSurvey lifeSurvey) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM LifeSurvey l WHERE l.lifeProposal.id = :lifeProposalId");
			delQuery.setParameter("lifeProposalId", lifeSurvey.getLifeProposal().getId());
			delQuery.executeUpdate();
			em.persist(lifeSurvey);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Survey", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(LifeProposal lifeProposal) throws DAOException {
		try {
			em.merge(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(LifeProposal lifeProposal) throws DAOException {
		try {
			lifeProposal = em.merge(lifeProposal);
			em.remove(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addAttachment(LifeProposal lifeProposal) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM LifeProposalAttachment l WHERE l.lifeProposal.id = :lifeProposalId");
			delQuery.setParameter("lifeProposalId", lifeProposal.getId());
			delQuery.executeUpdate();
			for (LifeProposalAttachment att : lifeProposal.getAttachmentList()) {
				em.persist(att);
			}
			for (ProposalInsuredPerson pin : lifeProposal.getProposalInsuredPersonList()) {
				Query query = em.createQuery("DELETE FROM InsuredPersonAttachment l WHERE l.proposalInsuredPerson.id = :proposalInsuredPersonId");
				query.setParameter("proposalInsuredPersonId", pin.getId());
				query.executeUpdate();
			}
			for (ProposalInsuredPerson pin : lifeProposal.getProposalInsuredPersonList()) {
				for (InsuredPersonAttachment att : pin.getAttachmentList()) {
					em.persist(att);
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Attachment", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findById(String id) throws DAOException {
		LifeProposal result = null;
		try {
			result = em.find(LifeProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findLifePortalById(String id) throws DAOException {
		LifeProposal lifeProposal = null;
		try {
			String queryStr = "select distinct l.SUBMITTEDDATE, l.PAYMENTTYPEID, l.AGENTID, l.CUSTOMERID, " + "p.ID, p.INITIALID, p.FIRSTNAME, p.MIDDLENAME, p.LASTNAME, "
					+ "p.FATHERNAME, p.IDNO, p.IDTYPE, p.DATEOFBIRTH, p.PROPOSEDSUMINSURED, p.RESIDENTADDRESS, " + "p.RESIDENTTOWNSHIPID, p.OCCUPATIONID, p.GENDER, p.PRODUCTID "
					+ "from lproposal_request l inner join lproposal_request_insuredperson p on l.id = p.lproposalrequestid " + "where l.ID = '" + id + "' ";
			Query q = em.createNativeQuery(queryStr);
			List<Object[]> results = q.getResultList();
			if (results.size() > 0) {

				lifeProposal = new LifeProposal();
				lifeProposal.setPortalId(id);
				lifeProposal.setSubmittedDate((Date) results.get(0)[0]);

				String paymentTypeId = (String) results.get(0)[1];
				if (!StringUtils.isBlank(paymentTypeId)) {
					PaymentType paymentType = paymentTypeDAO.findById(paymentTypeId);
					lifeProposal.setPaymentType(paymentType);
				}

				String agentId = (String) results.get(0)[2];
				if (!StringUtils.isBlank(agentId)) {
					Agent agent = agentDAO.findById(agentId);
					lifeProposal.setAgent(agent);
				}

				String customerId = (String) results.get(0)[3];
				if (!StringUtils.isBlank(customerId)) {
					Customer customer = customerDAO.findById(customerId);
					lifeProposal.setCustomer(customer);
				}

				List<ProposalInsuredPerson> proposalInsuredPersonList = new ArrayList<ProposalInsuredPerson>();
				for (Object[] o : results) {
					ProposalInsuredPerson insuredPerson = new ProposalInsuredPerson();
					String insuredPersonId = (String) o[4];
					insuredPerson.setInitialId((String) o[5]);

					Name name = new Name();
					name.setFirstName((String) o[6]);
					name.setMiddleName((String) o[7]);
					name.setLastName((String) o[8]);
					insuredPerson.setName(name);

					insuredPerson.setFatherName((String) o[9]);
					insuredPerson.setIdNo((String) o[10]);
					insuredPerson.setIdType(IdType.valueOf((String) o[11]));
					insuredPerson.setDateOfBirth((Date) o[12]);
					insuredPerson.setProposedSumInsured((Double) o[13]);

					ResidentAddress residentAddress = new ResidentAddress();
					residentAddress.setResidentAddress(((String) o[14]));
					String townshipId = (String) o[15];
					if (!StringUtils.isBlank(townshipId)) {
						Township township = townshipDAO.findById(townshipId);
						residentAddress.setTownship(township);
					}
					insuredPerson.setResidentAddress(residentAddress);

					String occupationId = (String) o[16];
					if (!StringUtils.isBlank(occupationId)) {
						Occupation occupation = occupationDAO.findById(occupationId);
						insuredPerson.setOccupation(occupation);
					}

					insuredPerson.setGender(Gender.valueOf(((String) o[17])));

					String productId = (String) o[18];
					if (!StringUtils.isBlank(productId)) {
						Product product = productDAO.findById(productId);
						insuredPerson.setProduct(product);
						insuredPerson.setKeyFactorValueList(loadKeyFactor(product, insuredPerson));
					}

					String queryStr1 = "select distinct b.initialid, b.firstname, b.middlename, b.lastname, b.idno, b.idtype, "
							+ "b.gender, b.residentaddress, b.residenttownshipid, b.percentage, b.age, b.relationshipid "
							+ "from lproposal_request_insuredperson p inner join lproposal_request_beneficiaries b on p.id = b.isuraedpersonid " + "where p.id = '"
							+ insuredPersonId + "' ";
					Query q1 = em.createNativeQuery(queryStr1);
					List<Object[]> beneficiaries = q1.getResultList();
					if (beneficiaries.size() > 0) {

						List<InsuredPersonBeneficiaries> insuredPersonBeneficiariesList = new ArrayList<InsuredPersonBeneficiaries>();
						for (Object[] b : beneficiaries) {
							InsuredPersonBeneficiaries insuredPersonBeneficiaries = new InsuredPersonBeneficiaries();

							insuredPersonBeneficiaries.setInitialId((String) b[0]);
							Name iName = new Name();
							iName.setFirstName((String) b[1]);
							iName.setMiddleName((String) b[2]);
							iName.setLastName((String) b[3]);
							insuredPersonBeneficiaries.setName(iName);

							insuredPersonBeneficiaries.setIdNo((String) b[4]);
							insuredPersonBeneficiaries.setIdType(IdType.valueOf((String) b[5]));
							insuredPersonBeneficiaries.setGender(Gender.valueOf(((String) b[6])));

							ResidentAddress iResidentAddress = new ResidentAddress();
							iResidentAddress.setResidentAddress(((String) b[7]));
							String iTownshipId = (String) b[8];
							if (!StringUtils.isBlank(iTownshipId)) {
								Township township = townshipDAO.findById(iTownshipId);
								iResidentAddress.setTownship(township);
							}
							insuredPersonBeneficiaries.setResidentAddress(iResidentAddress);

							insuredPersonBeneficiaries.setPercentage(((Double) b[9]).floatValue());
							insuredPersonBeneficiaries.setAge((Integer) b[10]);

							String relationshipId = (String) o[11];
							if (!StringUtils.isBlank(relationshipId)) {
								RelationShip relationShip = relationshipDAO.findById(relationshipId);
								insuredPersonBeneficiaries.setRelationship(relationShip);
							}

							insuredPersonBeneficiariesList.add(insuredPersonBeneficiaries);
						}
						insuredPerson.setInsuredPersonBeneficiariesList(insuredPersonBeneficiariesList);
					}

					proposalInsuredPersonList.add(insuredPerson);
				}
				lifeProposal.setInsuredPersonList(proposalInsuredPersonList);
			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal", pe);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findAll() throws DAOException {
		List<LifeProposal> result = null;
		try {
			Query q = em.createNamedQuery("LifeProposal.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifeProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findByDate(Date startDate, Date endDate) throws DAOException {
		List<LifeProposal> result = null;
		try {
			Query q = em.createNamedQuery("LifeProposal.findByDate");
			q.setParameter("startDate", startDate);
			q.setParameter("endDate", endDate);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Date: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findByIdList(List<String> proposalIdList) throws DAOException {
		List<LifeProposal> result = new ArrayList<LifeProposal>();
		try {
			Query query = em.createQuery("SELECT l FROM LifeProposal l WHERE l.id IN :ids");
			query.setParameter("ids", proposalIdList);
			result = query.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Date: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifeProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuPersonMedicalStatus(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException {
		try {
			for (ProposalInsuredPerson insuPerson : proposalInsuredPersonList) {
				String queryString = "UPDATE ProposalInsuredPerson p SET p.clsOfHealth = :clsOfHealth WHERE p.id = :insuPersonId";
				Query query = em.createQuery(queryString);
				query.setParameter("clsOfHealth", insuPerson.getClsOfHealth());
				query.setParameter("insuPersonId", insuPerson.getId());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	/* used for Life Proposal Enquire (lifeProposalEnquiry.xhtml) */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LPL001> findByEnquiryCriteria(EnquiryCriteria criteria, List<Product> productList) throws DAOException {
		List<LPL001> results = new ArrayList<LPL001>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append(" SELECT l.id, l.proposalNo, a.initialId, a.name,  ");
			queryString.append(" CONCAT(TRIM(c.initialId),' ', TRIM(c.name.firstName), ' ', TRIM(c.name.middleName), ' ', TRIM(c.name.lastName)),");
			queryString.append(" o.name, l.branch.name, SUM(pi.proposedPremium),SUM(pi.proposedSumInsured),SUM(COALESCE(pi.unit,0)), ");
			queryString.append(" l.paymentType.name, l.submittedDate, ");
			queryString.append(" l.saleChannelType,b.name,sp.name");
			queryString.append(" FROM LifeProposal l INNER JOIN l.proposalInsuredPersonList pi ");
			queryString.append(" LEFT OUTER JOIN l.agent a ");
			queryString.append(" LEFT OUTER JOIN l.customer c ");
			queryString.append(" LEFT OUTER JOIN l.organization o ");
			queryString.append(" LEFT OUTER JOIN l.saleBank b");
			queryString.append(" LEFT OUTER JOIN l.salesPoints sp");
			queryString.append(" WHERE l.proposalNo IS NOT NULL  ");
			if (ProposalType.UNDERWRITING.equals(criteria.getProposalType())) {
				queryString.append(" AND l.lifePolicy IS NULL");
			} else if (ProposalType.ENDORSEMENT.equals(criteria.getProposalType())) {
				queryString.append(" AND l.lifePolicy IS NOT NULL");
			}
			if (criteria.getInsuranceType() != null) {
				queryString.append(" AND pi.product.insuranceType = :insuranceType");
			}
			if (criteria.getAgent() != null) {
				queryString.append(" AND l.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND l.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND l.submittedDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND l.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND l.organization.id = :organizationId");
			}
			if (criteria.getSaleBank() != null) {
				queryString.append(" AND l.saleBank.id = :saleBankId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND l.branch.id = :branchId");
			}
			if (criteria.getSalePoint() != null) {
				queryString.append(" AND sp.id = :salePointId");
			}
			if (criteria.getProduct() != null) {
				queryString.append(" AND pi.product.id = :productId");
			} else if (productList != null && !productList.isEmpty()) {
				queryString.append(" AND pi.product IN :productList");
			}
			if (!criteria.getProposalNo().isEmpty()) {
				queryString.append(" AND l.proposalNo like :proposalNo");
			}
			if (criteria.getProposalType() != null) {
				queryString.append(" AND l.proposalType = :proposalType");
			}
			if (criteria.getSaleChannelType() != null) {
				queryString.append(" AND l.saleChannelType =:saleChannelType");
			}
			if (criteria.getInsuredPersonName() != null) {
				queryString.append(" AND CONCAT(FUNCTION('REPLACE',pi.name.firstName,' ',''),FUNCTION('REPLACE',pi.name.middleName,' ','')");
				queryString.append(",FUNCTION('REPLACE',pi.name.lastName,' ','')) LIKE :insuredPersonName");
			}
			queryString.append(" GROUP BY l.id, l.proposalNo, a.initialId, a.name,c.initialId,c.name.firstName,c.name.middleName,c.name.lastName");
			queryString.append(", o.name, l.branch.name,b, l.branch.name,l.paymentType.name, l.submittedDate,l.saleChannelType,b.name,sp.name");
			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getProposalType() != null) {
				query.setParameter("proposalType", criteria.getProposalType());
			}
			if (criteria.getInsuranceType() != null) {
				query.setParameter("insuranceType", criteria.getInsuranceType());
			}
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getSaleBank() != null) {
				query.setParameter("saleBankId", criteria.getSaleBank().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			} else if (productList != null && !productList.isEmpty()) {
				query.setParameter("productList", productList);
			}
			if (!criteria.getProposalNo().isEmpty()) {
				query.setParameter("proposalNo", "%" + criteria.getProposalNo() + "%");
			}
			if (criteria.getSaleChannelType() != null) {
				query.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			if (criteria.getInsuredPersonName() != null) {
				query.setParameter("insuredPersonName", "%" + criteria.getInsuredPersonName().replace(" ", "") + "%");
			}
			List<Object[]> objectList = query.getResultList();
			String proposalId;
			String proposalNo;
			String agent = null;
			String customer = null;
			String oranization;
			String branch;
			double proposedPremium;
			double proposedSumInsured;
			int unit;
			String paymentType;
			Date submittedDate;
			String saleBank, salePoint;
			SaleChannelType saleChannelType;

			for (Object[] objArr : objectList) {
				agent = null;
				customer = null;
				proposalId = (String) objArr[0];
				proposalNo = (String) objArr[1];
				if (objArr[2] != null) {
					agent = (String) objArr[2] + ((Name) objArr[3]).getFullName();
				}
				if (objArr[4] != null) {
					customer = (String) objArr[4];
				}
				oranization = (String) objArr[5];
				branch = (String) objArr[6];
				proposedPremium = (Double) objArr[7];
				proposedSumInsured = (Double) objArr[8];
				unit = (int) objArr[9];
				paymentType = (String) objArr[10];
				submittedDate = (Date) objArr[11];
				saleChannelType = (SaleChannelType) objArr[12];
				saleBank = (String) objArr[13];
				salePoint = (String) objArr[14];
				customer = customer == null ? oranization : customer;
				results.add(new LPL001(proposalId, proposalNo, customer, agent, branch, proposedPremium, proposedSumInsured, paymentType, submittedDate, saleChannelType, saleBank,
						unit, salePoint));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by EnquiryCriteria : ", pe);
		}

		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProposalInsuredPerson findProposalInsuredPersonById(String id) throws DAOException {
		ProposalInsuredPerson result = null;
		try {
			result = em.find(ProposalInsuredPerson.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ProposalInsuredPerson", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeSurvey findSurveyByProposalId(String proposalId) throws DAOException {
		LifeSurvey result = null;
		try {
			Query query = em.createQuery("SELECT l FROM LifeSurvey l WHERE l.lifeProposal.id = :id");
			query.setParameter("id", proposalId);
			result = (LifeSurvey) query.getSingleResult();
			em.flush();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Proposal Survey : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyInsuredPerson> findPolicyInsuredPersonByParameters(Name name, String idNo, String fatherName, Date dob) throws DAOException {
		List<PolicyInsuredPerson> results = null;

		try {
			Query q = em.createQuery("SELECT c From PolicyInsuredPerson c WHERE c.name = :name " + " AND c.fatherName = :fatherName AND c.dateOfBirth = :dob AND c.idNo = :idNo");
			q.setParameter("name", name);
			q.setParameter("fatherName", fatherName);
			q.setParameter("dob", dob);
			q.setParameter("idNo", idNo);
			results = (List<PolicyInsuredPerson>) q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Customer", pe);
		}
		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findStatusById(String id) throws DAOException {
		String result = null;
		try {
			Query q = em.createNativeQuery("SELECT STATUS FROM LPROPOSAL_REQUEST WHERE ID = '" + id + "' ");
			result = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(String status, String id) throws DAOException {
		try {
			Query q = em.createNativeQuery("UPDATE LPROPOSAL_REQUEST SET STATUS = '" + status + "' WHERE ID = '" + id + "' ");
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update status", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findByProposalNo(String proposalNo) throws DAOException {
		LifeProposal result = null;
		try {
			Query query = em.createQuery("SELECT m FROM LifeProposal m WHERE m.proposalNo = :proposalNo");
			query.setParameter("proposalNo", proposalNo);
			result = (LifeProposal) query.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Proposal No : " + proposalNo, pe);
		}
		return result;
	}

	private List<InsuredPersonKeyFactorValue> loadKeyFactor(Product product, ProposalInsuredPerson insuredPersonInfoDTO) {
		List<InsuredPersonKeyFactorValue> keyFactorValueList = new ArrayList<InsuredPersonKeyFactorValue>();
		for (KeyFactor insKFV : product.getKeyFactorList()) {
			InsuredPersonKeyFactorValue value = new InsuredPersonKeyFactorValue(insKFV);
			if (KeyFactorChecker.isSumInsured(insKFV)) {
				value.setValue(insuredPersonInfoDTO.getProposedSumInsured() + "");
			} else if (KeyFactorChecker.isTerm(insKFV)) {
				value.setValue(insuredPersonInfoDTO.getLifeProposal().getPeriodMonth() + "");
			} else if (KeyFactorChecker.isAge(insKFV)) {
				value.setValue(insuredPersonInfoDTO.getAgeForNextYear() + "");
			}
			keyFactorValueList.add(value);
		}
		return keyFactorValueList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalPersonCode(List<ProposalInsuredPerson> personList) throws DAOException {
		try {
			for (ProposalInsuredPerson person : personList) {
				Query query = em.createQuery("UPDATE ProposalInsuredPerson p SET p.insPersonCodeNo = :personCodeNo WHERE p.id = :id");
				query.setParameter("id", person.getId());
				query.setParameter("personCodeNo", person.getInsPersonCodeNo());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update insured person code no", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalBeneficiaryCode(List<InsuredPersonBeneficiaries> beneficiaryList) throws DAOException {
		try {
			for (InsuredPersonBeneficiaries beneficiary : beneficiaryList) {
				Query query = em.createQuery("UPDATE InsuredPersonBeneficiaries p SET p.beneficiaryNo = :beneficiaryNo WHERE p.id = :id");
				query.setParameter("id", beneficiary.getId());
				query.setParameter("beneficiaryNo", beneficiary.getBeneficiaryNo());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update beneficiary code no", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalStatus(ProposalStatus status, String proposalId) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE LifeProposal l SET l.proposalStatus = :status WHERE l.id = :id");
			q.setParameter("status", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Proposal status", pe);
		}
	}

}
