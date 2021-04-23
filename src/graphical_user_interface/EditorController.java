package graphical_user_interface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditorController implements Initializable {

    @FXML
    private TextArea textArea;

    private Stage stage;
    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser
                .getExtensionFilters()
                .addAll(
                        new FileChooser.ExtensionFilter("Text", "*.txt"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
    }
    public void init(Stage myStage) {
        this.stage = myStage;
    }
    @FXML
    private void onsave() {
        try {
            fileChooser.setTitle("Save As");
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                PrintWriter savedText = new PrintWriter(file);
                BufferedWriter out = new BufferedWriter(savedText);
                out.write(textArea.getText());
                out.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void openFile() {
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            textArea.clear();
            readText(file);
        }
    }
    private void readText(File file) {
        String text;

        try (BufferedReader buffReader = new BufferedReader(new FileReader(file))) {
            while ((text = buffReader.readLine()) != null) {
                textArea.appendText(text + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void newFile() {
        textArea.clear();
    }

    @FXML
    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("About");
        alert.setHeaderText("Esse é um editor de texto padrão criado para cadastrar o relatorio de seus pacientes");
        alert.showAndWait();
    }
    @FXML
    public void fontSize(ActionEvent e) {
        String choice = ((CheckMenuItem) e.getSource()).getId();

        switch (choice) {
            case "small":
                textArea.setStyle("-fx-font-size: 14px");
                break;
            case "default":
                textArea.setStyle("-fx-font-size: 22px");
                break;
            case "large":
                textArea.setStyle("-fx-font-size: 30px");
                break;
            default:
                textArea.setStyle("-fx-font-size: 22px");
        }
    }
}
