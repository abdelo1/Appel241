

package com.example.appel241;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appel241.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    List<User> mlist;
    RecyclerView recycler;
    TextView loading;
    RecyclerViewAdapter mAdapter;
    TextView nbcontact;
    String name ,number,rawid;
    ProgressBar progressBar;
    String newnumber;
    int count;
    MyAsyncTask myAsyncTasks;
    private static final int PERMISSION_REQUEST_CODE = 1;

    EditText searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAsyncTasks = new MyAsyncTask();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        loading= (TextView) findViewById(R.id.Loading);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        recycler =(RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        nbcontact=(TextView)findViewById(R.id.nbContact);
        if (checkPermission()) {
            Log.e("permission", "Permission already granted.");
            myAsyncTasks.execute();
        } else {

            //If the app doesn’t have the CALL_PHONE permission, request it//

            requestPermission();
        }

        searchView=(EditText)findViewById(R.id.Search);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchWord=s.toString().toLowerCase();
                List<User> newList=new ArrayList<>();
                for(User us:mlist)
                {
                    if (us.getName().toLowerCase().contains(searchWord))
                        newList.add(us);
                }
                mAdapter.updateList(newList);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }




    public boolean checkPermission() {
        System.out.println("in checkpermissions");

        return  ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CONTACTS)== PackageManager.PERMISSION_GRANTED ;

    }

    private void requestPermission()
    {

        System.out.println("in Requestpermissions");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                }, PERMISSION_REQUEST_CODE);

    }
    @Override
    public void  onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        System.out.println("in onRequestpermissions");
        if  (requestCode==PERMISSION_REQUEST_CODE ) {

            if (
                    grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1]==PackageManager.PERMISSION_GRANTED
                            && grantResults[2]==PackageManager.PERMISSION_GRANTED
            )
            {
                Toast.makeText(MainActivity.this,
                        "Permission accepté", Toast.LENGTH_LONG).show();
                myAsyncTasks.execute();
                //If the permission is denied….//

            }
            else
            {
                Toast.makeText(MainActivity.this,

                        //...display the following toast...//

                        "Veuillez accepter les permissions pour pouvoir utiliser l'application", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        requestPermission();
                    }
                }, 3000); // LENGTH_SHORT is usually 2 second long


            }

        }

    }


    public class MyAsyncTask extends AsyncTask<Void, Void,Integer> {


        public MyAsyncTask(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
            Log.e("TAG", "AsyncTask is started.");
        }

        @Override
        protected void onPostExecute(Integer success) {
            super.onPostExecute(success);
            progressBar.setVisibility(View.GONE);
             loading.setVisibility(View.GONE);
            recycler.setAdapter(mAdapter);
            nbcontact.setText(count+" contacts");
            Toast.makeText(MainActivity.this,"Votre répertoire téléphonique à été mis à jour",Toast.LENGTH_LONG).show();
            Log.e("TAG", "AsyncTask is finished.");

        }

        @Override
        protected Integer doInBackground(Void... voids) {

            mlist=new ArrayList<>();

            String code;
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";

//Query the phone number table using the URI stored in CONTENT_URI//

            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  null, null,sortOrder);
            Cursor ncursor=getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,  null, null,sortOrder);
            count=ncursor.getCount();

            while (cursor.moveToNext())
            {

//Get the display name for each contact//

                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                rawid=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     number=number.replaceAll("\\s+","");

                         if(number.startsWith("00241")||number.startsWith("+241")||number.startsWith("07")||number.startsWith("06")||number.startsWith("05")||number.startsWith("04")||number.startsWith("02")||number.startsWith("01"))
                         {

                             if (number.length()==8&&number.startsWith("0"))
                             {
                                 code=number.substring(1,2);
                                 System.out.println("code " +code);
                                 if (code.equals("7")||code.equals("4"))
                                 {

                                     String part1=number.substring(1);

                                     newnumber="07".concat(part1);

                                 }
                                 if (code.equals("5")||code.equals("6")||code.equals("2")||code.equals("1"))
                                 {


                                     String part1=number.substring(1);

                                     newnumber="06".concat(part1);
                                 }


                             }
                             if(number.length()==12&&number.startsWith("+241"))
                             {

                                 code=number.substring(5,6);

                                 if (code.equals("7")||code.equals("4"))
                                 {

                                     newnumber=number.replace("+2410","+2417");

                                 }

                                 if (code.equals("5")||code.equals("6")||code.equals("2")||code.equals("1"))
                                 {

                                     newnumber=number.replace("+2410","+2416");
                                 }


                             }
                             if(number.length()==13 && number.startsWith("00241"))
                             {

                                 code=number.substring(6,7);

                                 if (code.equals("7")||code.equals("4"))
                                 {

                                     newnumber=number.replace("002410","+2417");

                                 }

                                 else if (code.equals("5")||code.equals("6")||code.equals("2")||code.equals("1"))
                                 {

                                     newnumber=number.replace("002410","+2416");
                                 }
                             }


                             User user = new User(name, newnumber);
                             mlist.add(user);

                        ContentResolver cr = getContentResolver();
                        ContentValues phoneNumberUpdate = new ContentValues();
                        phoneNumberUpdate.put(ContactsContract.CommonDataKinds.Phone.NUMBER,newnumber);
                        ContentValues nameUpdate = new ContentValues();
                        nameUpdate.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,name);
                        System.out.println("new  " +newnumber+" and name"+name+"and id "+rawid);
                        String where = ContactsContract.CommonDataKinds.Phone._ID+ "=?";
                        String [] params={rawid};
                        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
                        Uri dataUri = ContactsContract.Data.CONTENT_URI;

                        cr.update(dataUri, phoneNumberUpdate, where,params);
                        cr.update(dataUri, nameUpdate, where,params);




                         }
                         else
                         {
                             User user = new User(name, number);
                             mlist.add(user);

                         }


                     }






            mAdapter = new RecyclerViewAdapter(getApplicationContext(), mlist);

            cursor.close();
            ncursor.close();
            return mlist.size();
        }

    }
}
