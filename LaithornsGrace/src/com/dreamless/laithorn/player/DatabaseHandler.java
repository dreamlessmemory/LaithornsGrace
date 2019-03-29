package com.dreamless.laithorn.player;

import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.PlayerMessager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

public class DatabaseHandler {

	public static PlayerData retreivePlayerData(Player player) {
		String query = "SELECT * FROM " + LaithornsGrace.getDatabase() + "playerdata WHERE uuid=?";

		PlayerData playerData;

		try (PreparedStatement stmt = LaithornsGrace.connection.prepareStatement(query)) {

			stmt.setString(0, player.getUniqueId().toString());
			ResultSet result = stmt.executeQuery();

			if (!result.next()) {
				playerData = new PlayerData(result.getInt("attunementEXP"), result.getInt("attunementLevel"),
						result.getInt("smithingEXP"), result.getInt("smithingLevel"), result.getInt("essence"));

			} else {
				playerData = new PlayerData(0, 0, 0, 0, 0);
			}
		} catch (SQLException e) {
			playerData = new PlayerData(0, 0, 0, 0, 0);
			e.printStackTrace();
		}

		return playerData;
	}

	public static void updatePlayerData(UUID uuid, PlayerData playerData) {
		String query = "INSERT INTO " + LaithornsGrace.getDatabase()
				+ "playerdata (uuid, attunementEXP, attunementLevel, smithingEXP, smithingLevel, essence) VALUES (?, ?, ?, ?, ?, ?) " + 
				"ON DUPLICATE KEY UPDATE attunementEXP=?, attunementLevel=?, smithingEXP=?, smithingLevel=?, essence=?";
		
		try (PreparedStatement stmt = LaithornsGrace.connection.prepareStatement(query)) {
			// Assignment
			stmt.setString(0, uuid.toString());
			stmt.setInt(1, playerData.getAttunementEXPNeeded());
			stmt.setInt(2, playerData.getAttunementLevel());
			stmt.setInt(3, playerData.getSmithingEXPNeeded());
			stmt.setInt(4, playerData.getSmithingLevel());
			stmt.setInt(5, playerData.getEssenceStorage());
			stmt.setInt(6, playerData.getAttunementEXPNeeded());
			stmt.setInt(7, playerData.getAttunementLevel());
			stmt.setInt(8, playerData.getSmithingEXPNeeded());
			stmt.setInt(9, playerData.getSmithingLevel());
			stmt.setInt(10, playerData.getEssenceStorage());
			
			PlayerMessager.debugLog(stmt.toString());
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
