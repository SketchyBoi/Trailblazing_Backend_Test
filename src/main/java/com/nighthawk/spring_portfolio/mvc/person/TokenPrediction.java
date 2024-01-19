package com.nighthawk.spring_portfolio.mvc.person;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
class LocationData {
    private double latitude;
    private double longitude;
    private String dayOfWeek;
    private double totalDistance;
    private int hoursElapsed;
    private int minutesElapsed;
    private int secondsElapsed;

    public LocationData(double latitude, double longitude, String dayOfWeek,
                        double totalDistance, int hoursElapsed, int minutesElapsed, int secondsElapsed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayOfWeek = dayOfWeek;
        this.totalDistance = totalDistance;
        this.hoursElapsed = hoursElapsed;
        this.minutesElapsed = minutesElapsed;
        this.secondsElapsed = secondsElapsed;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getHoursElapsed() {
        return hoursElapsed;
    }

    public int getMinutesElapsed() {
        return minutesElapsed;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }
}

class GeolocationRewardSystem {
    public double calculateReward(LocationData locationData) {
        double totalDistance = locationData.getTotalDistance();
        int hoursElapsed = locationData.getHoursElapsed();
        int minutesElapsed = locationData.getMinutesElapsed();
        int secondsElapsed = locationData.getSecondsElapsed();

        // Calculate average speed (in meters per second)
        double totalTimeInSeconds = hoursElapsed * 3600 + minutesElapsed * 60 + secondsElapsed;
        double averageSpeed = totalDistance / totalTimeInSeconds;


        System.out.println("Average speed " + averageSpeed);
        // Example: Reward users based on average speed
        double speedReward = calculateSpeedReward(averageSpeed);

        System.out.println("Total distance " + totalDistance);
        // Bonus rewards for distance thresholds
        double distanceBonus = calculateDistanceBonus(totalDistance);
        System.out.println(distanceBonus);

        // Combine the rewards with a weighting factor for each
        double combinedReward = (0.6 * speedReward + 0.4 * distanceBonus);

        return combinedReward;
    }

    private double calculateSpeedReward(double averageSpeed) {
        // Assuming average speed is in meters per second
        if (averageSpeed > 4.5) {
            return 30;  // Reward 30 coins for average speeds greater than 4.5 m/s (faster running)
        } else if (averageSpeed > 3.5) {
            return 23;  // Reward 23 coins for average speeds between 3.5 and 4.5 m/s
        } else if (averageSpeed > 3.0) {
            return 15;  // Reward 15 coins for average speeds between 3.0 and 3.5 m/s
        } else if (averageSpeed > 2.5) {
            return 10;  // Reward 10 coins for average speeds between 2.5 and 3.0 m/s
        } else if (averageSpeed > 2.0) {
            return 4;   // Reward 4 coins for average speeds between 2.0 and 2.5 m/s
        } else {
            return 1;   // Reward 1 coin for average speeds less than 2.0 m/s (walking)
        }
    }

    private double calculateDistanceBonus(double totalDistance) {
        // Bonus rewards based on distance thresholds
        if (totalDistance > 10000.0) {
            return totalDistance * 0.040;
        } else if (totalDistance > 5000.0) {
            return totalDistance * 0.030;
        } else if (totalDistance > 1000.0) {
            return totalDistance * 0.015;
        } else {
            return 0;   // No bonus for distances below the thresholds
        }
    }

    public double distributeReward(LocationData locationData) {
        return calculateReward(locationData);
    }
    
}


public class TokenPrediction {
    public static double parseDoubleOrDefault(String s, double defaultValue) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static int parseIntOrDefault(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
    public static double calculateRewardFromFile(File file) {
        GeolocationRewardSystem rewardSystem = new GeolocationRewardSystem();

        try (Scanner scanner = new Scanner(file)) {
            String lastLine = null;

            while (scanner.hasNextLine()) {
                lastLine = scanner.nextLine();
            }

            if (lastLine != null) {
                String[] parts = lastLine.split(", ");

                // Parse data from the last line
                double latitude = parseDoubleOrDefault(parts[0], 0.0);
                double longitude = parseDoubleOrDefault(parts[1], 0.0);
                String dayOfWeek = parts[2];
                double totalDistance = parseDoubleOrDefault(parts[3], 0.0);
                int hoursElapsed = parseIntOrDefault(parts[4], 0);
                int minutesElapsed = parseIntOrDefault(parts[5], 0);
                int secondsElapsed = parseIntOrDefault(parts[6], 0);

                LocationData locationData = new LocationData(latitude, longitude, dayOfWeek,
                        totalDistance, hoursElapsed, minutesElapsed, secondsElapsed);

                // Calculate reward
                return rewardSystem.calculateReward(locationData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return 0.0; // Default reward if something goes wrong
    }



    public static void main(String[] args) {
        GeolocationRewardSystem rewardSystem = new GeolocationRewardSystem();
    
        // Simulate users with location data
        try {
            // Read data from the text file
            Scanner scanner = new Scanner(new File("src/main/java/com/nighthawk/spring_portfolio/mvc/person/GPS_Data.txt"));
            String lastLine = null;
    
            while (scanner.hasNextLine()) {
                lastLine = scanner.nextLine();
            }
    
            if (lastLine != null) {
                String[] parts = lastLine.split(", ");
    
                // Parse data from the last line
                double latitude = parseDoubleOrDefault(parts[0], 0.0);
                double longitude = parseDoubleOrDefault(parts[1], 0.0);
                String dayOfWeek = parts[2];
                double totalDistance = parseDoubleOrDefault(parts[3], 0.0);
                int hoursElapsed = parseIntOrDefault(parts[4], 0);
                int minutesElapsed = parseIntOrDefault(parts[5], 0);
                int secondsElapsed = parseIntOrDefault(parts[6], 0);
    
                LocationData locationData = new LocationData(latitude, longitude, dayOfWeek,
                        totalDistance, hoursElapsed, minutesElapsed, secondsElapsed);
    
                // Simulate reward distribution
                double reward = rewardSystem.distributeReward(locationData);
                System.out.println("Earned " + reward + " coins.");
            }
    
            // Close the scanner
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
}

