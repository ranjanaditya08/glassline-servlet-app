package com.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/glassline";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	public boolean createUser( String firstName, String lastName, String email, String password) {
		
		
		try (Connection connection = createConnectionWithDB();){
			
			String query = "INSERT INTO users(firstName,lastName,email,password) VALUES (?,?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, password);

			int rowsAffected = preparedStatement.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return false;
	}

	public ResultSet getAllUsers() {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = createConnectionWithDB();
	        String sql = "SELECT * FROM users";
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        return rs; // Return the ResultSet to the caller
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null; // Handle errors appropriately
	    }
	   
	}


	public Connection createConnectionWithDB() {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;

	}
	
	public ResultSet getAllGlasses() {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = createConnectionWithDB();
	        String sql = "SELECT * FROM glasses";
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        return rs; // Return the ResultSet to the caller
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null; // Handle errors appropriately
	    }
	   
	}

}
