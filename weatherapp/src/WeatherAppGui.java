import org.json.simple.JSONObject;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.google.api.gax.rpc.BidiStream;
import java.io.FileInputStream;
import com.google.api.gax.rpc.ApiStreamObserver;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import javax.sound.sampled.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.util.ArrayList;


public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    private boolean isCelsius = true; // Track the current temperature unit**
    private JLabel temperatureText; // Make it an instance variable for updating**
    String userInput;
    JLabel alertsText;
    JTextField searchTextField;
    JLabel alertsImage;
    JLabel uvindexText;
    JLabel uvindexImage;
    JLabel weatherConditionImage;
    JLabel isdayImage;
    JLabel weatherConditionDesc;
    JLabel humidityImage;
    JLabel humidityText;
    JLabel windspeedImage;
    JLabel windspeedText;
    public WeatherAppGui() {
        // Setup our GUI and add a title



                        super("Weather App");

                        // Configure GUI to end the program's process once it has been closed
                        setDefaultCloseOperation(EXIT_ON_CLOSE);

                        // Set the size of our GUI (in pixels)
                        setSize(980, 650);

                        // Center the GUI on the screen
                        setLocationRelativeTo(null);

                        // Make our layout manager null to manually position our components within the GUI
                        setLayout(null);

                        // Prevent any resizing of our GUI
                        setResizable(false);

                        // Set a JLayeredPane as the content pane
                        JLayeredPane layeredPane = new JLayeredPane();
                        layeredPane.setLayout(null); // Set layout to null for manual positioning
                        setContentPane(layeredPane);

                        // Create a JLabel to hold the background image
                        JLabel backgroundLabel = new JLabel();
                        backgroundLabel.setBounds(0, 0, 1000, 650); // Match the size of the JFrame

                        // Load the background image
                        ImageIcon backgroundImage = new ImageIcon("src/assets/bg.png"); // Ensure the file path is correct
                        backgroundLabel.setIcon(backgroundImage);

                        // Add the background JLabel to the layered pane
                        layeredPane.add(backgroundLabel, Integer.valueOf(0)); // Add to the lowest layer

                        // Call a method to add GUI components
                        addGuiComponents(layeredPane);

                        // Make the GUI visible
                        setVisible(true);
                    }

                    private void addGuiComponents(JLayeredPane layeredPane) {
                        // Search field
                        searchTextField = new JTextField();
                        searchTextField.setBounds(15, 15, 800, 45); // Set the location and size
                        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24)); // Change font style and size
                        layeredPane.add(searchTextField, Integer.valueOf(1)); // Add on top of the background



                        // Weather image
                        weatherConditionImage = new JLabel(new ImageIcon("src/assets/climate.png"));
                        weatherConditionImage.setBounds(250, 140, 450, 217); // Set location and size
                        layeredPane.add(weatherConditionImage, Integer.valueOf(1)); // Add on top of the background





                // isday image
        isdayImage = new JLabel(loadImage("src/assets/dayandnight.png"));
        isdayImage.setBounds(810, 90, 85, 66);
        layeredPane.add(isdayImage, Integer.valueOf(1));

        // temperature text

        // Make temperatureText an instance variable**
        temperatureText = new JLabel("Temp. in °C");
        temperatureText.setBounds(250, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        layeredPane.add(temperatureText, Integer.valueOf(1));


        // weather condition description
        weatherConditionDesc = new JLabel("Let's check the weather!");
        weatherConditionDesc.setBounds(250, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        layeredPane.add(weatherConditionDesc,Integer.valueOf(1));

        // humidity image
        humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        layeredPane.add(humidityImage,Integer.valueOf(1));

        // humidity text
        humidityText = new JLabel("<html><b>Humidity</b></html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        layeredPane.add(humidityText,Integer.valueOf(1));

        // windspeed image
        windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(720, 500, 74, 66);
        layeredPane.add(windspeedImage,Integer.valueOf(1));

        // windspeed text
        windspeedText = new JLabel("<html><b>Windspeed</b></html>");
        windspeedText.setBounds(810, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        layeredPane.add(windspeedText,Integer.valueOf(1));

        // uvindex image
        uvindexImage = new JLabel(loadImage("src/assets/uvindex.png"));
        uvindexImage.setBounds(15, 90, 74, 66);
        layeredPane.add(uvindexImage,Integer.valueOf(1));

        // uvindex text
        uvindexText = new JLabel("<html><b>UV Index</b></html>");
        uvindexText.setBounds(90, 90, 100, 55);
        uvindexText.setFont(new Font("Dialog", Font.PLAIN, 16));
        layeredPane.add(uvindexText,Integer.valueOf(1));


        alertsImage = new JLabel(loadImage("src/assets/alerts.png"));
        alertsImage.setBounds(300, 443, 40, 66);
        layeredPane.add(alertsImage,Integer.valueOf(1));

        alertsText = new JLabel("<html><b><h1>Alerts</h1></b></html>");
        alertsText.setBounds(350, 460, 300, 36); // Adjust position and size
        alertsText.setFont(new Font("Dialog", Font.PLAIN, 18));
        //alertsText.setHorizontalAlignment(SwingConstants.CENTER);

                        alertsText.setOpaque(true); // Enable background rendering
                        alertsText.setBackground(Color.WHITE); // Background color for the box
                        alertsText.setForeground(Color.RED); // Text color
                        alertsText.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
                        alertsText.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Add a border


        layeredPane.add(alertsText,Integer.valueOf(1));

                        JButton convertButton = new JButton("°F");
                        convertButton.setBounds(562, 362, 50, 30);
                        convertButton.setFont(new Font("Dialog", Font.BOLD, 14));

                        convertButton.setContentAreaFilled(false); // Make button background transparent
                        convertButton.setOpaque(true); // Ensure the background is rendered
                        convertButton.setBackground(Color.GREEN); // Set the background color
                        convertButton.setForeground(Color.WHITE); // Set text color
                        layeredPane.add(convertButton,Integer.valueOf(1));

                        convertButton.setVisible(false);



                        convertButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                                if (weatherData != null) {
                                    double temperature = (double) weatherData.get("temperature");
                                    if (isCelsius) {
                                        // Convert to Fahrenheit
                                      //temperature = (temperature * 9 / 5) + 32;
                                        temperatureText.setText(String.format("%.1f °F", (temperature*9/5)+32));
                                        convertButton.setText("°C");
                                    } else {
                                        // Convert back to Celsius
                                        // temperature = (temperature - 32) * 5/9;
                                        temperatureText.setText(String.format("%.1f °C", temperature));
                                        convertButton.setText("°F");
                                    }
                                    isCelsius = !isCelsius;
                                }



                            }
                        });




        JButton voiceSearchButton = new JButton(loadImage("src/assets/microphone.png"));
        voiceSearchButton.setBounds(830, 13, 47, 45);
        voiceSearchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        voiceSearchButton.addActionListener(e -> {
            //String userInput = "Dubai"; //if you want to display dubai as default
            try {
                    //streamingMicRecognize();

                    String transcript = WeatherApp.streamingMicRecognize();
                    System.out.println("Received Transcript: " + transcript);

                    // Use the transcript as the user input
                    if (!transcript.isEmpty())
                        userInput = transcript;
                    else
                    {   convertButton.setVisible(false);
                        defaultGui();
                        JOptionPane.showMessageDialog(null, "No voice input received");

                    }
                    // Assign transcript to userInput
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error with voice search: " + ex.getMessage());
                }


            searchTextField.setText(userInput);

                                if (!userInput.isEmpty() && WeatherApp.getWeatherData(userInput)!=null) {
                                    convertButton.setVisible(true);
                                    updateWeatherData(userInput);
                                    // Retrieve weather data
                                }
                                else
                                {   JOptionPane.showMessageDialog(null, "Error with voice search: Please enter valid location! ");
                                    convertButton.setVisible(false);
                                    defaultGui();
                                }
        });

        layeredPane.add(voiceSearchButton,Integer.valueOf(1));

        // search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(880, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                //convertButton.setVisible(false);
                userInput = searchTextField.getText();
                if (!searchTextField.getText().trim().isEmpty() && WeatherApp.getWeatherData(userInput)!=null ) {
                    convertButton.setVisible(true);
                }
                else
                {   convertButton.setVisible(false);
                    defaultGui();
                    /*convertButton.setVisible(false);
                    weatherConditionImage.setIcon(loadImage("src/assets/climate.png"));
                    isdayImage.setIcon(loadImage("src/assets/dayandnight.png"));
                    temperatureText.setText("C");
                    weatherConditionDesc.setText("Let's check the weather!");
                    humidityText.setText("<html><b>Humidity</b></html>");
                    windspeedText.setText("<html><b>Windspeed</b> </html>");
                    uvindexText.setText("<html><b>UV Index</b></html>");
                    alertsText.setText("<html><b>Alerts</b></html>");*/
                }
                // validate input - remove whitespace to ensure non-empty text
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                updateWeatherData(userInput);
            }
            });

        layeredPane.add(searchButton,Integer.valueOf(1));


    }

    // used to create images in our gui components
    private ImageIcon loadImage(String resourcePath) {
        try {
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // returns an image icon so that our component can render it
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }


        private void updateWeatherData(String userInput) {
            if (userInput == null || userInput.trim().isEmpty()|| WeatherApp.getWeatherData(userInput)==null) {
                                JOptionPane.showMessageDialog(null, "Please enter a valid location!");
                                searchTextField.setText(null);
                                return;
                            }
                          weatherData = WeatherApp.getWeatherData(userInput);


                        if (weatherData != null) {
                            // Example: Check weather condition and set alerts
                            String weatherCondition = (String) weatherData.get("weather_condition");
                            String alertMessage = WeatherApp.getAlertMessage(weatherCondition);
                            alertsText.setText("<html>" + alertMessage + "</html>");
                        }
                        // update gui

                        // update weather image
                        String weatherCondition = (String) weatherData.get("weather_condition");

                        // depending on the condition, we will update the weather image that corresponds with the condition
                        switch (weatherCondition) {
                            case "Clear":
                                weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                                break;
                            case "Cloudy":
                                weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                                break;
                            case "Rain":
                                weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                                break;
                            case "Snow":
                                weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                                break;
                        }
                        String isdayCondition = (String) weatherData.get("isday");
                        switch (isdayCondition) {
                            case "Yes":
                                isdayImage.setIcon(loadImage("src/assets/day.png"));
                                break;
                            case "No":
                                isdayImage.setIcon(loadImage("src/assets/night.png"));
                                break;
                        }


                        // update temperature text
                        double temperature = (double) weatherData.get("temperature");
                        temperatureText.setText(temperature + " °C");

                        // update weather condition text
                        weatherConditionDesc.setText(weatherCondition);

                        // update humidity text
                        long humidity = (long) weatherData.get("humidity");
                        humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                        // update windspeed text
                        double windspeed = (double) weatherData.get("windspeed");
                        windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");

                        // update uvindex text
                        double uvindex = (double) weatherData.get("uvindex");
                        String uvdanger=(String) weatherData.get("uvdanger");

                        uvindexText.setText("<html><b>Risk: </b>" + uvdanger + "<br>(" + uvindex + ")</html>");

                        // Set temperature to Celsius
                        isCelsius = true;
                    }

                    private void defaultGui()
                    {

                        weatherConditionImage.setIcon(loadImage("src/assets/climate.png"));
                        isdayImage.setIcon(loadImage("src/assets/dayandnight.png"));
                        temperatureText.setText("Temp. in °C");
                        weatherConditionDesc.setText("Let's check the weather!");
                        humidityText.setText("<html><b>Humidity</b></html>");
                        windspeedText.setText("<html><b>Windspeed</b> </html>");
                        uvindexText.setText("<html><b>UV Index</b></html>");
                        alertsText.setText("<html><b></h1>Alerts</h1></b></html>");
                        searchTextField.setText(null);
                    }
}

