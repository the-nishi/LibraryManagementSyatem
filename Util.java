package iublibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LibraryRegisterController implements Initializable {
    @FXML
    private TextField nameTextField;

    @FXML
    private TextField contactNumberTextField;

    @FXML
    private RadioButton maleRadioButton;

    @FXML
    private RadioButton femaleRadioButton;

    @FXML
    private RadioButton otherRadioButton;

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private DatePicker dateOfJoiningPicker;

    @FXML
    private TableView<LibraryMember> membersTablesView;

    @FXML
    private TableColumn<LibraryMember, Integer> idColumn;

    @FXML
    private TableColumn<LibraryMember, String> nameColumn;

    @FXML
    private TableColumn<LibraryMember, String> departmentColumn;

    @FXML
    private TableColumn<LibraryMember, LocalDate> joiningDateColumn;

    @FXML
    private CheckBox departmentFilterCheckBox;

    @FXML
    private ComboBox<String> departmentFilterComboBox;

    @FXML
    private CheckBox dateOfJoiningFilterCheckBox;

    @FXML
    private DatePicker dateOfJoiningFilterDatePicker;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private ObservableList<LibraryMember> libraryMembers;
    private final List<String> departments = Arrays.asList("All", "Physics", "Marketing", "Finance", "Sociology", "English Literature", "Computer Science and Engineering");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureRadioButtons();
        configureTableColumns();
        configureFilterOptions();
        departmentFilterComboBox.setDisable(true);
        dateOfJoiningFilterDatePicker.setDisable(true);
        departmentComboBox.getItems().addAll(departments);
        departmentComboBox.getItems().remove("All");
        departmentFilterComboBox.getItems().addAll(departments);
        new Thread(() -> {
            libraryMembers = DB.getInstance().getLibraryMembers();
            membersTablesView.setItems(libraryMembers);
        }).start();
    }

    @FXML
    private void onApplyFilter() {
        String filterDepartment = departmentFilterComboBox.getValue();
        LocalDate filterDate = dateOfJoiningFilterDatePicker.getValue();
        if (filterDepartment == null && filterDate == null) {
            Util.showAlert(nameTextField.getScene().getWindow(), "Invalid Filter Option!");
            return;
        }
        membersTablesView.setItems(
                libraryMembers
                        .stream()
                        .filter(getLibraryMemberPredicate(filterDepartment, filterDate))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList))
        );
    }

    @FXML
    private void onRegisterNewMember() {
        LibraryMember libraryMember;
        if ((libraryMember = validateInputs()) == null) {
            Util.showAlert(nameTextField.getScene().getWindow(), "Invalid Input!");
            return;
        }
        membersTablesView.getItems().add(libraryMember);
    }

    @FXML
    private void onResetFilter() {
        departmentFilterCheckBox.setSelected(false);
        dateOfJoiningFilterCheckBox.setSelected(false);
        departmentFilterComboBox.setValue("All");
        dateOfJoiningFilterDatePicker.setValue(null);
        membersTablesView.setItems(libraryMembers);
    }

    @FXML
    private void onProceedToMoreInsight(ActionEvent event) throws Exception {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/extra-insight-view.fxml"));
        Scene scene = new Scene(loader.load());
        ExtraInsightController controller = loader.getController();
        controller.setInitData(departments, libraryMembers);
        stage.setTitle("Extra Insight");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private LibraryMember validateInputs() {
        try {
            String name = nameTextField.getText().trim();
            String contact = contactNumberTextField.getText().trim();
            String gender = maleRadioButton.isSelected() ? "Male" : (femaleRadioButton.isSelected() ? "Female" : (otherRadioButton.isSelected() ? "Other" : null));
            String department = departmentComboBox.getValue();
            LocalDate joiningDate = dateOfJoiningPicker.getValue();
            if (!name.isEmpty() && !contact.isEmpty() && gender != null && department != null && joiningDate != null) {
                return new LibraryMember(
                        libraryMembers.size(),
                        name,
                        contact,
                        department,
                        gender,
                        joiningDate
                );
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        joiningDateColumn.setCellValueFactory(new PropertyValueFactory<>("joiningDate"));
    }

    private void configureRadioButtons() {
        maleRadioButton.setToggleGroup(toggleGroup);
        femaleRadioButton.setToggleGroup(toggleGroup);
        otherRadioButton.setToggleGroup(toggleGroup);
    }

    private void configureFilterOptions() {
        departmentFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> departmentFilterComboBox.setDisable(!newValue));
        dateOfJoiningFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> dateOfJoiningFilterDatePicker.setDisable(!newValue));
    }

    private static Predicate<LibraryMember> getLibraryMemberPredicate(String filterDepartment, LocalDate filterDate) {
        Predicate<LibraryMember> predicate;
        if (filterDepartment != null && filterDate != null) {
            predicate = libraryMember -> (filterDepartment.equals("All") || libraryMember.getDepartment().equals(filterDepartment)) && libraryMember.getJoiningDate().isEqual(filterDate);
        } else if (filterDepartment != null) {
            predicate = libraryMember -> (filterDepartment.equals("All") || libraryMember.getDepartment().equals(filterDepartment));
        } else {
            predicate = libraryMember -> libraryMember.getJoiningDate().isEqual(filterDate);
        }
        return predicate;
    }
}