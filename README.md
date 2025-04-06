
# 🌤️ WeatherApp-with-AI-Speech-Recognition-GCP🌧️

A comprehensive weather application that provides real-time weather information such as temperature, UV index, humidity, and wind speed. The app includes dynamic images based on weather conditions and time of day, along with danger warnings for extreme weather events. It also incorporates speech recognition for a hands-free user experience.

## Features 🌟

- **Real-Time Weather Data:** 🌡️ Displays current weather conditions such as temperature, humidity, UV index, and wind speed.
- **Dynamic Images:** 🌈 Weather-based images that change depending on conditions like rain, snow, or clear skies, and time of day (day or night).
- **Location Search:** 🔍 A responsive search bar that accepts only valid location inputs.
- **UV Index Alerts:** ☀️ Warning messages based on the UV index, notifying users about dangerous levels of UV exposure.
- **Extreme Weather Alerts:** 🌪️ Notifications for extreme weather events such as cyclones, storms, and fog.
- **Speech Recognition:** 🎤 Voice-based location search using Google Cloud Speech-to-Text API.
- **Geolocation Support:** 🌍 Fetches latitude and longitude of the entered location using a geolocation API.
- **User-Friendly GUI:** 📱 Clean and responsive design optimized for all devices.

## Technologies Used 💻

- **Frontend:** HTML, CSS, JavaScript
- **APIs:**
  - 🌍 Geolocation API (for fetching latitude and longitude)
  - 🌦️ Weather Data API (for fetching real-time weather data)
  - 🎤 Google Cloud Speech-to-Text API (for voice search functionality)
- **Libraries:** Axios (for API requests), Bootstrap or Material UI (for styling)
- **Other:** Dynamic images based on weather conditions, real-time data updates

## Installation ⚙️

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/weather-app.git
   cd weather-app
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Set up environment variables:
   - You'll need API keys for the weather API and Google Cloud Speech-to-Text API.
   - Create a `.env` file in the root directory and add the following:
     ```
     WEATHER_API_KEY=your_weather_api_key
     GOOGLE_CLOUD_SPEECH_API_KEY=your_google_cloud_speech_api_key
     ```

4. Run the app:
   ```bash
   npm start
   ```

5. Open your browser and navigate to `http://localhost:3000` to see the app in action. 🌐

## Usage 📱

1. **Search for a Location:** 🔍
   - Use the search bar to type a city or place name to view weather details for that location.
   - The app also supports voice search 🎤 by clicking the microphone icon and speaking the location.

2. **View Weather Details:** 🌦️
   - The app displays weather information such as temperature, humidity, UV index, and wind speed for the searched location.

3. **Danger Warnings:** ⚠️
   - If the UV index is high, a warning is displayed.
   - The app will alert users to extreme weather conditions like cyclones, storms, and fog based on available data.

4. **Dynamic GUI:** 🌈
   - The background image and icon change based on the weather condition (e.g., rain, snow) and time of day (day or night).


## Contributing 🤝

1. Fork the repository 🍴
2. Create a new branch (`git checkout -b feature-xyz`) 🌱
3. Make your changes and commit them (`git commit -am 'Add feature xyz'`) ✍️
4. Push to the branch (`git push origin feature-xyz`) ⬆️
5. Create a new Pull Request 🔃


## Acknowledgements 🙏

- 🌦️ [Weather API](https://openweathermap.org/api) - For providing the weather data.
- 🎤 [Google Cloud Speech-to-Text API](https://cloud.google.com/speech-to-text) - For implementing voice search.
- 📱 [Bootstrap](https://getbootstrap.com/) - For responsive UI design.

---

Now the README has a bit more flair with the added emojis! You can further customize this as you like! 😊
