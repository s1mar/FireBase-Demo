package Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Fragment;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


import DataModels.Image;
import butterknife.BindView;
import butterknife.ButterKnife;
;
import io.realm.Realm;
import io.realm.RealmResults;
import kt.s1.com.kt.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetImage extends Fragment {

    @BindView(R.id.setImg_img_view)
    ImageView img_view;
    @BindView(R.id.setImg_btn_view)
    Button fb_btn;
    private StorageReference mStorageRef;

    Uri globalUri;

    private Realm realm;

    public FragmentSetImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_fragment_set_image, container, false);
        ButterKnife.bind(this,view);
        init_element_listners();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    void init_element_listners(){
        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_image();
            }
        });
        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase();
            }
        });

    }
    void add_image(){
        globalUri = null;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                img_view.setImageBitmap(bitmap);
                globalUri = uri;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void uploadToFirebase() {

        if (globalUri == null) {
            return;
        }
        String[] res_identifier = globalUri.toString().split("%");
        final String file_name = res_identifier[1];

        StorageReference imageRef = mStorageRef.child("images/"+file_name);

        imageRef.putFile(globalUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(alreadyExistsInRealm(file_name)){
                            Utilities.Helper.showDialog(getActivity(),"Alert","img already on the cloud");
                            return;
                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Image entity = realm.createObject(Image.class);
                                entity.setName(file_name);
                            }
                        });
                        realmTest();
                        Utilities.Helper.showDialog(getActivity(),"Success","img upload successful,link: "+ downloadUrl.toString());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Utilities.Helper.showDialog(getActivity(),"Failure","Couldnt upload image");
                    }
                });
    }

void realmTest(){

    RealmResults<Image> results1 = realm.where(Image.class).findAll();

    for(Image image:results1) {
        Log.d("results1", image.getName());
    }

}

    boolean alreadyExistsInRealm(String file_name){

        RealmResults<Image> results = realm.where(Image.class).contains("name",file_name).findAll();
        if(results.isEmpty()){
            return false;
        }
        return true;

    }

}
