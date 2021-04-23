package model.dao;

import java.util.List;

import model.entities.TerapyProfessional;

public interface ProfessionalDao {

	void insert(TerapyProfessional obj);
	void update(TerapyProfessional obj);
	void deleteById(Integer id);
	TerapyProfessional findById(Integer id);
	List<TerapyProfessional> findAll();
}
