package com.example.brocki.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button button1, button2, button3, button4;
    ImageView picture;
    String result;
    ArrayList<String> photoURLresult = new ArrayList<>();
    ArrayList<String> celebrityNames = new ArrayList<>();
    int maxArrayLength=0;
    int pickedCelebrity = 0;
    String pickedCelebrityName="";
    Random rand;
    ArrayList<String> possibleAnswers = new ArrayList<>();
    int score,numberOfQuestions = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.buttonAnswer1);
        button2 = findViewById(R.id.buttonAnswer2);
        button3 = findViewById(R.id.buttonAnswer3);
        button4 = findViewById(R.id.buttonAnswer4);
        picture = findViewById(R.id.imageViewCelebrity);
        rand = new Random();
        populateAtStart();
    }

    public class imageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                button1.setText(possibleAnswers.get(0));
                button2.setText(possibleAnswers.get(1));
                button3.setText(possibleAnswers.get(2));
                button4.setText(possibleAnswers.get(3));
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    public void getHTTPData() {
        httpDownloadTask httpDownloadTask = new httpDownloadTask();
        try {
            result = httpDownloadTask.execute("http://www.posh24.se/kandisar").get();

            Pattern p = Pattern.compile("src=\"http://(.*?)\"");
            Matcher m = p.matcher(result);
            while (m.find()) {
                photoURLresult.add(m.group(1));
            }
            Pattern p2 = Pattern.compile("alt=\"(.*?)\"/>");
            Matcher m2 = p2.matcher(result);
            while (m2.find()) {
                celebrityNames.add(m2.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class httpDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }


        }
    }

    public void downloadImage(String url) {

        imageDownloaderTask task = new imageDownloaderTask();
        Bitmap myImage;
        try {
            myImage = task.execute(url).get();
            picture.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkAnswer(View view) {
        if (possibleAnswers.get(Integer.parseInt(""+view.getTag())).equals(pickedCelebrityName)){
            score+=1;
            numberOfQuestions+=1;
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            generateQuestion();
        } else {
            numberOfQuestions+=1;
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            generateQuestion();
        }
    }

    public void generateQuestion() {
        possibleAnswers.clear();
        pickedCelebrity = rand.nextInt(maxArrayLength);
        pickedCelebrityName=celebrityNames.get(pickedCelebrity);
        possibleAnswers.add(pickedCelebrityName);
        possibleAnswers.add(celebrityNames.get(rand.nextInt(maxArrayLength)));
        possibleAnswers.add(celebrityNames.get(rand.nextInt(maxArrayLength)));
        possibleAnswers.add(celebrityNames.get(rand.nextInt(maxArrayLength)));
        Collections.shuffle(possibleAnswers);
        downloadImage("http://"+photoURLresult.get(pickedCelebrity));
       }

    public void populateAtStart(){
        getHTTPData();
        maxArrayLength = celebrityNames.size();
        generateQuestion();
    }
}
