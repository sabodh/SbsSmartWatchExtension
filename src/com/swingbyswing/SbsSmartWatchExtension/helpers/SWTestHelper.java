package com.swingbyswing.SbsSmartWatchExtension.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import com.swingbyswing.SbsSmartWatchExtension.SWExtensionService;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/28/14
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWTestHelper {


    public static void injectRoundData(final Context context) {
        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                String roundData = "{\n" +
                        "\t\t\t\"path\": \"http://smartwatch/round\",\n" +
                        "\t\t\t\"method\": \"GET\",\n" +
                        "\t\t\t\"data\": {\n" +
                        "\t\t\t\t\"clubShots\": [{\n" +
                        "\t\t\t\t\t\"club\": \"1W\",\n" +
                        "\t\t\t\t\t\"distance\": \"279\"\n" +
                        "\t\t\t\t}],\n" +
                        "\t\t\t\t\"scorecards\": [{\n" +
                        "\t\t\t\t\t\"name\": \"Terrence Giggy\",\n" +
                        "\t\t\t\t\t\"playerId\": 1,\n" +
                        "\t\t\t\t\t\"gross\": 37,\n" +
                        "\t\t\t\t\t\"net\": 27,\n" +
                        "\t\t\t\t\t\"points\": 7,\n" +
                        "\t\t\t\t\t\"par\": 4,\n" +
                        "\t\t\t\t\t\"hcp\": 0,\n" +
                        "\t\t\t\t\t\"teeType\": \"men\",\n" +
                        "\t\t\t\t\t\"scorecardType\": \"stroke play\",\n" +
                        "\t\t\t\t\t\"scorecardDepth\": \"full\"\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"name\": \"Trevor Brown\",\n" +
                        "\t\t\t\t\t\"playerId\": 2,\n" +
                        "\t\t\t\t\t\"gross\": 36,\n" +
                        "\t\t\t\t\t\"net\": 26,\n" +
                        "\t\t\t\t\t\"points\": 6,\n" +
                        "\t\t\t\t\t\"par\": 4,\n" +
                        "\t\t\t\t\t\"hcp\": 0,\n" +
                        "\t\t\t\t\t\"teeType\": \"men\",\n" +
                        "\t\t\t\t\t\"scorecardType\": \"stroke play\",\n" +
                        "\t\t\t\t\t\"scorecardDepth\": \"friend\"\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"name\": \"Ross Middleton\",\n" +
                        "\t\t\t\t\t\"playerId\": 3,\n" +
                        "\t\t\t\t\t\"gross\": 35,\n" +
                        "\t\t\t\t\t\"net\": 25,\n" +
                        "\t\t\t\t\t\"points\": 5,\n" +
                        "\t\t\t\t\t\"par\": 4,\n" +
                        "\t\t\t\t\t\"hcp\": 0,\n" +
                        "\t\t\t\t\t\"teeType\": \"men\",\n" +
                        "\t\t\t\t\t\"scorecardType\": \"stroke play\",\n" +
                        "\t\t\t\t\t\"scorecardDepth\": \"friend\"\n" +
                        "\t\t\t\t}], \n" +
                        "\t\t\t\t\"holes\": [{\n" +
                        "\t\t\t\t\t\"holeNum\": 1,\n" +
                        "\t\t\t\t\t\"par\": 4\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 2,\n" +
                        "\t\t\t\t\t\"par\": 3\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 3,\n" +
                        "\t\t\t\t\t\"par\": 4\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 4,\n" +
                        "\t\t\t\t\t\"par\": 5\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 6,\n" +
                        "\t\t\t\t\t\"par\": 4\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 7,\n" +
                        "\t\t\t\t\t\"par\": 4\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 8,\n" +
                        "\t\t\t\t\t\"par\": 3\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"holeNum\": 9,\n" +
                        "\t\t\t\t\t\"par\": 5\n" +
                        "\t\t\t\t}],\n" +
                        "\t\t\t\t\"clubs\": [{\n" +
                        "\t\t\t\t\t\"name\": \"1 Wood\",\n" +
                        "\t\t\t\t\t\"type\": \"1W\",\n" +
                        "\t\t\t\t\t\"clubId\": 1,\n" +
                        "\t\t\t\t\t\"average\": 215\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"name\": \"3 Wood\",\n" +
                        "\t\t\t\t\t\"type\": \"3W\",\n" +
                        "\t\t\t\t\t\"clubId\": 2,\n" +
                        "\t\t\t\t\t\"average\": 205\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"name\": \"5 Wood\",\n" +
                        "\t\t\t\t\t\"type\": \"5W\",\n" +
                        "\t\t\t\t\t\"clubId\": 3,\n" +
                        "\t\t\t\t\t\"average\": 190\n" +
                        "\t\t\t\t}],\n" +
                        "\t\t\t\t\"isLooper\": true,\n" +
                        "\t\t\t\t\"isAnonymous\": false,\n" +
                        "\t\t\t\t\"measurementType\": \"Y\",\n" +
                        "\t\t\t\t\"holeNum\": 1\n" +
                        "\t\t\t}\n" +
                        "\t\t}";

                Intent intent = new Intent();
                intent.setClass(context, SWExtensionService.class);
                intent.setAction("handle_message");
                intent.putExtra("json_string", roundData);
                context.startService(intent);
            }
        }, 4000);
    }

    public static void injectLocationData(final Context context) {
        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int locationAccuracyType = random.nextInt(3);
                int distance = random.nextInt(300);

                String locationData = "{\n" +
                        "\t\t\t\"path\": \"http://smartwatch/location\",\n" +
                        "\t\t\t\"method\": \"GET\",\n" +
                        "\t\t\t\"data\": {\n" +
                        "\t\t\t\t\"locationAccuracyType\": " + locationAccuracyType + ",\n" +
                        "\t\t\t\t\"isOnNextHole\": false,\n" +
                        "\t\t\t\t\"holeNum\": 1,\n" +
                        "\t\t\t\t\"par\": 4,\n" +
                        "\t\t\t\t\"distance\": " + distance + ",\n" +
                        "\t\t\t\t\"club\": \"1W\",\n" +
                        "\t\t\t\t\"shotDistance\": 145,\n" +
                        "\t\t\t\t\"isSleeping\": false\n" +
                        "\t\t\t}\n" +
                        "\t\t}";

                Intent intent = new Intent();
                intent.setClass(context, SWExtensionService.class);
                intent.setAction("handle_message");
                intent.putExtra("json_string", locationData);
                context.startService(intent);
            }
        }, 3000);
    }
}
