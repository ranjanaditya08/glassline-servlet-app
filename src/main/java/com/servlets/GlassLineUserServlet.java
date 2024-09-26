package com.servlets;

import java.io.BufferedReader;
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
import com.google.gson.JsonParser;

@WebServlet("/users")
public class GlassLineUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    JdbcConnection jdbcConnection = new JdbcConnection();
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Set CORS headers for preflight requests
    	res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        res.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        // Allow cross-origin requests from React app
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000"); 
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Set response content type to JSON
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        
        // Read the request body containing the JSON data
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON data
        String requestBody = sb.toString();
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

        // Extract form values from the parsed JSON object
        String firstName = jsonObject.get("firstName").getAsString();
        String lastName = jsonObject.get("lastName").getAsString();
        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();

        // Debugging purposes
        System.out.println("Received JSON data: " + requestBody);
        System.out.println("Email: " + email);

        // Create the user in the database
        boolean isUserCreated = jdbcConnection.createUser(firstName, lastName, email, password);

        // Respond with JSON
        if (isUserCreated) {
            out.print("{\"message\": \"User created successfully!\"}");
        } else {
            out.print("{\"message\": \"User creation failed!\"}");
        }

        out.flush();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Allow cross-origin requests from React app
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setContentType("application/json");

        PrintWriter out = res.getWriter();
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        // Fetch all users from the database
        try (ResultSet resultSet = jdbcConnection.getAllUsers()) { // Ensure this method returns a ResultSet
            if (resultSet != null) {
                while (resultSet.next()) {
                    // Create a JSON object for each user
                    JsonObject userJson = new JsonObject();
                    userJson.addProperty("firstName", resultSet.getString("firstName"));
                    userJson.addProperty("lastName", resultSet.getString("lastName"));
                    userJson.addProperty("email", resultSet.getString("email"));
                    userJson.addProperty("password", resultSet.getString("password"));
                    jsonArray.add(userJson);
                }
            }
        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("message", "Error fetching users: " + e.getMessage());
            out.print(gson.toJson(errorJson));
            out.flush();
            return;
        } 

        // Convert JSON array to a string and send the response
        out.print(gson.toJson(jsonArray));
        out.flush();
    }


    
    
}
