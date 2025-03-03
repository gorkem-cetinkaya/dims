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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountController {

    @FXML
    private Pane accountPane;

    @FXML
    private Button b1, b2, b3, b4, b5, b6;

    @FXML
    private TextField t0, t1, t2;

    private Connection conn = DBConnection.getConnection();

    @FXML
    void close(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Close ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Stage stage = (Stage) b4.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void fetch(ActionEvent event) {
        String accountsIdStr = t0.getText();

        if (!accountsIdStr.matches("\\d+")) {
            showErrorMessage("Please enter a valid Account ID.");
            return;
        }
        String accountsId = t0.getText();
        if (accountsId.isEmpty()) {
            showErrorMessage("Account ID is required to fetch data!");
            return;
        }

        try {
            String query = "SELECT * FROM Account WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(accountsId));
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                t1.setText(rs.getString("branch"));
                t2.setText(rs.getBigDecimal("balance").toString());
            } else {
                showErrorMessage("Account not found!");
            }
        } catch (SQLException e) {
            showErrorMessage("Error fetching account data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void save(ActionEvent event) {
        String branch = t1.getText();
        String balanceStr = t2.getText();

        if (branch.isEmpty() || balanceStr.isEmpty()) {
            showErrorMessage("All fields must be filled!");
            return;
        }

        try {
            BigDecimal balance = new BigDecimal(balanceStr);

            String query = "INSERT INTO Account (branch, balance) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, branch);
            pst.setBigDecimal(2, balance);
            pst.executeUpdate();

            showConfirmationMessage("Account saved successfully!");
        } catch (SQLException e) {
            showErrorMessage("Error saving account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void update(ActionEvent event) {
        String accountsId = t0.getText();
        String branch = t1.getText();
        String balanceStr = t2.getText();

        if (accountsId.isEmpty() || branch.isEmpty() || balanceStr.isEmpty()) {
            showErrorMessage("All fields must be filled!");
            return;
        }

        try {
            String accountsIdStr = t0.getText();

            if (!accountsIdStr.matches("\\d+")) {
                showErrorMessage("Please enter a valid Account ID.");
                return;
            }
            BigDecimal balance = new BigDecimal(balanceStr);

            String query = "UPDATE Account SET branch = ?, balance = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, branch);
            pst.setBigDecimal(2, balance);;
            pst.setInt(3, Integer.parseInt(accountsId));
            int result = pst.executeUpdate();

            if (result > 0) {

                showConfirmationMessage("Account updated successfully!");
            } else {
                showErrorMessage("Account ID not found!");
            }
        } catch (SQLException e) {
            showErrorMessage("Error updating account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        String accountsIdStr = t0.getText();

        if (!accountsIdStr.matches("\\d+")) {
            showErrorMessage("Please enter a valid Account ID.");
            return;
        }
        String accountsId = t0.getText();
        if (accountsId.isEmpty()) {
            showErrorMessage("Account ID is required to delete!");
            return;
        }

        try {
            String query = "DELETE FROM Account WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(accountsId));
            int result = pst.executeUpdate();

            if (result > 0) {
                showConfirmationMessage("Account deleted successfully!");
            } else {
                showErrorMessage("Account ID not found!");
            }
        } catch (SQLException e) {
            showErrorMessage("Error deleting account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void getCustomer(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApplication.class.getResource("customer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        stage.setTitle("Customer");
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
