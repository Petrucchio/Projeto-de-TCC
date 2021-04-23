package graphical_user_interface;

import java.io.IOException;
import java.net.URL;
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
import model.entities.TerapyProfessional;
import model.services.ProfessionalServices;

public class ProfessionalListController implements Initializable, DataChangeListener {
	private ProfessionalServices service;
	@FXML
	private TableView<TerapyProfessional> tableViewTerapyProfessional;
	@FXML
	private TableColumn<TerapyProfessional, Integer> tableCollumnID;
	@FXML
	private TableColumn<TerapyProfessional, String> tableCollumnName;
	@FXML
	private TableColumn<TerapyProfessional, String> tableCollumnEmail;
	@FXML
	private TableColumn<TerapyProfessional, TerapyProfessional> tableColumnEDIT;
	@FXML
	private TableColumn<TerapyProfessional, TerapyProfessional> tableColumnREMOVE;
	@FXML
	private Button btCadastrar;
	private ObservableList<TerapyProfessional> obsList;

	@FXML
	public void onBtCadastrarAction(ActionEvent event) {
		Stage parentStage = utilitarios.currentStage(event);
		TerapyProfessional obj = new TerapyProfessional();
		createDialogForm(obj, "/graphical_user_interface/ProfessionalForm.fxml", parentStage);
	}

	public void setProfessionalServices(ProfessionalServices service) {
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
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewTerapyProfessional.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<TerapyProfessional> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewTerapyProfessional.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(TerapyProfessional obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			ProfessionalFormController controller = loader.getController();
			controller.setTerapyProfessional(obj);
			controller.setProfessionalServices(new ProfessionalServices());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Professional Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<TerapyProfessional, TerapyProfessional>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(TerapyProfessional obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/graphical_user_interface/ProfessionalForm.fxml",
						utilitarios.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<TerapyProfessional, TerapyProfessional>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(TerapyProfessional obj, boolean empty) {
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

	private void removeEntity(TerapyProfessional obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
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
