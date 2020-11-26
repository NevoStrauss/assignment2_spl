package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class ESPNBroadcast implements Broadcast {
    private final String name;

    public ESPNBroadcast(String name) {
        this.name = name;
    }
}
