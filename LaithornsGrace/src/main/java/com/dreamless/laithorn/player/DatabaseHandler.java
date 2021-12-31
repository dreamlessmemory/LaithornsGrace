package com.dreamless.laithorn.player;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.PlayerMessager;

public class DatabaseHandler {

	private static int BONUS_CAP = 640;
	private static int DAILY_BONUS = 64;

	public static PlayerData retreivePlayerData(Player player) {
		String query = "SELECT * FROM " + LaithornsGrace.getDatabase() + "playerdata WHERE playeruuid=?";

		PlayerData playerData;

		try (PreparedStatement stmt = LaithornsGrace.connection.prepareStatement(query)) {

			stmt.setString(1, player.getUniqueId().toString());
			ResultSet result = stmt.executeQuery();

			if (result.next()) {

				// Calculate Login Boosts
				// Get current date
				Date loginDate = getCurrentSqlDate();
				int bonusAmount = getBonusAmount(result.getDate("lastlogin"), loginDate, result.getInt("boosts"));

				playerData = new PlayerData(
						result.getInt("attunementexp"), 
						result.getInt("attunementLevel"),
						result.getInt("smithingexp"), 
						result.getInt("smithinglevel"),
						LaithornUtils.deseralizeFlagMap(result.getString("flags")),
						result.getString("essencestorage"),
						bonusAmount, 
						loginDate, 
						true);

			} else {
				PlayerMessager.debugLog("No data for " + player.getDisplayName() + ". Creating profile");
				playerData = new PlayerData(0, 0, 0, 0, null, "", 64, getCurrentSqlDate(), true);
			}
		} catch (SQLException e) {
			PlayerMessager.debugLog("Error retrieving data for " + player.getDisplayName() + ". Will attempt to re-acquire data");
			playerData = new PlayerData(0, 0, 0, 0, null, "", 0, getCurrentSqlDate(), false);
			e.printStackTrace();
		}

		return playerData;
	}

	public static void updatePlayerData(UUID uuid, PlayerData playerData) {
		String query = "INSERT INTO " + LaithornsGrace.getDatabase()
		+ "playerdata (playeruuid, attunementexp, attunementlevel, smithingexp, smithinglevel, essencestorage, flags, lastlogin, boosts) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " + 
		"ON DUPLICATE KEY UPDATE attunementexp=?, attunementlevel=?, smithingexp=?, smithinglevel=?, essencestorage=?, flags=?, lastlogin=?, boosts=?";

		try (PreparedStatement stmt = LaithornsGrace.connection.prepareStatement(query)) {

			// Conversion
			String serializedFlagString = LaithornUtils.gson.toJson(playerData.getFlags());

			// Assignment
			stmt.setString(1, uuid.toString());

			// If new
			stmt.setInt(2, playerData.getAttunementEXP());
			stmt.setInt(3, playerData.getAttunementLevel());
			stmt.setInt(4, playerData.getSmithingEXP());
			stmt.setInt(5, playerData.getSmithingLevel());
			stmt.setString(6, LaithornUtils.toBase64(playerData.getInventory()));
			stmt.setString(7, serializedFlagString);
			stmt.setDate(8, playerData.getLastLoginDate());
			stmt.setInt(9, playerData.getBoostedFragments());

			// On Update
			stmt.setInt(10, playerData.getAttunementEXP());
			stmt.setInt(11, playerData.getAttunementLevel());
			stmt.setInt(12, playerData.getSmithingEXP());
			stmt.setInt(13, playerData.getSmithingLevel());
			stmt.setString(14, LaithornUtils.toBase64(playerData.getInventory()));
			stmt.setString(15, serializedFlagString);
			stmt.setDate(16, playerData.getLastLoginDate());
			stmt.setInt(17, playerData.getBoostedFragments());

			PlayerMessager.debugLog(stmt.toString());

			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static int getBonusAmount(Date previousLogin, Date currentLogin, int currentBonus)
	{

		if(previousLogin == null)
		{
			PlayerMessager.debugLog("Bonus set: " + DAILY_BONUS);
			return DAILY_BONUS;
		}

		LocalDate lastReset = LocalDate.now();
		LocalDate previousLoginDay = previousLogin.toLocalDate();

		if (lastReset.isEqual(previousLoginDay)) // Same day
		{
			return currentBonus;
		}

		LocalDate currentLoginDay = currentLogin.toLocalDate();

		Period foo = previousLoginDay.until(currentLoginDay);
		int rawBonus = foo.getDays() * DAILY_BONUS;

		PlayerMessager.debugLog("Bonus calculated: " + rawBonus);

		return rawBonus + currentBonus > BONUS_CAP ? BONUS_CAP : rawBonus + currentBonus;
	}

	private static java.sql.Date getCurrentSqlDate()
	{
		return new Date(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
	}

	public static int getBonusCap() {
		return BONUS_CAP;
	}

	public static void setBonusCap(int bonusCap) {
		BONUS_CAP = bonusCap;
	}

	public static int getDailyBonus() {
		return DAILY_BONUS;
	}

	public static void setDailyBonus(int dailyBonus) {
		DAILY_BONUS = dailyBonus;
	}
}
