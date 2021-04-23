package graphical_user_interface;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import graphical_user_interface.utilitarios.Alerts;
import graphical_user_interface.utilitarios.utilitarios;
import graphical_user_intrerface.listeners.DataChangeListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Paciente;
import model.services.PacienteServices;
import model.services.ProfessionalServices;

public class PacienteListController implements Initializable, DataChangeListener {
	private PacienteServices service;
	@FXML
	private TableView<Paciente> tableViewPaciente;
	@FXML
	private TableColumn<Paciente, Integer> tableCollumnID;
	@FXML
	private TableColumn<Paciente, String> tableCollumnName;
	@FXML
	private TableColumn<Paciente, String> tableCollumnEmail;
	@FXML
	private TableColumn<Paciente, Date> tableCollumnBirthDate;
	@FXML
	private TableColumn<Paciente, Paciente> tableColumnEDIT;
	@FXML
	private TableColumn<Paciente, Paciente> tableColumnREMOVE;
	@FXML
	private Button btCadastrar;
	private ObservableList<Paciente> obsList;
	
	
	@FXML
	public void onBtRelatorioAction(ActionEvent even) throws IOException {
		Stage s1 = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));
        Scene scene = new Scene(root);

        s1.setScene(scene);
        s1.show(); 
	}

	@FXML
	public void onBtCadastrarAction(ActionEvent event) {
		Stage parentStage = utilitarios.currentStage(event);
		Paciente obj = new Paciente();
		createDialogForm(obj, "/graphical_user_interface/PacienteForm.fxml", parentStage);
	}
	

	public void setPacienteServices(PacienteServices service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		InicializeNodes();

	}

	private void InicializeNodes() {
		tableCollumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableCollumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableCollumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableCollumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("BirthDate"));
		utilitarios.formatTableColumnDate(tableCollumnBirthDate, "dd/MM/yyyy");
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewPaciente.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Paciente> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewPaciente.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Paciente obj, String absoluteName, Stage parentStage) {
		
		  try { FXMLLoader loader = new
		  FXMLLoader(getClass().getResource(absoluteName)); Pane pane = loader.load();
		  PacienteFormController controller = loader.getController();
		  controller.setPaciente(obj); 
		  controller.setServices(new PacienteServices(), new ProfessionalServices());
		  controller.loadAssociatedObjects();
		  controller.subscribeDataChangeListener(this);
		  controller.updateFormData(); 
		  Stage dialogStage = new Stage();
		  dialogStage.setTitle("Enter Paciente Data");
		  dialogStage.setScene(new Scene(pane)); dialogStage.setResizable(false);
		  dialogStage.initOwner(parentStage);
		  dialogStage.initModality(Modality.WINDOW_MODAL);
		  dialogStage.showAndWait(); }
		  catch (IOException e) { 
			  e.printStackTrace();
			  Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR); }
		 }

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Paciente, Paciente>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Paciente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/graphical_user_interface/PacienteForm.fxml",
						utilitarios.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Paciente, Paciente>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Paciente obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Paciente obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja deletar?");
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
			service.remove(obj);
			updateTableView();
			}catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
