package com.example.umeyesdk.entity;


import java.io.Serializable;

public class ReqDevAbilityLevel implements Serializable {

    private int Operation = 109;
    private int Request_Type = 0;
    private ValueBean Value;

    public int getOperation() {
        return Operation;
    }

    public void setOperation(int Operation) {
        this.Operation = Operation;
    }

    public int getRequest_Type() {
        return Request_Type;
    }

    public void setRequest_Type(int Request_Type) {
        this.Request_Type = Request_Type;
    }

    public ValueBean getValue() {
        return Value;
    }

    public void setValue(ValueBean value) {
        Value = value;
    }

    public static class ValueBean implements Serializable {
        /**
         * channel : -1
         */
        private int channel = -1;

        public ValueBean() {
        }

        public ValueBean(int channel) {
            this.channel = channel;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }
    }
}
