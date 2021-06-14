package com.dreamless.laithorn.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.PlayerMessager;

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
						LaithornUtils.deseralizeFlagMap(result.getString("flags")),
						result.getString("essencestorage"),
						true);

			} else {
				PlayerMessager.debugLog("No data for " + player.getDisplayName() + ". Creating profile");
				playerData = new PlayerData(0, 0, 0, 0, null, "", true);
			}
		} catch (SQLException e) {
			PlayerMessager.debugLog("Error retrieving data for " + player.getDisplayName() + ". Will attempt to re-acquire data");
			playerData = new PlayerData(0, 0, 0, 0, null, "", false);
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
			stmt.setInt(2, playerData.getAttunementEXP());
			stmt.setInt(3, playerData.getAttunementLevel());
			stmt.setInt(4, playerData.getSmithingEXP());
			stmt.setInt(5, playerData.getSmithingLevel());
			stmt.setString(6, LaithornUtils.toBase64(playerData.getInventory()));
			stmt.setString(7, serializedFlagString);
			
			// On Update
			stmt.setInt(8, playerData.getAttunementEXP());
			stmt.setInt(9, playerData.getAttunementLevel());
			stmt.setInt(10, playerData.getSmithingEXP());
			stmt.setInt(11, playerData.getSmithingLevel());
			stmt.setString(12, LaithornUtils.toBase64(playerData.getInventory()));
			stmt.setString(13, serializedFlagString);
			
			PlayerMessager.debugLog(stmt.toString());
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
