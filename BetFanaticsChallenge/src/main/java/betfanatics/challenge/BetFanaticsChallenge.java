package betfanatics.challenge;

import org.json.simple.JSONObject;

import java.util.List;

// Run this file to see all the results
public class BetFanaticsChallenge {

    public static void main(String[] args) {
        Challenge challenge = new Challenge();
        try {
            // 1-3. get sorted users from page three and print out number of total pages  
            List<JSONObject> users = challenge.getSortedUsersFromPageThree();
            System.out.println("Users sorted by name on page 3 : " + users.toString());
            //4. get the name of the last user
            String lastUser = challenge.getLastUserFromList(users);
            //5. update last user's name to new value and save it
            JSONObject changeNameObject = challenge.updateLastUserNameAndSave(users);
            //6. Delete the user whose name was changed last
            challenge.deleteUserByUserObject(changeNameObject);
            //7. Request for NonExistent user
            int responseStatusFromCall = challenge.getResponseCodeById("5555");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
