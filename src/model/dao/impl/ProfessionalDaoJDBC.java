package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.ProfessionalDao;
import model.entities.TerapyProfessional;

public class ProfessionalDaoJDBC implements ProfessionalDao {

	private Connection conn;
	
	public ProfessionalDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public TerapyProfessional findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM terapeuta WHERE Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				TerapyProfessional obj = new TerapyProfessional();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setEmail(rs.getString("Email"));
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

	@Override
	public List<TerapyProfessional> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM terapeuta ORDER BY Name");
			rs = st.executeQuery();

			List<TerapyProfessional> list = new ArrayList<>();

			while (rs.next()) {
				TerapyProfessional obj = new TerapyProfessional();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setEmail(rs.getString("Email"));
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
	public void insert(TerapyProfessional obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"INSERT INTO terapeuta " +
				"(Name, Email) " +
				"VALUES " +
				"(?, ?)" , 
				Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());

			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
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
	public void update(TerapyProfessional obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"UPDATE terapeuta " +
				"SET Name = ? ," +
				" Email = ? " +
				"WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setInt(3, obj.getId());

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
			st = conn.prepareStatement(
				"DELETE FROM terapeuta WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} 
		finally {
			DB.closeStatement(st);
		}
	}
}
