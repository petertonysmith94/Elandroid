package com.elan_droid.elandroid.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.dao.CommandDao;
import com.elan_droid.elandroid.database.dao.FlagDao;
import com.elan_droid.elandroid.database.dao.PacketDao;
import com.elan_droid.elandroid.database.dao.PacketFlagDao;
import com.elan_droid.elandroid.database.dao.PageDao;
import com.elan_droid.elandroid.database.dao.PageItemDao;
import com.elan_droid.elandroid.database.dao.ParameterDao;
import com.elan_droid.elandroid.database.dao.ProfileDao;
import com.elan_droid.elandroid.database.dao.TripDao;
import com.elan_droid.elandroid.database.dao.UserDao;
import com.elan_droid.elandroid.database.dao.VehicleDao;
import com.elan_droid.elandroid.database.entity.FlagFormatted;
import com.elan_droid.elandroid.database.entity.Packet;
import com.elan_droid.elandroid.database.entity.ParameterFormatted;
import com.elan_droid.elandroid.database.entity.Message;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.PageItem;
import com.elan_droid.elandroid.database.entity.ParameterBitwise8;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.entity.Vehicle;
import com.elan_droid.elandroid.database.prepopulate.LotusElanS2;
import com.elan_droid.elandroid.database.prepopulate.PrepopulateModel;

import java.util.concurrent.Executors;

/**
 * Created by Peter Smith
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = {
        Vehicle.class, User.class, Message.class, Page.class, Trip.class, Packet.class,
            ParameterBitwise8.class, ParameterFormatted.class, PageItem.class,
            FlagFormatted.class
    }
)
public abstract class AppDatabase extends RoomDatabase {

    public static final int NEW_ENTITY_ID = 0;

    /** the table name used by the database **/
    public static final String DATABASE_NAME = "elandroid";

    /** the default database column name for the primary ID **/
    public static final String COLUMN_ID = "id";

    private static AppDatabase INSTANCE;

    private static final PrepopulateModel[] MODELS = new PrepopulateModel[] {
        new LotusElanS2 ()
    };

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .addCallback(new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase database = getInstance(context);

                            for (PrepopulateModel model : MODELS) {
                                database.vehicleDao().baseInsert(model.getVehicle());
                                database.commandDao().insertCommands(model.getCommands());
                            }
                        }
                    });
                }
            })
            .build();
        }
        return INSTANCE;
    }


    // Relationship dao s
    public abstract ProfileDao profileDao();

    public abstract CommandDao commandDao();

    public abstract PacketFlagDao packetFlagDao();


    // Base entity dao s
    public abstract VehicleDao vehicleDao();

    public abstract UserDao userVehicleDao();

    public abstract PageDao pageDao();

    public abstract TripDao tripDao();

    public abstract PacketDao packetDao();

    public abstract ParameterDao parameterDao();

    public abstract PageItemDao pageItemDao();

    public abstract FlagDao flagDao();


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

}
