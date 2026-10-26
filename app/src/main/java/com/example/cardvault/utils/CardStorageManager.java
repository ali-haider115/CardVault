package com.example.cardvault.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.cardvault.models.CardModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CardStorageManager {
    private static final String CARDS_FILE_NAME = "cards.json";
    private static final String IMAGES_DIR = "card_images";
    private final Context context;

    public CardStorageManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized List<CardModel> getAllCards() {
        List<CardModel> cardList = new ArrayList<>();
        File file = new File(context.getFilesDir(), CARDS_FILE_NAME);
        if (!file.exists()) {
            return cardList;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int bytesRead = fis.read(data);
            if (bytesRead > 0) {
                String jsonStr = new String(data, 0, bytesRead);
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    cardList.add(CardModel.fromJSONObject(obj));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cardList;
    }

    public synchronized CardModel getCardById(String id) {
        if (id == null) return null;
        for (CardModel card : getAllCards()) {
            if (id.equals(card.getId())) {
                return card;
            }
        }
        return null;
    }

    public synchronized boolean saveCard(CardModel card) {
        List<CardModel> cards = getAllCards();
        cards.add(card);
        return writeCardsToFile(cards);
    }

    public synchronized boolean updateCard(CardModel updatedCard) {
        List<CardModel> cards = getAllCards();
        boolean found = false;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(updatedCard.getId())) {
                cards.set(i, updatedCard);
                found = true;
                break;
            }
        }
        if (found) {
            return writeCardsToFile(cards);
        }
        return false;
    }

    public synchronized boolean deleteCard(String cardId) {
        List<CardModel> cards = getAllCards();
        CardModel targetCard = null;
        for (CardModel card : cards) {
            if (card.getId().equals(cardId)) {
                targetCard = card;
                break;
            }
        }

        if (targetCard != null) {
            cards.remove(targetCard);
            deleteImageFile(targetCard.getFrontImagePath());
            deleteImageFile(targetCard.getBackImagePath());
            return writeCardsToFile(cards);
        }
        return false;
    }

    private boolean writeCardsToFile(List<CardModel> cards) {
        JSONArray array = new JSONArray();
        for (CardModel card : cards) {
            try {
                array.put(card.toJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        File file = new File(context.getFilesDir(), CARDS_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(array.toString().getBytes());
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String saveImageToInternalStorage(Uri uri, String fileName) {
        if (uri == null) return "";
        File imagesDir = new File(context.getFilesDir(), IMAGES_DIR);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        File destFile = new File(imagesDir, fileName + "_" + System.currentTimeMillis() + ".jpg");
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(destFile)) {

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.flush();
                return destFile.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean deleteImageFile(String path) {
        if (path == null || path.isEmpty()) return false;
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}