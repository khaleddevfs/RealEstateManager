package com.openclassrooms.realestatemanager.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageLoader {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void loadImage(String imageUrl, ImageLoadCallback callback) {
        Log.d("TAG", "loadImage: " + imageUrl);
        Future<Drawable> future = executorService.submit(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Log.d("TAG", "loadImage: " + connection.getResponseMessage());
                return Drawable.createFromStream(inputStream, "image");
            }catch(MalformedURLException ex){
                File file=new File(imageUrl);
                if(file.exists()) {
                    try (InputStream inputStream = new FileInputStream(file)) {
                        return Drawable.createFromStream(inputStream, "image");
                    }
                    catch (FileNotFoundException e) {
                        Log.e("TAG", "loadImage: FILE NOT FOUND ", e);

                    }
                }
                return null;
            }
            catch (IOException e) {
                Log.d("TAG", "loadImage: BUG");
                Log.e("TAG", "loadImage: ",e );
                return null;
            }
        });

        executorService.execute(() -> {
            try {
                Drawable drawable = future.get();
                Log.d("TAG", "loadImage: GET");// Wait for the result
                callback.onImageLoaded(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public interface ImageLoadCallback {
        void onImageLoaded(Drawable drawable);
    }
}


