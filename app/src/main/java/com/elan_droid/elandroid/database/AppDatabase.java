package com.elan_droid.elandroid.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.elan_droid.elandroid.database.dao.PageDao;
import com.elan_droid.elandroid.database.dao.ProfileDao;
import com.elan_droid.elandroid.database.dao.TripDao;
import com.elan_droid.elandroid.database.dao.UserDao;
import com.elan_droid.elandroid.database.dao.VehicleDao;
import com.elan_droid.elandroid.database.entity.Page;
import com.elan_droid.elandroid.database.entity.Trip;
import com.elan_droid.elandroid.database.entity.User;
import com.elan_droid.elandroid.database.entity.Vehicle;
import com.elan_droid.elandroid.database.model.LotusElanS2;

import java.util.concurrent.Executors;

/**
 * Created by Peter Smith
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = {
        Vehicle.class, User.class, Page.class, Trip.class
    }
)
public abstract class AppDatabase extends RoomDatabase {

    /** the table name used by the database **/
    public static final String DATABASE_NAME = "elandroid";

    /** the default database column name for the primary ID **/
    public static final String COLUMN_ID = "id";

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            ).fallbackToDestructiveMigration()
            .addCallback(new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            getInstance(context).vehicleDao().insert(LotusElanS2.getVehicle());
                        }
                    });
                }
            })
            .build();
        }
        return INSTANCE;
    }

    public abstract VehicleDao vehicleDao();

    public abstract UserDao userVehicleDao();

    public abstract ProfileDao profileDao();

    public abstract PageDao pageDao();

    public abstract TripDao tripDao();

    /**
     * Callback for prepopulating the database with vehicle models
     */
    private static class Prepopulate extends Callback {

        private Context mContext;

        public Prepopulate(final Context context) {
            this.mContext = context;
        }

        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);


        }
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

}
