package com.lab3.lab3_game.UserAccount;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lab3.lab3_game.DataBase.ModelFirebaseDatabase;
import com.lab3.lab3_game.DataBase.ModelFirebaseStorage;
import com.lab3.lab3_game.DataBase.RegistrateUser;
import com.lab3.lab3_game.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserPageViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isGravatar = new MutableLiveData<>();
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> image = new MutableLiveData<>();
    private final MutableLiveData<Uri> imgUri = new MutableLiveData<>();
    private final MutableLiveData<String> outputMessage = new MutableLiveData<>();
    private final MutableLiveData<String> dialogMessage = new MutableLiveData<>();

    RegistrateUser firebaseAuth;
    ModelFirebaseDatabase firebaseDatabase;
    ModelFirebaseStorage firebaseStorage;

    String pathToRoom;

    public UserPageViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new RegistrateUser();
        firebaseDatabase = new ModelFirebaseDatabase();
        firebaseStorage = new ModelFirebaseStorage();
        pathToRoom = "Users/" + firebaseAuth.getUIDUser();
    }

    public LiveData<Boolean> isGravatar() {
        return isGravatar;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getOutputMessage() {
        return outputMessage;
    }

    public LiveData<String> getDialogMessage() {
        return dialogMessage;
    }

    public LiveData<String> getImage() {
        return image;
    }

    public LiveData<Uri> getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri Image) {
        imgUri.setValue(Image);
    }

    public void setInformation() {
        firebaseDatabase.getRef(pathToRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (Objects.equals(child.getKey(), "Gravatar")) {
                        isGravatar.setValue(Boolean.parseBoolean(Objects.requireNonNull(child.getValue()).toString()));
                    } else if (Objects.equals(child.getKey(), "userName")) {
                        userName.setValue(Objects.requireNonNull(child.getValue()).toString());
                    } else {
                        image.setValue(Objects.requireNonNull(child.getValue()).toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setNewName(final String newName) {
        if (!newName.isEmpty()) {
            Query query = firebaseDatabase.getRef("Users").orderByChild("userName").equalTo(newName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        firebaseDatabase.setValue(pathToRoom + "/userName", newName);
                        outputMessage.setValue(getApplication().getString(R.string.Changed_successfully));
                    } else {
                        outputMessage.setValue(getApplication().getString(R.string.name_already_exists));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            outputMessage.setValue(getApplication().getString(R.string.Empty));
        }
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public void uploadImage() {
        Uri imageUri = imgUri.getValue();
        if (imageUri != null) {
            StorageReference ref = firebaseStorage.getRef(UserPageActivity.STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imageUri));
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    outputMessage.setValue(UserPageViewModel.this.getApplication().getString(R.string.Image_uploaded));
                    Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("Image", uri.toString());
                            firebaseDatabase.updateChild(pathToRoom, childUpdates);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            outputMessage.setValue(e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialogMessage.setValue("Uploaded" + (int) progress + "%");
                        }
                    });
        } else {
            outputMessage.setValue(getApplication().getString(R.string.Select_image));
        }
    }


    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getApplication().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void changeButton(boolean gravatar) {
        Map<String, Object> childUpdates = new HashMap<>();
        if (gravatar) {
            String hash = md5(firebaseAuth.getEmail());
            String gravatarUrl = "https://s.gravatar.com/avatar/" + hash + "?s=80";
            image.setValue(gravatarUrl);
            childUpdates.put("Image", gravatarUrl);
            childUpdates.put("Gravatar", true);
        } else {
            String urlImage = "https://firebasestorage.googleapis.com/v0/b/playgamekl.appspot.com/o/image%2F1606652344403.png?alt=media&token=e5c51ce4-3939-490e-9d4f-fc5ddeef127e";
            image.setValue(urlImage);
            childUpdates.put("Image", urlImage);
            childUpdates.put("Gravatar", false);
        }
        firebaseDatabase.updateChild(pathToRoom, childUpdates);
    }

    private String md5(String in) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            BigInteger bigInt = new BigInteger(1, digest.digest());
            result = bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
