package com.dreamless.laithorn.player;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.PlayerMessager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

public class DatabaseHandler {

	public static PlayerData retreivePlayerData(Player player) {
		String query = "SELECT * FROM " + LaithornsGrace.getDatabase() + "playerdata WHERE playeruuid=?";

		PlayerData playerData;

		try (PreparedStatement stmt = LaithornsGrace.connection.prepareStatement(query)) {

			stmt.setString(1, player.getUniqueId().toString());
			ResultSet result = stmt.executeQuery();

			if (result.next()) {
				playerData = new PlayerData(
						result.getInt("attunementexp"), 
						result.getInt("attunementLevel"),
						result.getInt("smithingexp"), 
						result.getInt("smithinglevel"), 
						result.getInt("essencestorage"),
						LaithornUtils.deseralizeFlagMap(result.getString("flags")));

			} else {
				PlayerMessager.debugLog("No data for " + player.getDisplayName() + ". Creating profile");
				playerData = new PlayerData(0, 0, 0, 0, 0, null);
			}
		} catch (SQLException e) {
			PlayerMessager.debugLog("Error retrieving data for " + player.getDisplayName() + ". Using blank profile");
			playerData = new PlayerData(0, 0, 0, 0, 0, null);
			e.printStackTrace();
		}

		return playerData;
	}

	public static void updatePlayerData(UUID uuid, PlayerData playerData) {
		String query = "INSERT INTO " + LaithornsGrace.getDatabase()
				+ "playerdata (playeruuid, attunementexp, attunementlevel, smithingexp, smithinglevel, essencestorage, flags) VALUES (?, ?, ?, ?, ?, ?, ?) " + 
				"ON DUPLICATE KEY UPDATE attunementexp=?, attunementlevel=?, smithingexp=?, smithinglevel=?, essencestorage=?, flags=?";
		
		try (PreparedStatement stmt = LaithornsGrace.connection.prepareStatement(query)) {
			
			// Conversion
			String serializedFlagString = LaithornUtils.gson.toJson(playerData.getFlags());
			
			// Assignment
			stmt.setString(1, uuid.toString());
			stmt.setInt(2, playerData.getAttunementEXPNeeded());
			stmt.setInt(3, playerData.getAttunementLevel());
			stmt.setInt(4, playerData.getSmithingEXPNeeded());
			stmt.setInt(5, playerData.getSmithingLevel());
			stmt.setInt(6, playerData.getEssenceStorage());
			stmt.setString(7, serializedFlagString);
			
			// On Update
			stmt.setInt(8, playerData.getAttunementEXPNeeded());
			stmt.setInt(9, playerData.getAttunementLevel());
			stmt.setInt(10, playerData.getSmithingEXPNeeded());
			stmt.setInt(11, playerData.getSmithingLevel());
			stmt.setInt(12, playerData.getEssenceStorage());
			stmt.setString(13, serializedFlagString);
			
			PlayerMessager.debugLog(stmt.toString());
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
