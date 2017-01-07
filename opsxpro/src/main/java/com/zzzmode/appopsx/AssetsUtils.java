package com.zzzmode.appopsx;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zl on 2016/11/13.
 */

class AssetsUtils {

    public static void copyFile(Context context, String fileName, File destFile, boolean force){
        InputStream open=null;
        FileOutputStream fos=null;
        try {

            AssetFileDescriptor openFd = context.getAssets().openFd(fileName);

            if(force){
                destFile.delete();
            }else {
                if(destFile.exists()){
                    if( destFile.length() != openFd.getLength()){
                        destFile.delete();
                    }else {
                        return;
                    }
                }
            }

            if(!destFile.exists()){
                destFile.createNewFile();
                destFile.setReadable(true,false);
                destFile.setExecutable(true,false);
            }


            fos=new FileOutputStream(destFile);
            byte[] buff=new byte[1024*16];
            int len=-1;
            open=openFd.createInputStream();

            while ( (len=open.read(buff)) != -1){
                fos.write(buff,0,len);
            }
            fos.flush();
            fos.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fos != null)
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if(open != null)
                open.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
