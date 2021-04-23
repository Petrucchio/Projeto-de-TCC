package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.PacienteDao;
import model.entities.Paciente;
import model.entities.TerapyProfessional;

public class PacienteDaoJDBC implements PacienteDao {

	private Connection conn;
	
	public PacienteDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Paciente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO paciente "
					+ "(Name, Email, BirthDate, TerapeutaId) "
					+ "VALUES "
					+ "(?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setInt(4, obj.getTerapyProfessional().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Paciente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE paciente "
					+ "SET Name = ?, Email = ?, BirthDate = ?, TerapeutaId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setInt(4, obj.getTerapyProfessional().getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM paciente WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Paciente findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT paciente.*,terapeuta.Name as terapeutaName "
					+ "FROM paciente INNER JOIN terapeuta "
					+ "ON paciente.TerapeutaId = Terapeuta.Id "
					+ "WHERE paciente.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				TerapyProfessional terapeuta = instantiateTerapyProfessional(rs);
				Paciente obj = instantiatePaciente(rs, terapeuta);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Paciente instantiatePaciente(ResultSet rs, TerapyProfessional terapeuta) throws SQLException {
		Paciente obj = new Paciente();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setTerapyProfessional(null);;
		return obj;
	}

	private TerapyProfessional instantiateTerapyProfessional(ResultSet rs) throws SQLException {
		TerapyProfessional dep = new TerapyProfessional();
		dep.setId(rs.getInt("TerapeutaId"));
		dep.setName(rs.getString("Name"));
		dep.setEmail(rs.getString("Email"));
		return dep;
	}

	@Override
	public List<Paciente> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT paciente.*,terapeuta.Name as terapeutaName "
					+ "FROM paciente INNER JOIN terapeuta "
					+ "ON paciente.TerapeutaId = terapeuta.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Paciente> list = new ArrayList<>();
			Map<Integer, TerapyProfessional> map = new HashMap<>();
			
			while (rs.next()) {
				
				TerapyProfessional terapeuta = map.get(rs.getInt("TerapeutaId"));
				
				if (terapeuta == null) {
					terapeuta = instantiateTerapyProfessional(rs);
					map.put(rs.getInt("TerapeutaId"), terapeuta);
				}
				
				Paciente obj = instantiatePaciente(rs, terapeuta);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Paciente> findByDepartment(TerapyProfessional department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT paciente.*,paciente.Name as Name "
					+ "FROM paciente INNER JOIN terapeuta "
					+ "ON paciente.terapeutaId = terapeuta.Id "
					+ "WHERE terapeutaId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Paciente> list = new ArrayList<>();
			Map<Integer, TerapyProfessional> map = new HashMap<>();
			
			while (rs.next()) {
				
				TerapyProfessional terapeuta = map.get(rs.getInt("TerapeutaId"));
				
				if (terapeuta == null) {
					terapeuta = instantiateTerapyProfessional(rs);
					map.put(rs.getInt("TerapeutaId"), terapeuta);
				}
				
				Paciente obj = instantiatePaciente(rs, terapeuta);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
