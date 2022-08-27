package betfanatics.challenge;

import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.*;

public class Challenge {
    private final String ACCESS_KEY = "ec55b886010d2a10dee93bcf93fe2e3efd315c9dc048c4db0ba6bf0cc2232b5e";

    public Challenge() {
    }

    /**
     * 1-3) get sorted users from page three and logs out the total number of pages
     * @param
     * @return List<JSONObject> list of users sorted
     */
    public List<JSONObject> getSortedUsersFromPageThree() throws Exception {
        // using arraylist to store the user list that we are getting back
        List<JSONObject> userList = new ArrayList<JSONObject>();

        try {

            // use HTTP Url connection in order to make get call and get JSON Object
            // 1) Make the request to get page 3 of the users (?page=3) in the url
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://gorest.co.in/public/v2/users?access-token=" + ACCESS_KEY + "&page=3")
                    .method("GET", null)
                    .addHeader("X-Pagination-Pages", "")
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            JSONParser parser = new JSONParser();

            // 1) Result for get the users from Page 3
            JSONArray usersFromPageThree = (JSONArray) parser.parse(data);

            // 2) print out the pagination total - total number of pages from the previous request
            int paginationTotal = Integer.parseInt(response.headers().get("x-pagination-pages").toString());
            System.out.println(paginationTotal);

            //3) Construct a data structure to hold users and sort them -> return the sorted list of users
            for(int i = 0; i < usersFromPageThree.size(); i++) {
                JSONObject user = (JSONObject) usersFromPageThree.get(i);
                userList.add(user);
            }

            Collections.sort(userList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    return o1.get("name").toString().compareTo(o2.get("name").toString());
                }
            });
        } catch(IOException e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * 4) get the name of the last user
     * @param  users - List<JSONObjects>
     * @return String
     */
    public String getLastUserFromList(List<JSONObject> users) {
        return users.get(users.size()-1).get("name").toString();
    }

    /**
     * 5) update last user's name to new value and save it using a patch call
     * @param  users - List<JSONObjects>
     * @return JSONObject
     */
    public JSONObject updateLastUserNameAndSave(List<JSONObject> users) {
        JSONObject changedNameObj = null;
        int lastUserId = Integer.parseInt(users.get(users.size()-1).get("id").toString());
        System.out.println(lastUserId);
        String urlBuilder = "https://gorest.co.in/public/v2/users/" + lastUserId + "?access-token=" + ACCESS_KEY;

        // Set new name of user and save it as Peter Parker for that user id
        String jsonPayload = " {\n        \"name\": \"Peter Parker\"\n\n}";
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonPayload);
            Request request = new Request.Builder()
                    .url(urlBuilder)
                    .method("PATCH", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            JSONParser parser = new JSONParser();
            changedNameObj = (JSONObject) parser.parse(response.body().string());
        } catch(Exception e){
            e.printStackTrace();
        }

        return changedNameObj;
    }

    /**
     * 6) Delete the user whose name was changed last - takes in an object to delete and
     *    calls the delete method
     * @param objectToDelete JSONObject
     * @return void
     */
    public void deleteUserByUserObject(JSONObject objectToDelete) {
        try {
            String deleteUrl = "https://gorest.co.in/public/v2/users/" + objectToDelete.get("id") +
                    "?access-token=" + ACCESS_KEY;

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(deleteUrl)
                    .method("DELETE", body)
                    .build();
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                System.out.println("User " + objectToDelete.get("id") + " has been successfully deleted");
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 7) Request for NonExistent user - gets the response code for a user id
     * @param userId int
     * @return int responseCode
     */
    public int getResponseCodeById(String userId) {
        String requestUrl = "https://gorest.co.in/public/v2/users/" + userId +
                "?access-token=" + ACCESS_KEY;

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .method("GET", null)
                    .addHeader("X-Pagination-Pages", "")
                    .build();
            Response response = client.newCall(request).execute();
            return response.code();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
