package ru.spbu.lv;

import jade.core.Agent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DefaultAgent extends Agent {
    private String[] linkedAgents;
    private float number;

    @Override
    protected void setup() {

        int id = Integer.parseInt(getAID().getLocalName());

        if (id == 1) {
            number = 8f;
        } else if (id == 2) {
            number = 22f;
        } else if (id == 3) {
            number = 15f;
        } else if (id == 4) {
            number = 5f;
        } else if (id == 5) {
            number = 0f;
        }

//        Random randomizer = new Random();
//        number = randomizer.nextFloat();

        System.out.println("Agent №" + id + " has number " + number);

        String[][] graph = {
                {"5","2"},
                {"1","3"},
                {"2","4"},
                {"3","5"},
                {"4","1"}
        };

        linkedAgents = graph[id-1];

        addBehaviour(new FindAverageBehaviour(this, TimeUnit.SECONDS.toMillis(1)));
    }

    public float getNumber() {
        return number;
    }

    public String[] getLinkedAgents() {
        return linkedAgents;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public void setLinkedAgents(String[] linkedAgents) {
        this.linkedAgents = linkedAgents.clone();
    }
}

