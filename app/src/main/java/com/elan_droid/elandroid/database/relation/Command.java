package com.elan_droid.elandroid.database.relation;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.Parameter;

import java.util.ArrayList;
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

    public Command (Message message, Parameter[] parameters) {
        this.message = message;
        this.parameters = new ArrayList<>(parameters.length);
        for (Parameter p : parameters) {
            this.parameters.add(p);
        }
    }

    public Command (Message message, List<Parameter> parameters) {
        this.message = message;
        this.parameters = parameters;
    }

    public void format () {

    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Parameter[] getParametersArray() {
        Parameter[] tmp = new Parameter[parameters.size()];
        parameters.toArray(tmp);
        return tmp;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

}
