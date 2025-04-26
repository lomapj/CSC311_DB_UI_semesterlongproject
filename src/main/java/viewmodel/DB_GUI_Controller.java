package viewmodel;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Person;
import dao.DbConnectivityClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

public class DB_GUI_Controller {

    @FXML private TableView<Person> tv;
    @FXML private TableColumn<Person, String> colFirstName;
    @FXML private TableColumn<Person, String> colLastName;
    @FXML private TableColumn<Person, String> colDepartment;
    @FXML private TableColumn<Person, String> colMajor;
    @FXML private TableColumn<Person, String> colEmail;
    @FXML private TableColumn<Person, ImageView> colImage;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtDepartment;
    @FXML private ComboBox<String> cbxMajor;
    @FXML private TextField txtEmail;
    @FXML private TextField txtImageUrl;
    @FXML private Label lblStatus;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnToggleTheme;

    private final DbConnectivityClass db = new DbConnectivityClass();
    private final ObservableList<Person> peopleList = FXCollections.observableArrayList();
    private boolean darkMode = false;

    @FXML
    public void initialize() {
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colImage.setCellValueFactory(cellData -> {
            String imageUrl = cellData.getValue().getImageURL();
            try {
                return new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        new ImageView(new Image(imageUrl, 100, 100, true, true)));
            } catch (IllegalArgumentException e) {
                return new javafx.beans.property.ReadOnlyObjectWrapper<>(new ImageView());
            }
        });

        tv.setItems(peopleList);
        cbxMajor.setItems(FXCollections.observableArrayList("CS", "CPIS", "ENGLISH"));

        tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = newSel == null;
            btnEdit.setDisable(disable);
            btnDelete.setDisable(disable);
            if (newSel != null) fillForm(newSel);
        });

        loadPeople();
    }

    private void fillForm(Person person) {
        txtFirstName.setText(person.getFirstName());
        txtLastName.setText(person.getLastName());
        txtDepartment.setText(person.getDepartment());
        cbxMajor.setValue(person.getMajor());
        txtEmail.setText(person.getEmail());
        txtImageUrl.setText(person.getImageURL());
    }

    private void clearForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtDepartment.clear();
        cbxMajor.setValue(null);
        txtEmail.clear();
        txtImageUrl.clear();
    }

    public void toggleTheme() {
        Scene scene = tv.getScene();
        if (scene == null) return;
        scene.getStylesheets().clear();

        try {
            String cssPath = darkMode ? "/css/light-theme.css" : "/css/dark-theme.css";
            URL resource = getClass().getResource(cssPath);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
                btnToggleTheme.setText(darkMode ? "Switch to Dark Mode" : "Switch to Light Mode");
                darkMode = !darkMode;
            } else {
                lblStatus.setText("Theme file missing in /css/");
            }
        } catch (Exception e) {
            lblStatus.setText("Error loading theme.");
        }
    }

    public void addPerson() {
        Person p = new Person(
                txtFirstName.getText(),
                txtLastName.getText(),
                txtDepartment.getText(),
                cbxMajor.getValue(),
                txtEmail.getText(),
                txtImageUrl.getText()
        );
        db.insertUser(p);
        loadPeople();
        clearForm();
        lblStatus.setText("Status: Added user");
    }

    public void editButtonClick() {
        Person selected = tv.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Person updated = new Person(
                    txtFirstName.getText(),
                    txtLastName.getText(),
                    txtDepartment.getText(),
                    cbxMajor.getValue(),
                    txtEmail.getText(),
                    txtImageUrl.getText()
            );
            updated.setId(selected.getId());
            db.editUser(selected.getId(), updated);
            loadPeople();
            tv.getSelectionModel().clearSelection();
            clearForm();
            lblStatus.setText("Status: Edited user");
        }
    }

    public void deleteButtonClick() {
        Person selected = tv.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteRecord(selected);
            loadPeople();
            tv.getSelectionModel().clearSelection();
            clearForm();
            lblStatus.setText("Status: Deleted user");
        }
    }

    private void loadPeople() {
        peopleList.setAll(db.getData());
    }

    @FXML
    public void importCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 6) {
                        Person p = new Person(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
                        db.insertUser(p);
                    }
                }
                loadPeople();
                lblStatus.setText("Status: Imported CSV");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Person p : peopleList) {
                    bw.write(String.join(",",
                            p.getFirstName(),
                            p.getLastName(),
                            p.getDepartment(),
                            p.getMajor(),
                            p.getEmail(),
                            p.getImageURL()
                    ));
                    bw.newLine();
                }
                lblStatus.setText("Status: Exported CSV");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
