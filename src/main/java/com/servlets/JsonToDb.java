package com.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

class Glass {
	int id;
	String categoryId;
	String type;
	String imageSrc;
	String specsName;
	String price;
	String description;
}

public class JsonToDb {
	public static void main(String[] args) {
		String jsonFilePath = "/C://Users//Lenovo//Downloads//MOCK_DATA.json/";
		List<Glass> glasses = parseJson(jsonFilePath);

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/glassline", "root",
				"root")) {
			String sql = "INSERT INTO glasses (id, categoryId, type, imageSrc, specsName,price, description) VALUES (?, ?, ?,?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			for (Glass glass : glasses) {
				pstmt.setInt(1, glass.id);
				pstmt.setString(2, glass.categoryId);
				pstmt.setString(3, glass.type);
				pstmt.setString(4, glass.imageSrc);
				pstmt.setString(5, glass.specsName);
				pstmt.setString(6, glass.price);
				pstmt.setString(7, glass.description);
				pstmt.addBatch();
			}

			pstmt.executeBatch(); // Execute batch insert
			System.out.println("Data inserted successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Glass> parseJson(String jsonFilePath) {
		try (FileReader reader = new FileReader(jsonFilePath)) {
			return new Gson().fromJson(reader, new TypeToken<List<Glass>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
