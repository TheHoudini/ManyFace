package dao;


import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dbmapping.Account;

@Transactional
@Repository
public class AccountDAO extends BaseDAO{


	public Account getAccountByLogin(String login){
		try{
			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaQuery<Account> critQuery = critBuilder.createQuery(Account.class);
			
			Root<Account> root = critQuery.from(Account.class);
			critQuery.where(critBuilder.equal(root.get("login"), login));
			TypedQuery<Account> acc = getManager().createQuery(critQuery);
			return acc.getSingleResult();
		}catch(Exception e){
			return null;
		}
	}
}
