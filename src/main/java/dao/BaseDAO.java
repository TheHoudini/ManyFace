package dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


public abstract class BaseDAO {

	private EntityManager manager;
	
	@PersistenceContext
	public void setManager(EntityManager mgr){
		manager = mgr;
	}
	
	protected EntityManager getManager(){
		return manager;
	}
	
	public <T> void save(T obj){
		manager.persist(obj);
	}
	
	public <T> void merge(T obj){
		manager.merge(obj);
	}
	
	public <T> void remove(T obj){
		manager.remove(obj);
	}
	
	public <T> T getById(long id,Class<T> ResultClass){
		try{
			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaQuery<T> critQuery = critBuilder.createQuery(ResultClass);
			
			Root<T> root = critQuery.from(ResultClass);
			critQuery.where(critBuilder.equal(root.get("id"), id));

			TypedQuery<T> query = getManager().createQuery(critQuery);
			return query.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
	
}
