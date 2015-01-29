package com.theraytar.ungkyrkja;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        if(isOnline()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                DownloadFile();
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
            else{
                Context context = getApplicationContext();
                CharSequence notSupport = "Your device does not support Download Manager.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, notSupport, duration);
                toast.show();
            }
        }
        else{
            //Do something here, too
            Context context = getApplicationContext();
            CharSequence noConnection = "Cannot connect to server.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, noConnection, duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//Currently not in use
//    public static boolean isDownloadManagerAvailable(Context context) {
//        try{
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
//                return false;
//            }
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
//            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            return list.size() > 0;
//        } catch (Exception e){
//            return false;
//        }
//    }

    public void DownloadFile(){
        deleteFile(Environment.getExternalStorageDirectory()+"/ungKyrkja/", "uk-xml.xml");
        String url = "http://www.ungkyrkja.co.nf/uk-xml.xml";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("ungKyrkja is downloading a file");
        request.setTitle("ungKyrkja");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            //If you want a notification, uncomment next line
            //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        //Environment.DIRECTORY_DOWNLOADS
        request.setDestinationInExternalPublicDir("/ungKyrkja", "uk-xml.xml");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                Thread.sleep(2000);
            } catch (Exception e){}
            setContentView(R.layout.activity_main);
            Toast toast = Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

}
