package viewmodel;

import dao.DbConnectivityClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Major;
import model.Person;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class DB_GUI_Controller {

    @FXML private TextField txtFirstName, txtLastName, txtDepartment, txtEmail, txtImageUrl;
    @FXML private ComboBox<Major> cbxMajor;
    @FXML private TableView<Person> tv;
    @FXML private TableColumn<Person, String> colFirstName, colLastName, colDepartment, colMajor, colEmail;
    @FXML private TableColumn<Person, ImageView> colImage;
    @FXML private Button btnAdd, btnEdit, btnDelete;
    @FXML private Label lblStatus;

    private final DbConnectivityClass db = new DbConnectivityClass();
    private ObservableList<Person> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbxMajor.setItems(FXCollections.observableArrayList(Major.values()));

        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colImage.setCellValueFactory(cellData -> {
            String url = cellData.getValue().getImageURL();
            ImageView imageView;
            try {
                imageView = new ImageView(new Image(url, 100, 100, true, true));
            } catch (Exception e) {
                imageView = new ImageView();
            }
            return new ReadOnlyObjectWrapper<>(imageView);
        });

        tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> updateFormFields(newSel));
        updateButtonState();
        loadData();
    }

    private void loadData() {
        dataList.setAll(db.getData());
        tv.setItems(dataList);
        tv.refresh();
    }

    private void updateFormFields(Person person) {
        boolean hasSelection = person != null;
        txtFirstName.setText(hasSelection ? person.getFirstName() : "");
        txtLastName.setText(hasSelection ? person.getLastName() : "");
        txtDepartment.setText(hasSelection ? person.getDepartment() : "");
        try {
            cbxMajor.setValue(hasSelection && person.getMajor() != null ? Major.valueOf(person.getMajor().toUpperCase()) : null);
        } catch (IllegalArgumentException e) {
            cbxMajor.setValue(null);
        }
        txtEmail.setText(hasSelection ? person.getEmail() : "");
        txtImageUrl.setText(hasSelection ? person.getImageURL() : "");
        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasSelection = tv.getSelectionModel().getSelectedItem() != null;
        btnEdit.setDisable(!hasSelection);
        btnDelete.setDisable(!hasSelection);
    }

    @FXML
    private void addPerson() {
        Person p = new Person(
                txtFirstName.getText(),
                txtLastName.getText(),
                txtDepartment.getText(),
                cbxMajor.getValue() != null ? cbxMajor.getValue().toString() : "",
                txtEmail.getText(),
                txtImageUrl.getText()
        );
        db.insertUser(p);
        loadData();
        lblStatus.setText("Status: Added user");
    }

    @FXML
    private void editButtonClick() {
        Person selected = tv.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Person updated = new Person(
                    txtFirstName.getText(),
                    txtLastName.getText(),
                    txtDepartment.getText(),
                    cbxMajor.getValue() != null ? cbxMajor.getValue().toString() : "",
                    txtEmail.getText(),
                    txtImageUrl.getText()
            );
            db.editUser(selected.getId(), updated);
            loadData();
            lblStatus.setText("Status: Edited user");
        }
    }

    @FXML
    private void deleteButtonClick() {
        Person selected = tv.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteRecord(selected);
            dataList.remove(selected);
            tv.refresh();
            lblStatus.setText("Status: Deleted user");
        }
    }

    @FXML
    private void importCSV() {
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
                loadData();
                lblStatus.setText("Status: Imported CSV");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Person p : dataList) {
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