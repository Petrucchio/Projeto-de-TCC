package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Paciente implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String email;
	private Date birthDate;
	
	private TerapyProfessional terapeuta;
	
	public Paciente() {
	}

	public Paciente(Integer id, String name, String email, Date birthDate, TerapyProfessional terapeuta) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.birthDate = birthDate;
		this.terapeuta = terapeuta;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}


	public TerapyProfessional getTerapyProfessional() {
		return terapeuta;
	}

	public void setTerapyProfessional(TerapyProfessional terapeuta) {
		this.terapeuta = terapeuta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paciente other = (Paciente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Seller [id=" + id + ", name=" + name + ", email=" + email + ", birthDate=" + birthDate 
				 + ", terapeuta=" + terapeuta + "]";
	}
}
