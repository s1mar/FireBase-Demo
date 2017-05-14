package kt;
import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by JAPC on 14-05-2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init_Realm();
    }

    void init_Realm(){

        Realm.init(this);

        RealmConfiguration config1 = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("pd")   //primary driver
                .build();


        RealmConfiguration config2 = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("sd")   //primary driver
                .build();


        Realm.setDefaultConfiguration(config1);
    }



}
