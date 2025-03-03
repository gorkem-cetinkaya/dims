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
    private TextField t0, t1, t2, t3;

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
                t3.setText(String.valueOf(rs.getInt("customer_id")));
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
        String customerIdStr = t3.getText();

        if (branch.isEmpty() || balanceStr.isEmpty() || customerIdStr.isEmpty()) {
            showErrorMessage("All fields must be filled!");
            return;
        }

        try {
            BigDecimal balance = new BigDecimal(balanceStr);
            int customerId = Integer.parseInt(customerIdStr);

            String query = "INSERT INTO Account (branch, balance, customer_id) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, branch);
            pst.setBigDecimal(2, balance);
            pst.setInt(3, customerId);
            pst.executeUpdate();

            String customerAccountQuery = "INSERT INTO customer_account (customer_id, account_id, branch, balance, customer_name, customer_address) " +
                    "SELECT c.id, a.id, a.branch, a.balance, c.name, c.address " +
                    "FROM Customers c, Account a " +
                    "WHERE a.customer_id = c.id AND a.customer_id = ?";
            PreparedStatement customerAccountPst = conn.prepareStatement(customerAccountQuery);
            customerAccountPst.setInt(1, customerId);
            customerAccountPst.executeUpdate();

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
        String customerIdStr = t3.getText();

        if (accountsId.isEmpty() || branch.isEmpty() || balanceStr.isEmpty() || customerIdStr.isEmpty()) {
            showErrorMessage("All fields must be filled!");
            return;
        }

        try {
            BigDecimal balance = new BigDecimal(balanceStr);
            int customerId = Integer.parseInt(customerIdStr);

            String query = "UPDATE Account SET branch = ?, balance = ?, customer_id = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, branch);
            pst.setBigDecimal(2, balance);
            pst.setInt(3, customerId);
            pst.setInt(4, Integer.parseInt(accountsId));
            int result = pst.executeUpdate();

            if (result > 0) {
                String updateCustomerAccountQuery = "UPDATE customer_account SET branch = ?, balance = ? WHERE account_id = ?";
                PreparedStatement updatePst = conn.prepareStatement(updateCustomerAccountQuery);
                updatePst.setString(1, branch);
                updatePst.setBigDecimal(2, balance);
                updatePst.setInt(3, Integer.parseInt(accountsId));
                updatePst.executeUpdate();

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
        String accountsId = t0.getText();
        if (accountsId.isEmpty()) {
            showErrorMessage("Account ID is required to delete!");
            return;
        }

        try {
            String deleteCustomerAccountQuery = "DELETE FROM customer_account WHERE account_id = ?";
            PreparedStatement deletePst = conn.prepareStatement(deleteCustomerAccountQuery);
            deletePst.setInt(1, Integer.parseInt(accountsId));
            deletePst.executeUpdate();

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
