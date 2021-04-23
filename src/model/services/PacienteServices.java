package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.PacienteDao;
import model.entities.Paciente;

public class PacienteServices {
	private PacienteDao dao = DaoFactory.createPacienteDao();
	public List<Paciente> findAll(){
		return dao.findAll();
	}
	public void saveOrUpdate(Paciente obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	public void remove(Paciente obj) {
		dao.deleteById(obj.getId());
	}
}
