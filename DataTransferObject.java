package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import dao.AppointmentDAO;
import dao.ContactsDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ResourceBundle;

/** This is a class that creates generated reports.
 * This class implements Initializable. */
public class ReportsController implements Initializable
{
    @FXML
    private ComboBox<Appointments> apptTypeCmbBx;
    @FXML
    private ComboBox<Contacts> contactDetailsCmbBx;
    @FXML
    private ComboBox<Month> monthComboBox;
    @FXML
    private ComboBox<Contacts> contactComboBox;

    @FXML
    private TableView<Appointments> contactScheduleTblView;
    @FXML
    private TableColumn<Appointments, Integer> apptIDCol;
    @FXML
    private TableColumn<Appointments, String> aptTitleCol;
    @FXML
    private TableColumn<Appointments, String> typeContactCol;
    @FXML
    private TableColumn<Appointments, String> descriptionCol;
    @FXML
    private TableColumn<Appointments, LocalDateTime> startCol;
    @FXML
    private TableColumn<Appointments, LocalDateTime> endCol;
    @FXML
    private TableColumn<Appointments, Integer> custIDCol;

    @FXML
    private TableView<Contacts> contactDetailsTblView;
    @FXML
    private TableColumn<Contacts, Integer> contactIDCol;
    @FXML
    private TableColumn<Contacts, String> contactNameCol;
    @FXML
    private TableColumn<Contacts, String> contactEmailCol;

    @FXML
    private Label countLbl;

    Stage stage;
    Parent scene;

    /** This is an on action method.
     * This method populates a table when a selection is made from this combo box.
     * @param actionEvent Combo box Selection
     * @throws SQLException SQL method */
    public void onActionContactCmbBx(ActionEvent actionEvent) throws SQLException {
        int contactID = contactComboBox.getValue().getContactID();
        ObservableList <Appointments> contactAptFilter = FXCollections.observableArrayList();
        for (Appointments appointments : AppointmentDAO.selectAll()) {
            if (contactID == appointments.getContactID()){
                contactAptFilter.add(appointments);
            }
            contactScheduleTblView.setItems(contactAptFilter);
        }
    }

    /** This is an on action method.
     * This method populates a table with filtered results when a selection is made from this combo box.
     * @param actionEvent Combo Box Selection */
    public void onActionContactDetailsCmbBx(ActionEvent actionEvent)
    {
        int contactID = contactDetailsCmbBx.getValue().getContactID();
        ObservableList<Contacts> contactFilter = FXCollections.observableArrayList();
        for(Contacts contacts : contactDetailsCmbBx.getItems())
        {
            if (contactID == contacts.getContactID()){
                contactFilter.add(contacts);
            }
            contactDetailsTblView.setItems(contactFilter);
        }
    }

    /** This is an on action method.
     * This method will switch screens to the appointments main screen.
     * @param actionEvent Button Click
     * @throws IOException load */
    public void onActionMainMenu(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** This is an override method.
     * This is the first method to get called for this screen.
     * There are several DAO classes called to populate varies appropriate combo boxes.
     * @param resourceBundle Local Objects
     * @param url Global Objects */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try
        {
            contactComboBox.setItems(ContactsDAO.selectAll());
            contactDetailsCmbBx.setItems(ContactsDAO.selectAll());
            apptTypeCmbBx.setItems(AppointmentDAO.selectType());
            contactScheduleTblView.setItems(AppointmentDAO.selectAll());
            contactDetailsTblView.setItems(ContactsDAO.selectAll());
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        for(int i = 1; i <= 12; i++){
            monthComboBox.getItems().add(Month.of(i));
        }

        //contact schedule cell value set
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        aptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeContactCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        //contact details cell values set
        contactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        contactEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    /** This is an on action method.
     * This method retrieves the count for what the user has selected.
     * @param actionEvent Button Click */
    public void onActionGetCount(ActionEvent actionEvent)
    {
        try {
            Month month = monthComboBox.getSelectionModel().getSelectedItem();
            String aptType = apptTypeCmbBx.getSelectionModel().getSelectedItem().getType();

            int startMonth = month.getValue();
            int rowsAffected = AppointmentDAO.countMonth(aptType, startMonth);

            if (aptType == null || month == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an appointment type and a month.");
                alert.setTitle("Error Counting Appointments");
                alert.showAndWait();
            }
            else
            {
                countLbl.setText(String.valueOf(rowsAffected));
            }
        }
        catch (SQLException sql){
            sql.printStackTrace();
        }
    }
}