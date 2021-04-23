package model.dao;

import java.util.List;

import model.entities.TerapyProfessional;
import model.entities.Paciente;

public interface PacienteDao {

	void insert(Paciente obj);
	void update(Paciente obj);
	void deleteById(Integer id);
	Paciente findById(Integer id);
	List<Paciente> findAll();
	List<Paciente> findByDepartment(TerapyProfessional department);
}
