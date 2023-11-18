package com.nighthawk.spring_portfolio.mvc.person;

import java.util.HashMap;

public class CounterUpdate {
    private String email;
    private int counter;

    public CounterUpdate(String email) {
        this.email = email;
        this.counter = 0; // Initialize counter to zero
    }


    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setCounter(int add) {
        this.counter = add;
    }

    public int getCounter() {
        return this.counter;
    }

    public String getEmail() {
        return this.email;
    }
}