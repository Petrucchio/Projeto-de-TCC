package graphical_user_interface;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import graphical_user_interface.utilitarios.Alerts;
import graphical_user_interface.utilitarios.Constraints;
import graphical_user_interface.utilitarios.utilitarios;
import graphical_user_intrerface.listeners.DataChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.TerapyProfessional;
import model.exceptions.ValidationException;
import model.services.ProfessionalServices;

public class ProfessionalFormController implements Initializable{
	private TerapyProfessional entity;
	private ProfessionalServices service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	public void setTerapyProfessional(TerapyProfessional entity) {
		this.entity = entity;
	}
	public void setProfessionalServices(ProfessionalServices service) {
		this.service = service;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity==null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service==null) {
			throw new IllegalStateException("Service was null");
		}
		try {
		entity = getFormData();
		service.saveOrUpdate(entity);
		notifyDataChangeListeners();
		utilitarios.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e){
			Alerts.showAlert("Error saving object", null,e.getMessage(), AlertType.ERROR);
		}
	}
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}
	private TerapyProfessional getFormData() {
		TerapyProfessional obj = new TerapyProfessional();
		ValidationException exception = new ValidationException("Validation Error");
		obj.setId(utilitarios.tryParseToInt(txtId.getText()));
		if(txtName.getText()==null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		if(txtEmail.getText()==null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}
	@FXML
	public void onBtcancelAction(ActionEvent event) {
		utilitarios.currentStage(event).close();
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldMaxLength(txtEmail, 80);
	}
	public void updateFormData() {
		if(entity==null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
	}
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		if(fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email"));
		}
	}

}
