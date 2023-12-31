package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/person")
public class PersonApiController {
    @Autowired
    private PersonJpaRepository repository;

    @Autowired
    private PersonDetailsService personDetailsService;

    /*
    GET List of People
     */
    @GetMapping("/")
    public ResponseEntity<List<Person>> getPeople() {
        return new ResponseEntity<>( repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
    GET individual Person using ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Person person = optional.get();  // value from findByID
            return new ResponseEntity<>(person, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);       
    }

    /*
    DELETE individual Person using ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {  // Good ID
            Person person = optional.get();  // value from findByID
            repository.deleteById(id);  // value from findByID
            return new ResponseEntity<>(person, HttpStatus.OK);  // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
    }

    /*
    POST Aa record by Requesting Parameters from URI
     */
    @PostMapping("/post")
    public ResponseEntity<Object> postPerson(@RequestParam("email") String email,
                                             @RequestParam("password") String password,
                                             @RequestParam("name") String name) {
        // A person object WITHOUT ID will create a new record with default roles as student
        Person person = new Person(email, password, name);
        personDetailsService.save(person);
        return new ResponseEntity<>(email +" is created successfully", HttpStatus.CREATED);
    }

    /*
    The personSearch API looks across database for partial match to term (k,v) passed by RequestEntity body
     */
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> personSearch(@RequestBody final Map<String,String> map) {
        // extract term from RequestEntity
        String term = (String) map.get("term");

        // JPA query to filter on term
        List<Person> list = repository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);

        // return resulting list and status, error checking should be added
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /*
    The personStats API adds stats by Date to Person table 
    */
    @PostMapping("/setStats")
    public ResponseEntity<Person> personStats(@RequestBody final Map<String, String> requestMap, @RequestParam("email") String email) {
        try {
            // Retrieve the user by email instead of ID
            Person person = repository.findByEmail(email);
            // Extract stats data from requestMap and update the user
            Map<String, Object> statMap = new HashMap<>(requestMap);
            statMap.remove("email"); // Remove email from stats data
            person.updateStatsFromJson(statMap);
            repository.save(person);
            return new ResponseEntity<>(person, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/getStats")
    public ResponseEntity<Map<String, Object>> getPersonStats(@RequestParam("email") String email) {
        try {
            // Retrieve the user by email
            Person person = repository.findByEmail(email);

            // Check if the person exists
            if (person == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Get and return the person's stats
            Map<String, Object> stats = person.getStats();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getCheckDay")
    public ResponseEntity<Map<String, Object>> getCheckDay(@RequestParam("email") String email) {
        try {
            // Retrieve the user by email
            Person person = repository.findByEmail(email);

            // Check if the person exists
            if (person == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Get and return the person's checkDay map
            Map<String, Object> checkDay = person.getCheckDay();
            return new ResponseEntity<>(checkDay, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/editCheckDay")
    public ResponseEntity<Object> editCheckDay(@RequestParam("email") String email,
                                               @RequestParam("day") String day,
                                               @RequestParam("value") String valueStr) {
        try {
            // Retrieve the user by email
            Person person = repository.findByEmail(email);
            
            boolean value = Boolean.parseBoolean(valueStr);

            // Check if the person exists
            if (person == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Edit the checkDay map for the specified day
            person.editCheckDay(day, value);

            // Save the updated person to the repository
            repository.save(person);

            return new ResponseEntity<>(day + " successfully changed to " + value, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calculateReward")
    public ResponseEntity<Object> calculateReward(
            @RequestParam("email") String email,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

        try {
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);

            double reward = TokenPrediction.calculateRewardFromFile(tempFile);

            // Retrieve the person by email
            Person person = repository.findByEmail(email);

            // Check if the person exists
            if (person != null) {
                // Add the reward to heldWeeklyTokens
                person.addWeeklyTokens(reward);

                // Save the updated person to the repository
                repository.save(person);

                // Return a response
                return new ResponseEntity<>("Reward for " + email + ": " + reward + " tokens. Updated heldWeeklyTokens: " + person.getHeldWeeklyTokens(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Person with email " + email + " not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getHeldWeeklyTokens")
    public ResponseEntity<Double> getHeldWeeklyTokens(@RequestParam("email") String email) {
        try {
            // Retrieve the person by email
            Person person = repository.findByEmail(email);

            // Check if the person exists
            if (person != null) {
                // Return the heldWeeklyTokens
                return new ResponseEntity<>(person.getHeldWeeklyTokens(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/clearAllUserData")
    public ResponseEntity<String> clearAllUserData() {
        try {
            // Call the service method to clear data for all users
            personDetailsService.clearAllUserData();
            return ResponseEntity.ok("All users' data cleared successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error clearing all users' data: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void scheduleClearAllUserData() {
        clearAllUserData();
    }
}
