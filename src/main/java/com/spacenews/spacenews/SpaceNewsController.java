package com.spacenews.spacenews;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SpaceNewsController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> newsListView;

    @FXML
    private Button loadMoreButton;

    private JSONArray allNews; // To store all fetched news articles
    private int newsIndex = 0; // To keep track of the current batch of displayed news
    private static final int PAGE_SIZE = 5; // Number of articles to load per batch

    // Fetch news based on the search field input
    @FXML
    private void fetchNews() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            newsListView.getItems().add("Please enter a search query!");
            return;
        }

        // Fetch news articles from the API
        JSONArray newsArticles = getSpaceNews(query);
        if (newsArticles != null) {
            newsListView.getItems().clear(); // Clear previous search results
            allNews = newsArticles; // Store all news articles
            newsIndex = 0; // Reset the index
            loadMoreNews(); // Load the first batch of news
        } else {
            newsListView.getItems().add("No articles found or error occurred.");
        }
    }

    // Load more news articles when the "Load More" button is clicked
    @FXML
    private void loadMoreNews() {
        if (allNews == null || allNews.size() == 0) return;

        int endIndex = Math.min(newsIndex + PAGE_SIZE, allNews.size());
        for (int i = newsIndex; i < endIndex; i++) {
            JSONObject article = (JSONObject) allNews.get(i);
            JSONArray data = (JSONArray) article.get("data");

            for (Object dataObj : data) {
                JSONObject dataItem = (JSONObject) dataObj;
                String title = (String) dataItem.get("title");

                // Add title to the ListView
                newsListView.getItems().add("Title: " + title);
            }
        }

        // Update the index for the next batch
        newsIndex = endIndex;

        // Disable "Load More" button if all articles are loaded
        if (newsIndex >= allNews.size()) {
            loadMoreButton.setDisable(true); // Disable the button when all news is loaded
        }
    }

    // Fetch news from NASA API
    private JSONArray getSpaceNews(String query) {
        String urlString = "https://images-api.nasa.gov/search?q=" + query + "&media_type=image";

        try {
            HttpURLConnection apiConnection = fetchApiResponse(urlString);
            if (apiConnection.getResponseCode() != 200) {
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            JSONObject collection = (JSONObject) jsonObject.get("collection");
            return (JSONArray) collection.get("items");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read the API response
    private String readApiResponse(HttpURLConnection apiConnection) {
        try (Scanner scanner = new Scanner(apiConnection.getInputStream())) {
            StringBuilder resultJson = new StringBuilder();
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            return resultJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Establish API connection
    private HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Handle news item click to show more details
    @FXML
    private void onNewsItemClick() {
        String selectedItem = newsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && allNews != null) {
            for (Object newsObject : allNews) {
                JSONObject article = (JSONObject) newsObject;
                JSONArray dataArray = (JSONArray) article.get("data");

                for (Object dataObj : dataArray) {
                    JSONObject dataItem = (JSONObject) dataObj;
                    String title = (String) dataItem.get("title");
                    String description = (String) dataItem.get("description");

                    // Assuming the image URL is in the `links` array
                    JSONArray links = (JSONArray) article.get("links");
                    String imageUrl = null;
                    if (links != null && links.size() > 0) {
                        JSONObject linkObject = (JSONObject) links.get(0);
                        imageUrl = (String) linkObject.get("href");
                    }

                    if (selectedItem.contains(title)) {
                        showNewsDetails(title, description, imageUrl);
                        return;
                    }
                }
            }
        }
    }

    // Show detailed information in a new window
    private void showNewsDetails(String title, String description, String imageUrl) {
        Stage detailStage = new Stage();
        detailStage.setTitle("News Details");

        Label titleLabel = new Label("Title: " + title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");

        Label descriptionLabel = new Label("Description: " + description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        VBox detailBox = new VBox(10);
        detailBox.getChildren().addAll(titleLabel, descriptionLabel);

        if (imageUrl != null) {
            ImageView newsImage = new ImageView(new Image(imageUrl));
            newsImage.setFitWidth(300);
            newsImage.setPreserveRatio(true);
            detailBox.getChildren().add(newsImage);
        }

        detailBox.setStyle("-fx-padding: 20; -fx-background-color: white;");
        Scene detailScene = new Scene(detailBox, 400, 400);
        detailStage.setScene(detailScene);
        detailStage.show();
    }
}
