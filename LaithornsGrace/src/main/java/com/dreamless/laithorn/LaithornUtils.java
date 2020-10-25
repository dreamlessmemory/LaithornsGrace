package com.dreamless.laithorn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.dreamless.laithorn.player.PlayerData;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class LaithornUtils {

	public final static int WRAP_SIZE = 40;
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

	public static UUID getUUID(String name) {
		try (Reader reader = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream())) {
			return gson.fromJson(reader, UUID.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public static String toBase64(Inventory inventory) {
    	if(inventory == null)
        	return "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());
            
            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }        
    }
    
    
    public static Inventory fromBase64(String data, PlayerData player) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(player, dataInput.readInt(), "Fragment Reservoir");
    
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
