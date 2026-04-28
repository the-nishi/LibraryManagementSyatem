package iublibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExtraInsightController implements Initializable {
    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private RadioButton maleRadioButton;

    @FXML
    private RadioButton femaleRadioButton;

    @FXML
    private RadioButton otherRadioButton;

    @FXML
    private Text totalMembersText;

    @FXML
    private Text oldestDateText;

    @FXML
    private Text newestDateText;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    private ObservableList<LibraryMember> libraryMembers;

    public void setInitData(List<String> departments, ObservableList<LibraryMember> libraryMembers) {
        this.libraryMembers = libraryMembers;
        departmentComboBox.getItems().addAll(departments);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        maleRadioButton.setToggleGroup(toggleGroup);
        femaleRadioButton.setToggleGroup(toggleGroup);
        otherRadioButton.setToggleGroup(toggleGroup);
    }

    @FXML
    private void onGetInsight() {
        if (!validateInputs()) {
            Util.showAlert(maleRadioButton.getScene().getWindow(), "Invalid Input!");
            return;
        }
        String department = departmentComboBox.getValue();
        String gender = maleRadioButton.isSelected() ? "Male" : (femaleRadioButton.isSelected() ? "Female" : "Other");
        ObservableList<LibraryMember> filteredMembers = libraryMembers.stream()
                .filter(libraryMember -> (department.equals("All") || libraryMember.getDepartment().equals(department)) && libraryMember.getGender().equals(gender))
                .sorted(new JoiningDateComparator())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        totalMembersText.setText(
                "There are total " +
                        filteredMembers.size() +
                        " members matching the criterion"
        );
        oldestDateText.setText(filteredMembers.isEmpty() ? "" : filteredMembers.get(0).getJoiningDate().toString());
        newestDateText.setText(filteredMembers.isEmpty() ? "" : filteredMembers.get(filteredMembers.size() - 1).getJoiningDate().toString());
    }

    @FXML
    private void onGoBackToRegistration(ActionEvent event) throws Exception {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/library-register-view.fxml"))));
        stage.setTitle("Library Register");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private boolean validateInputs() {
        return departmentComboBox.getValue() != null && toggleGroup.getSelectedToggle() != null;
    }

    private static class JoiningDateComparator implements Comparator<LibraryMember> {
        @Override
        public int compare(LibraryMember m1, LibraryMember m2) {
            return m1.getJoiningDate().compareTo(m2.getJoiningDate());
        }

        @Override
        public boolean equals(Object other) {
            return false;
        }
    }
}