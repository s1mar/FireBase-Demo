package Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Adapters.ImageAdapter;
import DataModels.Image;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import kt.s1.com.kt.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentViewImage extends Fragment {

    ArrayList<File> image_data_files;
    ImageAdapter imageAdapter;



    @BindView(R.id.frag_view_list_view)
    ListView listView;

    private StorageReference mStorageRef;
    private Realm realm;
    public FragmentViewImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_fragment_view_image, container, false);
        ButterKnife.bind(this,view);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        init_getDataFromFirebase();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
    void init_gallery(){
        try {    Log.e("IGO","Inside Image Gallery");

            if (image_data_files == null){
                Log.e("IGO","null");
                image_data_files = new ArrayList<File>();
            }

            if(!image_data_files.isEmpty()){
                Log.e("IGO","init adapter");
                init_adapter();
                return;
            }

            Log.e("IGO","before init adapter");
            File dir_file = new File(getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES.concat("//KT")).toString());
            if (dir_file.exists() && dir_file.isDirectory()) {
                File[] image_files = dir_file.listFiles();
                if (image_files.length <= 0) {
                    // imageUnderFocus.setImageResource(R.drawable.photo_not_available);
                    return;
                }

                for (File file : image_files) {
                    if(file==null || image_data_files==null){
                        Utilities.Helper.showDialog(getActivity(),"Err","Null IGO");
                    }
                    image_data_files.add(file);
                }

                //  init_recycler(v);
            }


        }finally {
            init_adapter();
        }
    }
    void init_getDataFromFirebase() {
        if (image_data_files == null){
            Log.e("IGO1","null");
            image_data_files = new ArrayList<File>();
        }

        RealmResults<Image> dataset = realm.where(Image.class).findAll();
        try {
            for (Image i : dataset) {
                try {
                    final String file_name = i.getName();
                    StorageReference riversRef = mStorageRef.child("images/" + file_name);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.e("FFB", "SS!!!");
                            SaveImage(bytes, file_name);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("FFB", "FF!!!");
                        }
                    });
                } catch (Exception ex) {
                    Log.e("FFB", ex.getMessage());
                    return;
                }

            }
        }
        catch (Exception ex){
            Log.e("FFB1", ex.getMessage());
        }
        finally {
            init_gallery();
        }

    }

    void  SaveImage(byte[] img_data,String file_name) {
        String name = file_name+".jpg";
        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES.concat("//360Sure"));
        File path = new File(getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES.concat("//KT")).toString());
        //File path = new File(getActivity().getApplicationContext().getExternalFilesDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES.concat("//360Sure")).getAbsolutePath()).toString());
        // File file = FileUtils.getFile(path,name_builder[1]);
        File file = new File(path,name);
        if(file.exists() && file.isFile() && !file.isDirectory()){

           // Utilities.Helper.showDialog(getActivity(),"SAVE","File already exists");
            return;

        }

        try {

            boolean isDirCreated =  path.mkdirs();
            if(!isDirCreated){
                //  Utilities.dialog_creator(getActivity(),"SAVE","Unable to create Dirs");
            }

            OutputStream os = new FileOutputStream(file);
            os.write(img_data);
            os.close();

            MediaScannerConnection.scanFile(getActivity(),
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            //MemOps_SET(img_data);
            Utilities.Helper.showDialog(getActivity(),"SAVE","File successfully saved: "+path+"/"+name);


        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
        finally {
            if(imageAdapter!=null){
                imageAdapter.notifyDataSetChanged();
            }
        }
    }





    void init_adapter(){
    if(image_data_files==null || image_data_files.isEmpty()){
    Utilities.Helper.showDialog(getActivity(),"Err","Empty image data files");
    return;
}
        imageAdapter = new ImageAdapter(image_data_files,R.layout.gallery_bottombar,R.id.image_holder);
        listView.setAdapter(imageAdapter);


        image_data_files = null;

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int  position, long id) {
                Utilities.Helper.vibration(getActivity(),0);

                new AlertDialog.Builder(getActivity()).setTitle("Action")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // File file_ref = image_data_files.get(position);
                                File file_ref = imageAdapter.getItem(position);
                                final String file_name = file_ref.getName();
                                try {
                                    FileUtils.forceDelete(file_ref);
                                    //image_data_files.remove(position);
                                    imageAdapter.delete(position);
                                    //imageAdapter.notifyDataSetChanged();
                                }catch (IOException ex){
                                    // unable to delete file from storage;show dialog respectively
                                    new AlertDialog.Builder(getActivity()).setTitle("Error").setMessage("Unable to Delete it from storage")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                finally {

                                    final String[] fname = file_name.split("\\.");

                                    deleteFromFireBase(fname[0]);
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmResults<Image> result = realm.where(Image.class).contains("name",fname[0]).findAll();
                                            result.deleteAllFromRealm();
                                        }
                                    });
                                }
                            }
                        })

                        .setNegativeButton("Do Nothing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();

                return false;
            }
        });

    }

    void deleteFromFireBase(final String file_name){

        StorageReference imgRef = mStorageRef.child("images/"+file_name);
        // Delete the file
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Utilities.Helper.showDialog(getActivity(),"Alert","successfully deleting from cloud,file: "+file_name);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Utilities.Helper.showDialog(getActivity(),"Alert","Error deleting from cloud,file: "+file_name);

            }
        });


    }

    }

