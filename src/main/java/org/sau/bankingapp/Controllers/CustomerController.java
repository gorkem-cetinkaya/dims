package org.sau.bankingapp.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.sau.bankingapp.BankingApplication;
import org.sau.bankingapp.Database.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerController {

    @FXML
    private Pane customerPane;

    @FXML
    private Button b1, b2, b3, b4, b5, b6;

    @FXML
    private TextField t0, t1, t2, t3;

    private Connection conn = DBConnection.getConnection();

    @FXML
    void close(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Close ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Stage stage = (Stage) b5.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void fetch(ActionEvent event) {
        String customerIdStr = t0.getText();

        if (!customerIdStr.matches("\\d+")) {
            showErrorMessage("Please enter a valid Customer ID.");
            return;
        }
        int customerId = Integer.parseInt(t0.getText());
        try {
            String query = "SELECT * FROM Customers WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                t1.setText(rs.getString("name"));
                t2.setText(rs.getString("address"));
                t3.setText(rs.getString("city"));
            } else {
                showErrorMessage("No Customer Found with ID: " + customerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void save(ActionEvent event) {
        try {
            String name = t1.getText();
            String address = t2.getText();
            String city = t3.getText();
            if(name.isEmpty() || address.isEmpty() || city.isEmpty()) {
                showErrorMessage("All fields must be filled!");
                return;
            }
            System.out.println("Saving customer with Name: " + name + ", Address: " + address + " and City: " + city);
            String query = "INSERT INTO Customers (name, address, city) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, address);
            pst.setString(3, city);
            pst.executeUpdate();
            showConfirmationMessage("Customer Saved Successfully!");
        } catch (SQLException e) {
            System.out.println("Error while saving customer: " + e.getMessage());
            showErrorMessage("Error while saving customer: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void update(ActionEvent event) {
        try {
            String customerIdStr = t0.getText();

            if (!customerIdStr.matches("\\d+")) {
                showErrorMessage("Please enter a valid Customer ID.");
                return;
            }
            int customerId = Integer.parseInt(t0.getText());
            String name = t1.getText();
            String address = t2.getText();
            String city = t3.getText();
            if(name.isEmpty() || address.isEmpty() || city.isEmpty()) {
                showErrorMessage("All fields must be filled!");
                return;
            }
            String query = "UPDATE Customers SET name = ?, address = ?, city = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, address);
            pst.setString(3, city);
            pst.setInt(4, customerId);
            int result = pst.executeUpdate();

            if (result > 0) {
                showConfirmationMessage("Customer Updated Successfully!");
            } else {
                showErrorMessage("Customer ID not found!");
            }
        } catch (SQLException e) {
            showErrorMessage("Error while updating customer.");
            e.printStackTrace();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        String customerIdStr = t0.getText();

        if (!customerIdStr.matches("\\d+")) {
            showErrorMessage("Please enter a valid Customer ID.");
            return;
        }
        try {
            int customerId = Integer.parseInt(t0.getText());
            String query = "DELETE FROM Customers WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, customerId);
            int result = pst.executeUpdate();

            if (result > 0) {
                showConfirmationMessage("Customer Deleted Successfully!");
            } else {
                showErrorMessage("Customer ID not found!");
            }
        } catch (SQLException e) {
            showErrorMessage("Error while deleting customer.");
            e.printStackTrace();
        }
    }

    @FXML
    void getAccount(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("account-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        stage.setTitle("Account");
        stage.setScene(scene);
        stage.show();
    }


    private void showConfirmationMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
