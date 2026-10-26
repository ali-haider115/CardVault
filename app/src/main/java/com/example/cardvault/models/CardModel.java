package com.example.cardvault.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class CardModel {
    private String id;
    private String name;
    private String category;
    private String frontImagePath;
    private String backImagePath;
    private long createdAt;

    public CardModel() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
    }

    public CardModel(String id, String name, String category, String frontImagePath, String backImagePath, long createdAt) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.frontImagePath = frontImagePath;
        this.backImagePath = backImagePath;
        this.createdAt = createdAt > 0 ? createdAt : System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFrontImagePath() { return frontImagePath; }
    public void setFrontImagePath(String frontImagePath) { this.frontImagePath = frontImagePath; }

    public String getBackImagePath() { return backImagePath; }
    public void setBackImagePath(String backImagePath) { this.backImagePath = backImagePath; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("category", category);
        json.put("frontImagePath", frontImagePath != null ? frontImagePath : "");
        json.put("backImagePath", backImagePath != null ? backImagePath : "");
        json.put("createdAt", createdAt);
        return json;
    }

    public static CardModel fromJSONObject(JSONObject json) throws JSONException {
        return new CardModel(
                json.optString("id", UUID.randomUUID().toString()),
                json.optString("name", ""),
                json.optString("category", "Other"),
                json.optString("frontImagePath", ""),
                json.optString("backImagePath", ""),
                json.optLong("createdAt", System.currentTimeMillis())
        );
    }
}