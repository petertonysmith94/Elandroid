package com.elan_droid.elandroid.database.data.relation;

import com.elan_droid.elandroid.database.data.entity.Parameter;

/**
 * Created by Peter Smith on 4/24/2018.
 *
 *
 **/
public class ParameterFlag {

    private static final String DEFAULT_TEXT = "-";

    //
    private Parameter parameter;


    private String value;


    public ParameterFlag(Parameter parameter) {
        this.parameter = parameter;
        this.value = DEFAULT_TEXT;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals (long parameterId) {
        return parameter.getId() == parameterId;
    }

    @Override
    public boolean equals(Object o) {
        if (parameter.getId() == (long) o)
            return true;

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ParameterFlag that = (ParameterFlag) o;

        return parameter != null ? parameter.getId() == that.getParameter().getId() : that.parameter == null;

    }

    @Override
    public int hashCode() {
        int result = parameter != null ? parameter.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
