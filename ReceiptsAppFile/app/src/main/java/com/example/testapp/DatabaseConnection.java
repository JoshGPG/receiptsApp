package com.example.testapp;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseConnection extends AsyncTask<Void, Void, Connection> {

    @Override
    protected Connection doInBackground(Void... voids) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://wayne.cs.uwec.edu:3306/cs485group2";
            connection = DriverManager.getConnection(url, "WELDRT2820", "HFMM3N9I");

        } catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    protected void onPostExecute(Connection connection) {
        if (connection != null){
            try {
                String query = "SELECT * FROM users;";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){
                    String userName = resultSet.getString("name");
                    String userUsername = resultSet.getString("username");
                    System.out.println("User: " + userName + ", Username: " + userUsername);
                }

                connection.close();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
