package betfanatics.challenge;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.*;

/*

This is the testing file for the Challenge Class, it should be able to call the methods from the Challenge
and execute the outputs accordingly.

1-3. get sorted users from page three
List<JSONObject> users = getSortedUsersFromPageThree();

4. get the name of the last user
String lastUser = getLastUserFromList(users);

5. update last user's name to new value and save it
JSONObject changeNameObject = updateLastUserNameAndSave(users);

6. Delete the user whose name was changed last
deleteUserByUserObject(changeNameObject);

7. Request for NonExistent user
int responseStatusFromCall = getResponseCodeById("5555");

 */

public class ChallengeTest extends TestCase {

    @Test
    @DisplayName("Test to get Sorted Users from Page 3 and print total # of pages")
    public void testGetSortedUsersFromPageThree() {
        Challenge challenge = new Challenge();
        List<JSONObject> userList = null;
        try {
            userList  = challenge.getSortedUsersFromPageThree();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // use private method to mock API call and see if the two lists are matching
        String url = "https://gorest.co.in/public/v2/users?access-token=ec55b886010d2a10dee93bcf93fe2e3efd315c9dc048c4db0ba6bf0cc2232b5e&page=3";
        List<JSONObject> test = getUsersFromUrl(url);

        // check if both lists are matching and check if both numbers printed out are the same
        Assert.assertEquals(userList, test);
    }

    @Test
    @DisplayName("Test to get the last user from a list of users")
    public void testGetLastUserFromList() {
        Challenge challenge = new Challenge();
        List<JSONObject> userList = null;
        String lastUser = "";
        try {
            userList  = challenge.getSortedUsersFromPageThree();
            // 4. Logs out the last user after the sorting 
            lastUser = challenge.getLastUserFromList(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "https://gorest.co.in/public/v2/users?access-token=ec55b886010d2a10dee93bcf93fe2e3efd315c9dc048c4db0ba6bf0cc2232b5e&page=3";
        List<JSONObject> test = getUsersFromUrl(url);
        String lastUserTest = test.get(test.size()-1).get("name").toString();

        Assert.assertEquals(lastUser, lastUserTest);
    }

    @Test
    @DisplayName("Test to update the last user name and see if the names are matching accordingly with mock get call")
    public void testUpdateLastUserNameAndSave() {
        Challenge challenge = new Challenge();
        List<JSONObject> userList = null;
        JSONObject changedUserName = null;
        String lastUser = "";
        try {
            userList  = challenge.getSortedUsersFromPageThree();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "https://gorest.co.in/public/v2/users?access-token=ec55b886010d2a10dee93bcf93fe2e3efd315c9dc048c4db0ba6bf0cc2232b5e&page=3";
        List<JSONObject> test = getUsersFromUrl(url);

        JSONObject lastObject = test.get(test.size()-1);
        lastObject.put("name", "Peter Parker");
        changedUserName = challenge.updateLastUserNameAndSave(userList);

        Assert.assertEquals(changedUserName, lastObject);

        // check with a get call as well
        String changedId = changedUserName.get("id").toString();
        String changedName = getUserNameById(changedId);
        Assert.assertEquals(changedUserName.get("name").toString(), changedName);
    }

    @Test
    @DisplayName("Deletes a user and then mocks a get call to see if user is deleted - return 404")
    public void testDeleteUserByUserObject() {
        Challenge challenge = new Challenge();
        List<JSONObject> userList = null;
        JSONObject changedUserName = null;
        String idToCheck = "";
        try {
            userList  = challenge.getSortedUsersFromPageThree();
            changedUserName = challenge.updateLastUserNameAndSave(userList);
            idToCheck = changedUserName.get("id").toString();
            challenge.deleteUserByUserObject(changedUserName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "https://gorest.co.in/public/v2/users/" + idToCheck +
                "?access-token=ec55b886010d2a10dee93bcf93fe2e3efd315c9dc048c4db0ba6bf0cc2232b5e";


        int response = getResponseCodeForGetUserById(url);
        Assert.assertEquals(response, 404);
    }

    @Test
    @DisplayName("Test for the deleted user and check if it returns 404 status code")
    public void testGetUserById() {
        Challenge challenge = new Challenge();
        int responseCode = challenge.getResponseCodeById("5555");
        Assert.assertEquals(responseCode, 404);
    }

    // Private function to mock get user from url call for sorted user testing
    private List<JSONObject> getUsersFromUrl(String url) {
        List<JSONObject> userList = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("X-Pagination-Pages", "")
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            JSONParser parser = new JSONParser();

            JSONArray users = (JSONArray) parser.parse(data);
            System.out.println(response.headers().get("x-pagination-pages").toString());

            for(int i = 0; i < users.size(); i++) {
                JSONObject user = (JSONObject) users.get(i);
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
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return userList;
    }

    // private function for getting username by id for checking if username changed
    private String getUserNameById(String changedId) {

        String url = "https://gorest.co.in/public/v2/users/" + changedId +
                "?access-token=ec55b886010d2a10dee93bcf93fe2e3efd315c9dc048c4db0ba6bf0cc2232b5e";
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("X-Pagination-Pages", "")
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            System.out.println(data);
            JSONParser parser = new JSONParser();
            JSONObject user = (JSONObject) parser.parse(data);
            return user.get("name").toString();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    // private function to get response status from a mock get call
    private int getResponseCodeForGetUserById(String url) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("X-Pagination-Pages", "")
                    .build();
            Response response = client.newCall(request).execute();
            return response.code();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}