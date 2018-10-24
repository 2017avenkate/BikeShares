package bikeshares;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class BikeShares {
    //Stores the number of times each pass is used during a specific season
    static void checkMonths(int month, int[] seasonPassCounts, int pass) {
        //Summer
        if (month >= 6 && month <= 8) {
            seasonPassCounts[pass] += 1;
        }
        //Fall
        else if (month >= 9 && month <= 11) {
            seasonPassCounts[pass + 4] += 1;
        }
        //Spring
        else if (month >= 3 && month <= 5) {
            seasonPassCounts[pass + 8] += 1;
        }
        //Winter
        else {
            seasonPassCounts[pass + 12] += 1;
        }
    }
    public static void main(String[] args) {
        HashMap<String, Integer> startStation = new HashMap<String, Integer>();
        HashMap<String, Integer> stopStation = new HashMap<String, Integer>();
        int totalDurationInSec = 0;
        int totalRoundTrips = 0;
        int totalOneWays = 0;
        int totalMonthlyPasses = 0;
        int totalFlexPasses = 0;
        int totalWalkUpPasses = 0;
        int totalStaffAnnualPasses = 0;
        int avgBikingSpeed = 25;
        double totalDistance = 0.0;
        int[] seasonPassCounts = new int[16];
        
        BufferedReader sc = null;
        String line = "";
        String filename = "metro-bike-share-trip-data.csv";
        int x = 0;
        try {
            sc = new BufferedReader(new FileReader(filename));
            while ((line = sc.readLine()) != null) {
                if (x == 0) {
                    x += 1;
                }
                else {
                    String[] curLine = line.split(",");
                    
                    //Stores total duration to find average duration spent on bike shares
                    totalDurationInSec += Integer.parseInt(curLine[1]);
                    
                    //Stores all the start stations to find most popular
                    if (startStation.containsKey(curLine[4])) {
                        startStation.put(curLine[4], startStation.get(curLine[4]) + 1);
                    }
                    else {
                        startStation.put(curLine[4], 1);
                    }
                    
                    //Stores all the stop stations to find most popular
                    if (stopStation.containsKey(curLine[7])) {
                        stopStation.put(curLine[7], stopStation.get(curLine[7]) + 1);
                    }
                    else {
                        stopStation.put(curLine[7], 1);
                    }
                    
                    //Stores total number of round trips and one way trips
                    if (curLine[12].equals("Round Trip")) {
                        //Uses a general biking speed (25 km/hr) to calculate the distance traveled
                        totalDistance += ((double)Integer.parseInt(curLine[1]) / 3600) * avgBikingSpeed;
                        totalRoundTrips += 1;
                    }
                    if (curLine[12].equals("One Way")) {
                        //Finds total distance when both start and end latitudes and longitudes are given
                        double startLat = (!curLine[5].isEmpty()) ? Double.parseDouble(curLine[5]) : 0.0;
                        double startLong = (!curLine[6].isEmpty()) ? Double.parseDouble(curLine[6]) : 0.0;
                        double endLat = (!curLine[8].isEmpty()) ? Double.parseDouble(curLine[8]) : 0.0;
                        double endLong = (!curLine[9].isEmpty()) ? Double.parseDouble(curLine[9]) : 0.0;
                        //Uses the Haversine formula to calculate distance between two lat/long points
                        if (startLat != 0.0 && startLong != 0.0 && endLat != 0.0 && endLong != 0.0) {
                            int R = 6371;
                            double distLat = Math.toRadians(endLat - startLat);
                            double distLong = Math.toRadians(endLong - startLong);
                            double a = Math.sin(distLat / 2) * Math.sin(distLat / 2)
                                + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat))
                                * Math.sin(distLong / 2) * Math.sin(distLong / 2);
                            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                            double distance = R * c;
                            totalDistance += distance;
                        }
                        //Uses a general biking speed (25 km/hr) to calculate the distance traveled
                        else {
                            totalDistance += ((double)Integer.parseInt(curLine[1]) / 3600) * avgBikingSpeed;
                        }
                        
                        totalOneWays += 1;
                    }
                    
                    //Stores total number of each passholder type
                    if (curLine[13].equals("Monthly Pass")) {
                        int month = Integer.parseInt(curLine[2].substring(5, 7));
                        checkMonths(month, seasonPassCounts, 0);
                        totalMonthlyPasses += 1; 
                    }
                    if (curLine[13].equals("Flex Pass")) {
                        int month = Integer.parseInt(curLine[2].substring(5, 7));
                        checkMonths(month, seasonPassCounts, 1);
                        totalFlexPasses += 1;
                    }
                    if (curLine[13].equals("Walk-up")) {
                        int month = Integer.parseInt(curLine[2].substring(5, 7));
                        checkMonths(month, seasonPassCounts, 2);
                        totalWalkUpPasses += 1;
                    }
                    if (curLine[13].equals("Staff Annual")) {
                        int month = Integer.parseInt(curLine[2].substring(5, 7));
                        checkMonths(month, seasonPassCounts, 3);
                        totalStaffAnnualPasses += 1;
                    }
                    x += 1;
                }
            }
            
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (sc != null) {
                try {
                    sc.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        //Data Analysis
        //Pass Types
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("In the data set, there were " + totalMonthlyPasses + " monthly passes, " + totalFlexPasses + " flex passes, " 
            + totalWalkUpPasses + " walk-up passes, and " + totalStaffAnnualPasses + " staff annual passes.");
        System.out.println("Percentagewise, there were " + df.format(((double)totalMonthlyPasses / (x - 1)) * 100) + "% monthly passes, "
            + df.format(((double)totalFlexPasses / (x - 1)) * 100) + "% flex passes, " + df.format(((double)totalWalkUpPasses / (x - 1)) * 100)
            + "% walk up passes, and " + df.format(((double)totalStaffAnnualPasses / (x - 1)) * 100) + "% staff annual passes.\n");
        
        //Round Trips vs One Ways
        System.out.println("In the data set, there were " + totalOneWays + " one way trips and " + totalRoundTrips + " round trips.");
        System.out.println("Percentagewise, there were  " + df.format(((double)totalOneWays / (x - 1)) * 100) + "% one way trips and "
            + df.format(((double)totalRoundTrips / (x - 1)) * 100) + "% round trips. \n");
        
        //Average Duration
        System.out.println("The average duration of renting a bike for a single trip was " + ((double)totalDurationInSec / 60) / (x - 1) + " minutes.\n");
        
        //Most Popular Start Station
        HashMap.Entry<String, Integer> startMax = null;

        for (HashMap.Entry<String, Integer> entry : startStation.entrySet())
        {
            if (startMax == null || entry.getValue().compareTo(startMax.getValue()) > 0)
            {
                startMax = entry;
            }
        }
        
        System.out.println("Station " + startMax.getKey() + " was the most popular start station with a total of " + startMax.getValue() + " visits!");
        
        //Most Popular Stop Station
        HashMap.Entry<String, Integer> stopMax = null;

        for (HashMap.Entry<String, Integer> entry : stopStation.entrySet())
        {
            if (stopMax == null || entry.getValue().compareTo(stopMax.getValue()) > 0)
            {
                stopMax = entry;
            }
        }
        
        System.out.println("Station " + stopMax.getKey() + " was the most popular stop station with a total of " + stopMax.getValue() + " visits!\n");
        
        //Average distance traveled
        System.out.println("The average distance traveled for a single trip was " + df.format(totalDistance / (x - 1)) + " km, or "
            + df.format((totalDistance / (x - 1)) * 0.621371) + " miles.\n");
        
        //Regular Riders
        System.out.println("In total, there were " + ((x - 1) - totalWalkUpPasses) + " riders that either had monthly, flex, or staff annual passes"
            + ", signifying regular bike share riders.\n");
        
        //Seasonal Effect on Passholder Types
        System.out.println("Monthly passes in the summer: " + df.format(
            ((double)seasonPassCounts[0] / (seasonPassCounts[0] + seasonPassCounts[1] + seasonPassCounts[2] + seasonPassCounts[3])) * 100) + "%.");
        System.out.println("Flex passes in the summer: " + df.format(
            ((double)seasonPassCounts[1] / (seasonPassCounts[0] + seasonPassCounts[1] + seasonPassCounts[2] + seasonPassCounts[3])) * 100) + "%.");
        System.out.println("Walk-up passes in the summer: " + df.format(
            ((double)seasonPassCounts[2] / (seasonPassCounts[0] + seasonPassCounts[1] + seasonPassCounts[2] + seasonPassCounts[3])) * 100) + "%.");
        System.out.println("Staff annual passes in the summer: " + df.format(
            ((double)seasonPassCounts[3] / (seasonPassCounts[0] + seasonPassCounts[1] + seasonPassCounts[2] + seasonPassCounts[3])) * 100) + "%.\n");
        
        System.out.println("Monthly passes in the fall: " + df.format(
            ((double)seasonPassCounts[4] / (seasonPassCounts[4] + seasonPassCounts[5] + seasonPassCounts[6] + seasonPassCounts[7])) * 100) + "%.");
        System.out.println("Flex passes in the fall: " + df.format(
            ((double)seasonPassCounts[5] / (seasonPassCounts[4] + seasonPassCounts[5] + seasonPassCounts[6] + seasonPassCounts[7])) * 100) + "%.");
        System.out.println("Walk-up passes in the fall: " + df.format(
            ((double)seasonPassCounts[6] / (seasonPassCounts[4] + seasonPassCounts[5] + seasonPassCounts[6] + seasonPassCounts[7])) * 100) + "%.");
        System.out.println("Staff annual passes in the fall: " + df.format(
            ((double)seasonPassCounts[7] / (seasonPassCounts[4] + seasonPassCounts[5] + seasonPassCounts[6] + seasonPassCounts[7])) * 100) + "%.\n");
        
        System.out.println("Monthly passes in the spring: " + df.format(
            ((double)seasonPassCounts[8] / (seasonPassCounts[8] + seasonPassCounts[9] + seasonPassCounts[10] + seasonPassCounts[11])) * 100) + "%.");
        System.out.println("Flex passes in the spring: " + df.format(
            ((double)seasonPassCounts[9] / (seasonPassCounts[8] + seasonPassCounts[9] + seasonPassCounts[10] + seasonPassCounts[11])) * 100) + "%.");
        System.out.println("Walk-up passes in the spring: " + df.format(
            ((double)seasonPassCounts[10] / (seasonPassCounts[8] + seasonPassCounts[9] + seasonPassCounts[10] + seasonPassCounts[11])) * 100) + "%.");
        System.out.println("Staff annual passes in the spring: " + df.format(
            ((double)seasonPassCounts[11] / (seasonPassCounts[8] + seasonPassCounts[9] + seasonPassCounts[10] + seasonPassCounts[11])) * 100) + "%.\n");
        
        System.out.println("Monthly passes in the winter: " + df.format(
            ((double)seasonPassCounts[12] / (seasonPassCounts[12] + seasonPassCounts[13] + seasonPassCounts[14] + seasonPassCounts[15])) * 100) + "%.");
        System.out.println("Flex passes in the winter: " + df.format(
            ((double)seasonPassCounts[13] / (seasonPassCounts[12] + seasonPassCounts[13] + seasonPassCounts[14] + seasonPassCounts[15])) * 100) + "%.");
        System.out.println("Walk-up passes in the winter: " + df.format(
            ((double)seasonPassCounts[14] / (seasonPassCounts[12] + seasonPassCounts[13] + seasonPassCounts[14] + seasonPassCounts[15])) * 100) + "%.");
        System.out.println("Staff annual passes in the winter: " + df.format(
            ((double)seasonPassCounts[15] / (seasonPassCounts[12] + seasonPassCounts[13] + seasonPassCounts[14] + seasonPassCounts[15])) * 100) + "%.\n");
    }
}
