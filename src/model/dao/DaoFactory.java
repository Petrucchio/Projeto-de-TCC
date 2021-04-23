package model.dao;

import db.DB;
import model.dao.impl.PacienteDaoJDBC;
import model.dao.impl.ProfessionalDaoJDBC;

public class DaoFactory {

	public static PacienteDao createPacienteDao() {
		return new PacienteDaoJDBC(DB.getConnection());
	}
	
	public static ProfessionalDao createProfessionalDao() {
		return new ProfessionalDaoJDBC(DB.getConnection());
	}
}
