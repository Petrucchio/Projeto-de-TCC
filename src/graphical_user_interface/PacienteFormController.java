package graphical_user_interface;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import graphical_user_interface.utilitarios.Alerts;
import graphical_user_interface.utilitarios.Constraints;
import graphical_user_interface.utilitarios.utilitarios;
import graphical_user_intrerface.listeners.DataChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Paciente;
import model.entities.TerapyProfessional;
import model.exceptions.ValidationException;
import model.services.PacienteServices;
import model.services.ProfessionalServices;

public class PacienteFormController implements Initializable {
	private Paciente entity;
	private PacienteServices service;
	private ProfessionalServices professionalServices;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private ComboBox<TerapyProfessional> comboBoxTerapyProfessional;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private ObservableList<TerapyProfessional> obsList;

	public void setPaciente(Paciente entity) {
		this.entity = entity;
	}

	public void setServices(PacienteServices service, ProfessionalServices professionalServices) {
		this.service = service;
		this.professionalServices = professionalServices;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			utilitarios.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Paciente getFormData() {
		Paciente obj = new Paciente();
		ValidationException exception = new ValidationException("Validation Error");
		obj.setId(utilitarios.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		if(dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}else {
		Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
		obj.setBirthDate(Date.from(instant));
		}
		obj.setTerapyProfessional(comboBoxTerapyProfessional.getValue());
		if (exception.getErrors().size() > 0) {
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
		utilitarios.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxTerapyProfessional();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getTerapyProfessional() == null) {
			comboBoxTerapyProfessional.getSelectionModel().selectFirst();
		}else {
		comboBoxTerapyProfessional.setValue(entity.getTerapyProfessional());
		}
	}

	public void loadAssociatedObjects() {
		if (professionalServices == null) {
			throw new IllegalStateException("Professional Services was null");
		}
		List<TerapyProfessional> list = professionalServices.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxTerapyProfessional.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));		
	}
	private void initializeComboBoxTerapyProfessional() {
		Callback<ListView<TerapyProfessional>, ListCell<TerapyProfessional>> factory = lv -> new ListCell<TerapyProfessional>() {
			@Override
			protected void updateItem(TerapyProfessional item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxTerapyProfessional.setCellFactory(factory);
		comboBoxTerapyProfessional.setButtonCell(factory.call(null));
	}

}
