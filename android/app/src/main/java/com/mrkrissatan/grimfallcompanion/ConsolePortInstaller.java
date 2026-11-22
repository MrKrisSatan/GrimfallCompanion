package com.mrkrissatan.grimfallcompanion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.documentfile.provider.DocumentFile;
import android.widget.Toast;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

public final class ConsolePortInstaller {
    private static final String ZIP_URL="https://github.com/leoaviana/ConsolePortLK/archive/refs/tags/1.5.0-rc2.zip";
    private static final String ADDON_PREFIX="ConsolePort";
    private ConsolePortInstaller(){}
    public static void install(Activity activity,Uri folderUri){
        ProgressDialog progress=new ProgressDialog(activity);progress.setTitle("ConsolePortLK");progress.setMessage("Downloading and installing…");progress.setIndeterminate(true);progress.setCancelable(false);progress.show();
        new AsyncTask<Void,Void,String>(){
            protected String doInBackground(Void...v){
                try{
                    DocumentFile root=DocumentFile.fromTreeUri(activity,folderUri);
                    DocumentFile interfaceDir=root==null?null:root.findFile("Interface");
                    DocumentFile addons=interfaceDir==null?null:interfaceDir.findFile("AddOns");
                    if(addons==null||!addons.isDirectory())return "Select the client root containing Interface/AddOns.";
                    File archive=new File(activity.getCacheDir(),"cp-lk.zip");download(ZIP_URL,archive);
                    DocumentFile backup=addons.createDirectory("Grimfall_CP_Backup_"+System.currentTimeMillis());
                    if(backup==null)return "Unable to create addon backup folder.";
                    for(DocumentFile old:addons.listFiles()){String n=old.getName();if(old.isDirectory()&&n!=null&&n.startsWith(ADDON_PREFIX)&&!n.startsWith("Grimfall_CP_Backup"))copyTree(activity,old,backup.createDirectory(n));}
                    int written=extract(activity,archive,addons);archive.delete();
                    return written>0?"ConsolePortLK folders installed/repaired. Previous addon folders were backed up.":"Archive contained no recognised addon folders.";
                }catch(Exception e){return "ConsolePortLK installation failed: "+e.getMessage();}
            }
            protected void onPostExecute(String message){progress.dismiss();Toast.makeText(activity,message,Toast.LENGTH_LONG).show();}
        }.execute();
    }
    private static void download(String url,File file)throws Exception{
        HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();c.setConnectTimeout(20000);c.setReadTimeout(30000);c.setInstanceFollowRedirects(true);c.setRequestProperty("User-Agent","GrimfallCompanion");
        try(InputStream in=c.getInputStream();FileOutputStream out=new FileOutputStream(file)){byte[]b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);}finally{c.disconnect();}
    }
    private static boolean allowed(String n){return n.startsWith(ADDON_PREFIX);}
    private static int extract(Context context,File archive,DocumentFile addons)throws Exception{
        int count=0;ZipInputStream z=new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)));ZipEntry e;
        while((e=z.getNextEntry())!=null){
            String path=e.getName().replace('\\','/');String[] p=path.split("/");
            if(p.length<2||!allowed(p[1]))continue;
            DocumentFile folder=addons.findFile(p[1]);if(folder==null)folder=addons.createDirectory(p[1]);if(folder==null)continue;
            if(p.length==2){continue;}
            String[] sub=Arrays.copyOfRange(p,2,p.length);DocumentFile parent=folder;
            for(int i=0;i<sub.length-1;i++){DocumentFile next=parent.findFile(sub[i]);if(next==null)next=parent.createDirectory(sub[i]);if(next==null){parent=null;break;}parent=next;}
            if(parent==null||sub.length==0)continue;String name=sub[sub.length-1];
            if(e.isDirectory()){if(parent.findFile(name)==null)parent.createDirectory(name);continue;}
            DocumentFile prior=parent.findFile(name);if(prior!=null)prior.delete();
            DocumentFile outFile=parent.createFile("application/octet-stream",name);if(outFile==null)continue;
            try(OutputStream out=context.getContentResolver().openOutputStream(outFile.getUri())){byte[]b=new byte[8192];int n;while((n=z.read(b))!=-1)out.write(b,0,n);}
            count++;
        }z.close();return count;
    }
    private static void copyTree(Context c,DocumentFile src,DocumentFile dst)throws Exception{
        if(src==null||dst==null)return;
        for(DocumentFile f:src.listFiles()){
            if(f.isDirectory()){DocumentFile d=dst.createDirectory(f.getName());copyTree(c,f,d);}
            else{DocumentFile d=dst.createFile(f.getType()==null?"application/octet-stream":f.getType(),f.getName());if(d==null)continue;
                try(InputStream in=c.getContentResolver().openInputStream(f.getUri());OutputStream out=c.getContentResolver().openOutputStream(d.getUri())){byte[]b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);}}
        }
    }
}