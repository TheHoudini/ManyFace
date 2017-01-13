package dao;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import dbmapping.Dialog;
import dbmapping.Message;
import dbmapping.User;

public class DialogDAO extends BaseDAO {

	
	
	public List<Dialog> getDialogsWithUser(User user){
		try{
			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaQuery<Dialog> critQuery = critBuilder.createQuery(Dialog.class);
			Root<Dialog> root = critQuery.from(Dialog.class);
			Predicate cond;
			cond = getBasePredicate(critBuilder, root, user.getId());

			critQuery.where(cond);
			
			
			TypedQuery<Dialog> query = getManager().createQuery(critQuery);
			
			return query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Dialog getDialogWithUsers(User fUser, User sUser){
		try{
			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaQuery<Dialog> critQuery = critBuilder.createQuery(Dialog.class);
			Root<Dialog> root = critQuery.from(Dialog.class);
			Predicate cond;
			cond = getBasePredicate(critBuilder, root, fUser.getId(),sUser.getId());

			critQuery.where(cond);
			
			
			TypedQuery<Dialog> query = getManager().createQuery(critQuery);
			
			return query.getSingleResult();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Dialog addDialog(User fUser , User sUser){
		Dialog dlg = new Dialog(fUser.getId(),sUser.getId()) ;
		save( dlg );
		return dlg;
	}
	
	public Date addMessageToDialog(Dialog dialog, String message ,User owner){
		Message msg = new Message();
		msg.setMessage(message);
		msg.setMsgOwner( dialog.getfUser() == owner.getId() );
		Date time = GregorianCalendar.getInstance().getTime();
		msg.setSend_time(time);
		msg.setDialog_id(dialog.getId());
		save(msg);
		return time;
	}
	
	public List<Message> getMessagesFromDialog(Dialog dialog, Date date , Long count ,boolean reversed , Boolean readed)
	{
		try{
			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaQuery<Message> critQuery = critBuilder.createQuery(Message.class);
			Root<Message> root = critQuery.from(Message.class);

			Predicate cond = critBuilder.equal(root.get("dialog_id"), dialog.getId());

			cond = getMessagePredicate(date, reversed, readed, critBuilder, critQuery, root, cond);

			critQuery.where(cond);
			TypedQuery<Message> query = getManager().createQuery(critQuery);
			if(count != null)
				query.setMaxResults(count.intValue());

			List<Message> result = query.getResultList();
			long fUserId = dialog.getFirstUser().getId();
			long sUserId = dialog.getSecondUser().getId();
			for(Message msg : result)
				if(msg.isMsgOwner())
					msg.setOwnerID(fUserId);
				else
					msg.setOwnerID(sUserId);
			return result;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public void acceptMessages(Dialog dialog,User userFrom,long[] msgsId, Date date ,boolean reversed)
	{
		try {

			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaUpdate<Message> critQuery = critBuilder.createCriteriaUpdate(Message.class);
			Root<Message> root = critQuery.from(Message.class);

			Predicate cond = critBuilder.equal(root.get("dialog_id"), dialog.getId());
            Predicate ownerPredicate = critBuilder.equal(root.get("msgOwner"), dialog.getSecondUser().getId() == userFrom.getId());

            cond = critBuilder.and(cond,ownerPredicate);

            if(msgsId != null){
                Predicate idPredicate = critBuilder.disjunction();
                for(long id : msgsId)
                    idPredicate = critBuilder.or(idPredicate,critBuilder.equal(root.get("id"),id));

                cond = critBuilder.and(cond,idPredicate);
            }



			if(date != null){
				if(!reversed)
					cond = critBuilder.and(cond, critBuilder.greaterThanOrEqualTo(root.get("send_time"), date));
				else if(reversed) {
					cond = critBuilder.and(cond, critBuilder.lessThanOrEqualTo(root.get("send_time"), date));
				}
			}
			cond = critBuilder.and(cond,critBuilder.equal(root.get("readed"),0));


			critQuery.set("readed",true);
			critQuery.where(cond);
			getManager().createQuery(critQuery).executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}

	private Predicate getMessagePredicate(Date date, boolean reversed, Boolean readed, CriteriaBuilder critBuilder, CriteriaQuery<Message> critQuery, Root<Message> root, Predicate cond) {
		if(date != null){
            if(!reversed)
                cond = critBuilder.and(cond, critBuilder.greaterThanOrEqualTo(root.get("send_time"), date));
            else if(reversed) {
                cond = critBuilder.and(cond, critBuilder.lessThanOrEqualTo(root.get("send_time"), date));
                critQuery.orderBy(critBuilder.desc(root.get("send_time")));
            }
        }
		if(readed != null){
            cond = critBuilder.and(cond,critBuilder.equal(root.get("readed"),readed.booleanValue()));
        }
		return cond;
	}

	public List<Message> getAllMessages(User owner, Date date , Long count ,boolean reversed , Boolean readed)
	{
		try{
			List<Dialog> dlgList = getDialogsWithUser(owner);
			CriteriaBuilder critBuilder = getManager().getCriteriaBuilder();
			CriteriaQuery<Message> critQuery = critBuilder.createQuery(Message.class);
			Root<Message> root = critQuery.from(Message.class);
		
			Predicate cond = critBuilder.disjunction();
			for(Dialog dlg : dlgList){
				cond = critBuilder.or(cond, critBuilder.equal(root.get("dialog_id"), dlg.getId()));
			}

			cond = getMessagePredicate(date, reversed, readed, critBuilder, critQuery, root, cond);


			critQuery.where(cond);
			TypedQuery<Message> query = getManager().createQuery(critQuery);
			if(count != null)
				query.setMaxResults(count.intValue());



			List<Message> result = query.getResultList();
			
			Map<Long,Dialog> map = new HashMap<>();
			for(Dialog dlg : dlgList) map.put(new Long(dlg.getId()), dlg);
			
			for(Message msg : result){
				if(msg.isMsgOwner())
					msg.setOwnerID(map.get( new Long(msg.getDialog_id()) ).getFirstUser().getId()  );
				else
					msg.setOwnerID(map.get( new Long(msg.getDialog_id()) ).getSecondUser().getId() );
			}
			return result;
			
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private Predicate getBasePredicate(CriteriaBuilder critBuilder, Root<Dialog> root, long userid1, long userid2) {
		Predicate pred1 = getBasePredicate(critBuilder, root, userid1);
		Predicate pred2 = getBasePredicate(critBuilder, root, userid2);
		Predicate cond = critBuilder.and(pred1,pred2);
		return cond;
	}
	
	private Predicate getBasePredicate(CriteriaBuilder critBuilder, Root<Dialog> root, long userid) {
		Predicate from = critBuilder.equal(root.get("fUser"), userid);
		Predicate to = critBuilder.equal(root.get("sUser"), userid);
		Predicate cond = critBuilder.or(from,to);
		return cond;
	}
}
