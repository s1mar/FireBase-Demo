package DataModels;

import io.realm.RealmObject;

/**
 * Created by JAPC on 14-05-2017.
 */

public class Image extends RealmObject {

    String name;

    public Image() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
