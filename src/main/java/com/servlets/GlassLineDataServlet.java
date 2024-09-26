package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



@WebServlet("/data")
public class GlassLineDataServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JdbcConnection jdbcConnection = new JdbcConnection();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
	
		res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setContentType("application/json");

        PrintWriter out = res.getWriter();
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        // Fetch all users from the database
        try (ResultSet resultSet = jdbcConnection.getAllGlasses()) { // Ensure this method returns a ResultSet
            if (resultSet != null) {
                while (resultSet.next()) {
                    // Create a JSON object for each user
                    JsonObject userJson = new JsonObject();
                    userJson.addProperty("id", resultSet.getInt("id"));
                    userJson.addProperty("categoryId", resultSet.getString("categoryId"));
                    userJson.addProperty("type", resultSet.getString("type"));
                    userJson.addProperty("imageSrc", resultSet.getString("imageSrc"));
                    userJson.addProperty("specsName", resultSet.getString("specsName"));
                    userJson.addProperty("description", resultSet.getString("description"));
                    userJson.addProperty("price", resultSet.getString("price"));
                    jsonArray.add(userJson);
                }
            }
        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("message", "Error fetching glasses: " + e.getMessage());
            out.print(gson.toJson(errorJson));
            out.flush();
            return;
        } 

        // Convert JSON array to a string and send the response
        out.print(gson.toJson(jsonArray));
        out.flush();
	}
	
	

}
