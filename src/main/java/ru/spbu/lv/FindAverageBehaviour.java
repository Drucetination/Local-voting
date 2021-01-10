package ru.spbu.lv;

import java.util.Objects;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;



public class FindAverageBehaviour extends TickerBehaviour {
    private final DefaultAgent agent;
    private int currentStep;
    private final int MAX_STEPS = 40;

    FindAverageBehaviour(DefaultAgent agent, long period) {
        super(agent, period);
        this.setFixedPeriod(true);
        this.agent = agent;
        this.currentStep = 0;
    }


    @Override
    public void onTick() {

        System.out.println(this.getTickCount());

        int neighbours_n = this.agent.getLinkedAgents().length;

        if (currentStep < MAX_STEPS) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setSender(this.agent.getAID());
            for(int i=0; i < neighbours_n; i++)
                msg.addReceiver(new AID(this.agent.getLinkedAgents()[i], AID.ISLOCALNAME));

            msg.setContent(Objects.toString(this.agent.getNumber()));
            this.agent.send(msg);

            ACLMessage[] received_messages = new ACLMessage[this.agent.getCurQueueSize()];
            for (int i=0; i<received_messages.length; i++){
                received_messages[i] = this.agent.blockingReceive();
            }
            float a = 1f / (1f + (float) received_messages.length);

            float sum_neighbours = 0;
            for (ACLMessage received_message : received_messages) {
                sum_neighbours += Float.parseFloat(received_message.getContent());
            }

            float current_number = a * (this.agent.getNumber() + sum_neighbours);
            this.agent.setNumber(current_number);

            currentStep++;
        } else {
            System.out.println("Average of agent №" + this.agent.getLocalName() + " is "+ this.agent.getNumber());
            this.stop();
        }
    }
}
