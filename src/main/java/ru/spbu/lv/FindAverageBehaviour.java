package ru.spbu.lv;

import java.util.Objects;
import java.util.Random;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;



public class FindAverageBehaviour extends TickerBehaviour {
    private final DefaultAgent agent;
    private int currentStep;
    private final int MAX_STEPS = 20;

    FindAverageBehaviour(DefaultAgent agent, long period) {
        super(agent, period);
        this.setFixedPeriod(true);
        this.agent = agent;
        this.currentStep = 0;
    }


    @Override
    public void onTick() {

        int neighbors_n = this.agent.getLinkedAgents().length;

        // set noise
        Random noise_randomizer = new Random();
        float[] noise = new float[neighbors_n];
        for (int i=0; i<noise.length; i++)
            noise[i] = (float) noise_randomizer.nextGaussian() / 100f;

        // set presence / absence of communication with neighbors
        boolean[] alive_links = new boolean[neighbors_n];
        for (int i =0; i < alive_links.length; i++)
            alive_links[i] = noise_randomizer.nextBoolean();

        if (currentStep < MAX_STEPS) {

            // messages sending
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setSender(this.agent.getAID());
            for(int i=0; i < neighbors_n; i++) {
                if (alive_links[i])
                    msg.addReceiver(new AID(this.agent.getLinkedAgents()[i], AID.ISLOCALNAME));
                    msg.setContent(Objects.toString(this.agent.getNumber() + noise[i]));
                    this.agent.send(msg);
            }

            // messages receiving and neighbors' values sum calculation
            float sum_neighbours = 0;
            int amount = 0;
            while (this.agent.getCurQueueSize() > 0 && amount < 2){
                ACLMessage received_message = this.agent.receive();
                sum_neighbours += Float.parseFloat(received_message.getContent());
                amount++;
            }

            // step size
            float a = 1f / (1f + (float) neighbors_n);

            // value update
            float current_number = this.agent.getNumber() + a * (sum_neighbours -  amount * this.agent.getNumber());
            this.agent.setNumber(current_number);

            currentStep++;
        } else {
            System.out.println("Average of agent №" + this.agent.getLocalName() + " is "+ this.agent.getNumber());
            this.stop();
        }
    }
}
