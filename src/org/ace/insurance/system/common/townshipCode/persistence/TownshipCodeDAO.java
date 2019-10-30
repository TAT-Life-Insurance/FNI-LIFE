// package org.ace.insurance.system.common.townshipCode.persistence;
//
// import java.util.List;
//
// import javax.persistence.PersistenceException;
// import javax.persistence.Query;
//
// import org.ace.insurance.system.common.townshipCode.TownshipCode;
// import
// org.ace.insurance.system.common.townshipCode.persistence.interfaces.ITownshipCodeDAO;
// import org.ace.java.component.persistence.BasicDAO;
// import org.ace.java.component.persistence.exception.DAOException;
// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Propagation;
// import org.springframework.transaction.annotation.Transactional;
//
// @Repository("TownshipCodeDAO")
// public class TownshipCodeDAO extends BasicDAO implements ITownshipCodeDAO {
//
// @Transactional(propagation = Propagation.REQUIRED)
// public void insert(TownshipCode townshipCode) throws DAOException {
// try {
// em.persist(townshipCode);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to insert townshipCode", pe);
// }
//
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public void update(TownshipCode townshipCode) throws DAOException {
// try {
// em.merge(townshipCode);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to update townshipCode", pe);
// }
//
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public void delete(TownshipCode townshipCode) throws DAOException {
// try {
// townshipCode = em.merge(townshipCode);
// em.remove(townshipCode);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to update townshipCode", pe);
// }
//
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public TownshipCode findById(String id) throws DAOException {
// TownshipCode result = null;
// try {
// result = em.find(TownshipCode.class, id);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find TownshipCode", pe);
// }
// return result;
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public List<TownshipCode> findAll() throws DAOException {
// List<TownshipCode> result = null;
// try {
// Query q = em.createNamedQuery("TownshipCode.findAll");
// result = q.getResultList();
// q.setMaxResults(50);
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find all of TownshipCode", pe);
// }
// return result;
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public List<TownshipCode> findByCriteria(String criteria) throws DAOException
// {
// List<TownshipCode> result = null;
// try {
// // Query q = em.createNamedQuery("Township.findByCriteria");
// Query q = em.createQuery("Select t from TownshipCode t where t.name Like '" +
// criteria + "%'");
// // q.setParameter("criteriaValue", "%" + criteria + "%");
// result = q.getResultList();
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find by criteria of TownshipCode.", pe);
// }
// return result;
// }
//
// @Transactional(propagation = Propagation.REQUIRED)
// public List<TownshipCode> findByStateCode(String stateCodeId) throws
// DAOException {
// List<TownshipCode> result = null;
// try {
// Query q = em.createQuery("Select t from TownshipCode t where t.stateCode.id
// =:stateCodeId ORDER BY t.name");
// q.setParameter("stateCodeId", stateCodeId);
// result = q.getResultList();
// em.flush();
// } catch (PersistenceException pe) {
// throw translate("Failed to find by criteria of TownshipCode.", pe);
// }
// return result;
// }
//
// }
