package com.elan_droid.elandroid.database.data.relation;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import com.elan_droid.elandroid.database.data.embedded.Request;
import com.elan_droid.elandroid.database.data.embedded.Response;
import com.elan_droid.elandroid.database.data.entity.Message;
import com.elan_droid.elandroid.database.data.entity.Parameter;
import com.elan_droid.elandroid.database.data.entity.ParameterStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Peter Smith
 *
 * A command represents a message and it's parameters used to format data.
 */
public class Command {

    @Embedded
    private Message message;

    @Relation (
        parentColumn = Message.COLUMN_ID,
        entityColumn = Message.REFERENCE_COLUMN_ID
    )
    private List<Parameter> parameters;

    @Ignore
    private long tripId;

    public Command (Message message, Parameter[] parameters) {
        this (message, Arrays.asList(parameters));
    }

    public Command (Message message, List<Parameter> parameters) {
        this.message = message;
        this.parameters = parameters;

        List<ParameterStream> streamParameters = new ArrayList<>();

        for (Parameter p : parameters) {
            if (p instanceof ParameterStream) {
                streamParameters.add((ParameterStream) p);
            }
        }

        this.message.getResponse().setStreamParameters(streamParameters);
    }

    public Message getMessage() {
        return message;
    }

    protected void setMessage(Message message) {
        this.message = message;
    }

    public long getMessageId() {
        return message.getId();
    }

    public Request getRequest() {
        return message.getRequest();
    }

    public Response getResponse() {
        return message.getResponse();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Parameter[] getParametersArray() {
        Parameter[] tmp = new Parameter[parameters.size()];
        parameters.toArray(tmp);
        return tmp;
    }

    protected void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public long getTripId() {
        return tripId;
    }

    protected void setTripId(long tripId) {
        this.tripId = tripId;
    }
}
