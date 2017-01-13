package dao;


import dbmapping.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


@Transactional
@Repository
public class UserDAO extends BaseDAO {

    public List<User> findUsers(String username){
        try{
            CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
            CriteriaQuery<User> critQuery = critBuilder.createQuery(User.class);

            Root<User> root = critQuery.from(User.class);
            critQuery.where(critBuilder.like(root.get("username"), "%"+username+"%"));

            TypedQuery<User> query = getManager().createQuery(critQuery);
            return query.getResultList();
        }catch(Exception e){
            return null;
        }
    }
}


