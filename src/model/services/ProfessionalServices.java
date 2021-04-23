package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ProfessionalDao;
import model.entities.TerapyProfessional;

public class ProfessionalServices {
	private ProfessionalDao dao = DaoFactory.createProfessionalDao();
	public List<TerapyProfessional> findAll(){
		return dao.findAll();
	}
	public void saveOrUpdate(TerapyProfessional obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	public void remove(TerapyProfessional obj) {
		dao.deleteById(obj.getId());
	}
}
