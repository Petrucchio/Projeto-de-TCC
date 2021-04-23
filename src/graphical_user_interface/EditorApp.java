package graphical_user_interface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class EditorApp extends Application{ 

   
	@Override
    public void start(Stage myStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui.fxml"));

    Parent root = fxmlLoader.load();
    EditorController controller = fxmlLoader.getController();
    controller.init(myStage);

    myStage.setTitle("Editor de Texto");
    myStage.setScene(new Scene(root, 700, 500));
    myStage.show();
}}

