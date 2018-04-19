package com.elan_droid.elandroid.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.elan_droid.elandroid.database.embedded.Request;
import com.elan_droid.elandroid.database.embedded.Response;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Peter Smith
 */
@Entity(
    tableName = Message.TABLE_NAME,
    foreignKeys = @ForeignKey (
        entity = Vehicle.class,
        parentColumns = Vehicle.COLUMN_ID,
        childColumns = Vehicle.REFERENCE_COLUMN_ID,
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
)
public class Message {

    public final static String TABLE_NAME = "message";
    public final static String COLUMN_ID = "messageId";
    public final static String REFERENCE_COLUMN_ID = "message_id";

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo (name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long id;

    @ColumnInfo (name = Vehicle.REFERENCE_COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    private long vehicleId;

    @NonNull
    @Embedded
    private Request request;

    @Nullable
    @Embedded
    private Response response;

    public Message (long id, long vehicleId, @NonNull Request request, @Nullable Response response) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.request = request;
        this.response = response;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    @NonNull
    public Request getRequest() {
        return request;
    }

    public void setRequest(@NonNull Request request) {
        this.request = request;
    }

    @Nullable
    public Response getResponse() {
        return response;
    }

    public void setResponse(@NonNull Response response) {
        this.response = response;
    }

}
