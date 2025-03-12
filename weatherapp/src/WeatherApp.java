import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: Location not found.");
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Use UTC timezone for accurate indexing
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m,uv_index,is_day&timezone=UTC";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to weather API.");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            JSONArray uvindexData = (JSONArray) hourly.get("uv_index");
            double uvindex = (double) uvindexData.get(index);
            String uvdanger = convertUVCode(uvindex);

            JSONArray isdayData = (JSONArray) hourly.get("is_day");
            String isdayCondition = convertIsdayCode((long) isdayData.get(index));

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            weatherData.put("uvindex", uvindex);
            weatherData.put("uvdanger", uvdanger);
            weatherData.put("isday", isdayCondition);

            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to geocoding API.");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return locationData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).equals(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(formatter);
    }

    private static String convertWeatherCode(long weathercode) {
        if (weathercode == 0L) return "Clear";
        if (weathercode <= 3L) return "Cloudy";
        if (weathercode >= 51L && weathercode <= 67L || weathercode >= 80L && weathercode <= 99L) return "Rain";
        if (weathercode >= 71L && weathercode <= 77L) return "Snow";
        return "Unknown";
    }

    private static String convertUVCode(double uvindex) {

        if (uvindex < 0) {
            return "Invalid UV Index value.";
        } else if (uvindex >= 0 && uvindex <= 2) {
            return "Low";
        } else if (uvindex > 2 && uvindex <= 5) {
            return "Medium";
        } else if (uvindex > 5 && uvindex <= 7) {
            return "High";
        } else if (uvindex > 7 && uvindex <= 10) {
            return "Severe";
        } else if (uvindex > 10) {
            return "Extreme";
        } else {
            return "Error: Could not determine UV Index risk.";
        }

    }

    private static String convertIsdayCode(long isdayData) {
        return isdayData == 1L ? "Yes" : "No";
    }


    public static String streamingMicRecognize() throws Exception {
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        StringBuilder transcriptBuilder = new StringBuilder(); // To store the transcript

        try (SpeechClient client = SpeechClient.create()) {
            responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
                ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                @Override
                public void onStart(StreamController controller) {
                    System.out.println("Stream started.");
                }

                @Override
                public void onResponse(StreamingRecognizeResponse response) {
                    responses.add(response);
                }

                @Override
                public void onComplete() {
                    System.out.println("Stream completed.");
                    for (StreamingRecognizeResponse response : responses) {
                        if (response.getResultsList().isEmpty()) continue;
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        if (result.getAlternativesList().isEmpty()) continue;
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        transcriptBuilder.append(alternative.getTranscript()).append(" ");
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error during streaming: " + t.getMessage());
                    t.printStackTrace();
                }
            };

            ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build();
            StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig)
                    .build();

            StreamingRecognizeRequest configRequest = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build();
            clientStream.send(configRequest);

            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            if (!AudioSystem.isLineSupported(targetInfo)) {
                throw new LineUnavailableException("Microphone not supported");
            }

            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            System.out.println("Start speaking...");

            long startTime = System.currentTimeMillis();
            try (AudioInputStream audio = new AudioInputStream(targetDataLine)) {
                byte[] data = new byte[6400];

                while (System.currentTimeMillis() - startTime < 5000) { // Stream for up to 1 minute
                    int bytesRead = audio.read(data);
                    if (bytesRead == -1) break;

                    StreamingRecognizeRequest audioRequest = StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(data, 0, bytesRead))
                            .build();
                    clientStream.send(audioRequest);
                }
            }

            targetDataLine.stop();
            targetDataLine.close();
            System.out.println("Stop speaking.");
        } catch (Exception e) {
            System.err.println("Exception in streamingMicRecognize: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (responseObserver != null) {
                responseObserver.onComplete();
            }
        }

        return transcriptBuilder.toString().trim(); // Return the transcript
    }
    public static String getAlertMessage(String weatherCondition) {
        switch (weatherCondition) {
            case "Rain":
                return "Possible heavy rainfall. Stay safe!";
            case "Snow":
                return "Snowstorm expected. Take precautions.";
            case "Cloudy":
                return "Foggy conditions. Drive carefully.";
            case "Storm":
                return "Thunderstorm warning. Stay indoors.";
            case "Cyclone":
                return "Cyclone alert. Follow safety measures.";
            default:
                return "No severe weather alerts.";
        }
    }

}
