package com.nighthawk.spring_portfolio.mvc.person;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Convert;
import static jakarta.persistence.FetchType.EAGER;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
Person is a POJO, Plain Old Java Object.
First set of annotations add functionality to POJO
--- @Setter @Getter @ToString @NoArgsConstructor @RequiredArgsConstructor
The last annotation connect to database
--- @Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="person", converter = JsonType.class)
public class Person {

    // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // email, password, roles are key attributes to login and authentication
    @NotEmpty
    @Size(min=5)
    @Column(unique=true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    // @NonNull, etc placed in params of constructor: "@NonNull @Size(min = 2, max = 30, message = "Name (2 to 30 chars)") String name"
    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    // To be implemented
    @ManyToMany(fetch = EAGER)
    private Collection<PersonRole> roles = new ArrayList<>();


    @NonNull
    private double heldWeeklyTokens;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> stats = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> checkDay = new HashMap<>();
    public void editCheckDay(String day, boolean value) {
        checkDay.put(day, value);
    }
    
    public Person(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Long getUserId() {
        return id;
    }

    public void addWeeklyTokens(double tokens) {
        this.heldWeeklyTokens += tokens;
    }

    private void initCheckDay() {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (String day : daysOfWeek) {
            editCheckDay(day, false);
        }
    }

    // Initialize static test data 
    public static Person[] init() {

        // basics of class construction
        Person p1 = new Person();
        p1.setName("Raymond");
        p1.setEmail("hi@gmail.com");
        p1.setPassword("sampletext");
        p1.setHeldWeeklyTokens(0);
        p1.initCheckDay();

        Person p2 = new Person();
        p2.setName("Alexander Graham Bell");
        p2.setEmail("lexb@gmail.com");
        p2.setPassword("123LexB!");
        p2.setHeldWeeklyTokens(0);
        p2.initCheckDay();

        // Array definition and data initialization
        Person persons[] = {p1, p2};
        return(persons);
    }

    public Map<String, Object> getStats() {
        return stats;
    }

    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }

    public void updateStatsFromJson(Map<String, Object> jsonData) {
        for (Map.Entry<String, Object> entry : jsonData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            stats.put(key, value);
        }
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        Person persons[] = init();

        // iterate using "enhanced for loop"
        for( Person person : persons) {
            System.out.println(person);  // print object
        }
    }

}