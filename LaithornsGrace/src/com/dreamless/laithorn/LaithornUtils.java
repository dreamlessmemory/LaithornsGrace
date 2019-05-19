package com.dreamless.laithorn;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bukkit.Location;
import org.bukkit.util.ChatPaginator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class LaithornUtils {

	public static int WRAP_SIZE = 40;
	// Parsing
	public static Gson gson = new Gson();

	public static ArrayList<String> wrapText(String text) {
		ArrayList<String> wrappedText = new ArrayList<String>();
		wrappedText.addAll(Arrays.asList(ChatPaginator.wordWrap(text, WRAP_SIZE)));
		return wrappedText;
	}

	public static String serializeLocation(Location location) {
		return gson.toJson(location.serialize());
	}

	public static HashMap<String, Boolean> deseralizeFlagMap(String json) {
		return gson.fromJson(json, new TypeToken<HashMap<String, Boolean>>() {
		}.getType());
	}

	public static Location deserializeLocation(String json) {
		return Location.deserialize(gson.fromJson(json, new TypeToken<HashMap<String, Object>>() {
		}.getType()));
	}

	public static UUID getUUID(String name) throws JsonSyntaxException {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
		try {
			String UUIDJson = IOUtils.toString(new URL(url), "US-ASCII");
			if (UUIDJson.isEmpty()) {
				return null;
			}
			return gson.fromJson(UUIDJson, UUID.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
