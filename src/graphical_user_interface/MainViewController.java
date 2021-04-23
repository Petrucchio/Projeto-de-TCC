package graphical_user_interface;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import graphical_user_interface.utilitarios.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.PacienteServices;
import model.services.ProfessionalServices;

public class MainViewController implements Initializable{
	@FXML
	private MenuItem menuItemPaciente;
	@FXML
	private MenuItem menuItemTerapeuta;
	@FXML
	private MenuItem menuItemAbout;
	@FXML
	public void onMenuItemPacienteAction() {
		loadView("/graphical_user_interface/PacienteList.fxml", (PacienteListController controller) -> {
			controller.setPacienteServices(new PacienteServices());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemTerapeutaAction() {
		loadView("/graphical_user_interface/TerapyList.fxml", (ProfessionalListController controller) -> {
			controller.setProfessionalServices(new ProfessionalServices());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/graphical_user_interface/About.fxml", x -> {});
	}
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	private synchronized <T> void  loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Alerts.showAlert("IO Exception", "Erro loading View", e.getMessage(), AlertType.ERROR);
		}
		
	}
	
}
