package com.globalbit.tellyou.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.model.Contact;
import com.globalbit.tellyou.model.Phone;
import com.globalbit.tellyou.model.Picture;
import com.globalbit.tellyou.network.requests.ContactsRequest;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

/**
 * Created by alex on 26/11/2017.
 */

public class ObservableHelper {

    public static Observable<String> imageObservable(final Context context, final Uri uri) {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override public ObservableSource<? extends String> call() throws Exception {
                InputStream image_stream =null;
                try {
                    image_stream=context.getContentResolver().openInputStream(uri);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap= BitmapFactory.decodeStream(image_stream );
                String encoded="";
                if(bitmap!=null) {
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                    byte[] byteArray=byteArrayOutputStream.toByteArray();
                    encoded=Base64.encodeToString(byteArray, Base64.DEFAULT);
                    bitmap.recycle();
                }
                return Observable.just(encoded);
            }
        });
    }

    public static Observable<String> mergeVideoFilesObservable(final ArrayList<String> sourceFiles, final String targetFile) {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override public ObservableSource<? extends String> call() throws Exception {
                String videoPath="";
                Log.i("mergeVideoFiles", "mergeMediaFiles: "+targetFile);
                try {
                    //String mediaKey = isAudio ? "soun" : "vide";
                    List<Movie> listMovies = new ArrayList<>();
                    for (String filename : sourceFiles) {
                        listMovies.add(MovieCreator.build(filename));
                    }
                    //List<Track> listTracks = new LinkedList<>();
                    List<Track> videoTracks = new LinkedList<>();
                    List<Track> audioTracks = new LinkedList<>();
                    for (Movie movie : listMovies) {
                        for (Track track : movie.getTracks()) {
                            if (track.getHandler().equals("soun")) {
                                audioTracks.add(track);
                            }
                            if (track.getHandler().equals("vide")) {
                                videoTracks.add(track);
                            }
                        }
                    }
                    Movie outputMovie = new Movie();
            /*if (!listTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }*/
                    if (audioTracks.size() > 0) {
                        outputMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        outputMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }
                    Container container = new DefaultMp4Builder().build(outputMovie);
                    FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();
                    container.writeContainer(fileChannel);
                    fileChannel.close();
                    videoPath=targetFile;
                    for(int i=0; i<sourceFiles.size(); i++) {
                        String filePath=sourceFiles.get(i);
                        File file=new File(filePath);
                        if(file.exists()) {
                            if(file.delete()) {
                                Log.i("mergeVideoFiles", "File deleted successfully: "+filePath);
                            }
                            else {
                                Log.i("mergeVideoFiles", "Couldn't delete the file: "+filePath);
                            }
                        }
                    }
                }
                catch (IOException e) {
                    Log.e("mergeVideoFiles", "Error merging media files. exception: "+e.getMessage());
                }
                return Observable.just(videoPath);
            }
        });
    }

    public static Observable<ArrayList<Picture>> imagesObservable(final Context context, final ArrayList<Picture> images) {
        return Observable.defer(new Callable<ObservableSource<? extends ArrayList<Picture>>>() {
            @Override public ObservableSource<? extends ArrayList<Picture>> call() throws Exception {
                for(Picture image : images) {
                    if(image.getUri()!=null) {
                        InputStream image_stream=null;
                        try {
                            image_stream=context.getContentResolver().openInputStream(image.getUri());
                        } catch(FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap=BitmapFactory.decodeStream(image_stream);
                        if(bitmap!=null) {
                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                            byte[] byteArray=byteArrayOutputStream.toByteArray();
                            String encoded=Base64.encodeToString(byteArray, Base64.DEFAULT);
                            image.setUpload(encoded);
                            bitmap.recycle();
                        }
                    }
                }
                return Observable.just(images);
            }
        });
    }

    public static Observable<ContactsRequest> contactsObservable(final Context context) {
        return Observable.defer(new Callable<ObservableSource<? extends ContactsRequest>>() {
            @Override public ObservableSource<? extends ContactsRequest> call() throws Exception {
                ContactsRequest request=new ContactsRequest();
                ArrayList<Contact> contacts=getContacts2(context);
                request.setEmails(new ArrayList<String>());
                request.setPhones(new ArrayList<Phone>());
                if(contacts!=null) {
                    for(int i=0; i<contacts.size(); i++) {
                        Contact contact=contacts.get(i);
                        if(!StringUtils.isEmpty(contact.getEmail())) {
                            request.getEmails().add(contact.getEmail());
                        }
                        if(!StringUtils.isEmpty(contact.getPhone())) {
                            Phone phone=new Phone();
                            phone.setNumber(contact.getPhone());
                            request.getPhones().add(phone);
                        }
                    }
                }
                return Observable.just(request);
            }
        });
    }

    public static ArrayList<Contact> getContacts2(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();

        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

        Uri uri = ContactsContract.Data.CONTENT_URI;
        // we could also use Uri uri = ContactsContract.Data.CONTENT_URI;

        // ok, let's work...
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if(cursor!=null) {
            final int mimeTypeIdx=cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            final int idIdx=cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            final int nameIdx=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int dataIdx=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
            final int typeIdx=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIdx);
                Contact contact=new Contact();
                contact.setName(cursor.getString(nameIdx));
                int type = cursor.getInt(typeIdx);
                String data = cursor.getString(dataIdx);
                String mimeType = cursor.getString(mimeTypeIdx);
                if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    // mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    contact.setEmail(data);
                } else {
                    // mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    contact.setPhone(data);
                }
                contacts.add(contact);
            }
            cursor.close();
        }

        return contacts;
    }

    /*public static ArrayList<Contact> getContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        ContentResolver cr = CustomApplication.getAppContext().getContentResolver();
        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cur!=null) {
            try {
                if(cur.moveToFirst()) {
                    do {
                        String id=cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name=cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String email=null;
                        Cursor ce=cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID+" = ?", new String[]{id}, null);
                        if(ce!=null&&ce.moveToFirst()) {
                            email=ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            ce.close();
                        }
                        String phone=null;
                        if(cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER))>0) {
                            Cursor pCur=cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                                    new String[]{id}, null);
                            if(pCur!=null) {
                                if(pCur.moveToFirst()) {
                                    phone=pCur.getString(pCur.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                                pCur.close();
                            }
                        }
                        Uri person=ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
                        Uri photo=Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                        Contact contact=new Contact();
                        contact.setName(name);
                        contact.setEmail(email);
                        contact.setPhone(phone);
                        contact.setPhoto(photo);
                        contacts.add(contact);

                    } while(cur.moveToNext());
                }


            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                cur.close();
            }
        }
        return contacts;
    }*/
}
