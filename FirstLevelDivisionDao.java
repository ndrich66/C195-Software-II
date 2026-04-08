package controllers;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import sample.Main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** This is a class that creates a screen that allows the user to log in.
 * This is the first class to be called on application start up.
 * This class implements Initializable. */
public class UserLoginController implements Initializable
{
    Stage stage;
    Parent scene;

    public TextField usernameTxtFld;
    public PasswordField passwordField;
    public Button loginBtn;
    public Label yourLanguageLbl;

    public Label loginLbl;
    public Label usernameLbl;
    public Label passwordLbl;
    public Label languageLbl;
    public Label yourTimeZoneLbl;
    public Label currentTimeZoneLbl;
    public Label pageTitleLbl;

    /** This is an on action method.
     * This method checks for a valid login before taking the user to the appointments main screen.
     * @param actionEvent Button Click
     * @throws IOException load
     * @throws SQLException SQL called */
    public void onActionLogin(ActionEvent actionEvent) throws IOException, SQLException
    {
        String userName = usernameTxtFld.getText();
        String password = passwordField.getText();
        boolean validLogin = false;

        for(User user : UserDAO.selectAll())
        {
            if(user.getUserName().equals(userName) && user.getPassword().equals(password))
            {
                validLogin = true;

                Main.userID = (int) user.getUserID();
                Main.userName = user.getUserName();

                break;
            }
        }
        if(validLogin)
        {
            String fileName = "src/util/login_activity.txt";
            FileWriter fw = new FileWriter(fileName, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("User: " + Main.userID + " Time: " + ZonedDateTime.now() + ". Successful.");
            pw.close();

            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/Appointments.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        else
        {
            String fileName = "src/util/login_activity.txt";
            FileWriter fw = new FileWriter(fileName, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("User: " + Main.getUserActiveNow().getUserID() + " Time: " + ZonedDateTime.now() + ". Failed.");
            pw.close();

            Alert alert = new Alert(Alert.AlertType.ERROR, Main.rb.getString("loginError"));
            alert.setTitle("Error Logging In");
            alert.showAndWait();
        }
    }

    /** This is an override method.
     * This is the first method called for the class.
     * This method sets the Zone ID display, and checks for system default language.
     * @param resourceBundle for local objects.
     * @param url for global objects. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //this sets the label to the time zone of the user
        currentTimeZoneLbl.setText(String.valueOf(ZoneId.systemDefault()));

        //this sets the language for the user login page to french or english
        try
        {
            Main.rb = ResourceBundle.getBundle("util/Lan", Locale.getDefault());
            if (Locale.getDefault().getLanguage().equals("fr"))
                loginLbl.setText(Main.rb.getString("login"));
            usernameLbl.setText(Main.rb.getString("username"));
            passwordLbl.setText(Main.rb.getString("password"));
            languageLbl.setText(Main.rb.getString("language"));
            yourTimeZoneLbl.setText(Main.rb.getString("time") + " " + Main.rb.getString("zone"));
            pageTitleLbl.setText(Main.rb.getString("appointment") + " " + Main.rb.getString("scheduler"));
            loginBtn.setText(Main.rb.getString("login"));
            yourLanguageLbl.setText(Main.rb.getString("English"));
        }
        catch (MissingResourceException m)
        {
            System.out.println("username" + ' ' + "login");
        }
    }
}