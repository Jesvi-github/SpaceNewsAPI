package com.spacenews.spacenews;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SpaceNewsApi {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String query;
            do {
                // Retrieve user input
                System.out.println("Welcome To Space News API");
                System.out.print("Search Here For Some Space News (type No to Quit): ");
                query = scanner.nextLine();

                if (query.equalsIgnoreCase("No")) break;

                // Get space news data
                JSONArray newsArticles = getSpaceNews(query);

                // Display news articles
                if (newsArticles != null) {
                    System.out.println("Space News Articles:");
                    for (Object articleObj : newsArticles) {
                        JSONObject article = (JSONObject) articleObj;
                        JSONArray data = (JSONArray) article.get("data");

                        // Extract title and description from the data array
                        for (Object dataObj : data) {
                            JSONObject dataItem = (JSONObject) dataObj;
                            String title = (String) dataItem.get("title");
                            String description = (String) dataItem.get("description");

                            // Print the article's title and description
                            System.out.println("Title: " + title);
                            System.out.println("Description: " + description);
                            System.out.println("-------------------------------------------");
                        }
                    }
                }
            } while (!query.equalsIgnoreCase("No"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getSpaceNews(String query) {
        String urlString = "https://images-api.nasa.gov/search?q=" + query + "&media_type=image";

        try {
            // Fetch the API response based on the API URL
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            // Check for response status
            if (apiConnection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API. Response code: " + apiConnection.getResponseCode());
                return null;
            }

            // Read the response and convert to String
            String jsonResponse = readApiResponse(apiConnection);

            // Print the raw response to check its structure
            System.out.println("API Response: " + jsonResponse);

            // Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            // Retrieve the collection of items
            JSONObject collection = (JSONObject) jsonObject.get("collection");
            return (JSONArray) collection.get("items");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConnection) {
        try {
            // Create a StringBuilder to store the resulting JSON data
            StringBuilder resultJson = new StringBuilder();

            // Create a Scanner to read from the InputStream of the HttpURLConnection
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            // Loop through each line in the response and append it to the StringBuilder
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            // Close the Scanner to release resources
            scanner.close();

            return resultJson.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            conn.setRequestMethod("GET");

            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
