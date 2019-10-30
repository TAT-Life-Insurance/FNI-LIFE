// package org.ace.insurance.system.common.stateCode.persistence;
//
// import java.util.List;
//
// import javax.persistence.PersistenceException;
// import javax.persistence.Query;
//
// import org.ace.insurance.system.common.stateCode.StateCode;
// import
// org.ace.insurance.system.common.stateCode.persistence.interfaces.IStateCodeDAO;
// import org.ace.java.component.persistence.BasicDAO;
// import org.ace.java.component.persistence.exception.DAOException;
// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Propagation;
// import org.springframework.transaction.annotation.Transactional;
//
// @Repository("StateCodeDAO")
// public class StateCodeDAO extends BasicDAO implements IStateCodeDAO{
//
// @Transactional(propagation = Propagation.REQUIRED)
// public void insert(StateCode stateCode) throws DAOException {
// try {
// em.persist(stateCode);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to insert stateCode", pe);
// }
//
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public void update(StateCode stateCode) throws DAOException {
// try {
// em.merge(stateCode);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to update stateCode", pe);
// }
//
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public void delete(StateCode stateCode) throws DAOException {
// try {
// stateCode = em.merge(stateCode);
// em.remove(stateCode);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to update stateCode", pe);
// }
//
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public StateCode findById(String id) throws DAOException {
// StateCode result = null;
// try {
// result = em.find(StateCode.class, id);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find StateCode", pe);
// }
// return result;
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public List<StateCode> findAll() throws DAOException {
// List<StateCode> result = null;
// try {
// Query q = em.createNamedQuery("StateCode.findAll");
// result = q.getResultList();
// q.setMaxResults(50);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find all of StateCode", pe);
// }
// return result;
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public List<StateCode> findByCriteria(String criteria) throws DAOException {
// List<StateCode> result = null;
// try {
// // Query q = em.createNamedQuery("Township.findByCriteria");
// Query q = em.createQuery("Select s from StateCode s where s.name Like '" +
// criteria + "%'");
// // q.setParameter("criteriaValue", "%" + criteria + "%");
// result = q.getResultList();
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find by criteria of StateCode.", pe);
// }
// return result;
// }
//
// }
