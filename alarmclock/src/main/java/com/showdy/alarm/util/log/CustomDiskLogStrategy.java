package com.showdy.alarm.util.log;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;


/**
 * Abstract class that takes care of background threading the file log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 * <p>
 * Writes all logs to the disk with CSV format.
 */
public class CustomDiskLogStrategy implements LogStrategy {

    @NonNull
    private final Handler handler;

    public CustomDiskLogStrategy(@NonNull Handler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void log(int level, @Nullable String tag, @NonNull String message) {
        Objects.requireNonNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread

        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("tag", tag);
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    static class WriteHandler extends Handler {

        @NonNull
        private final String folder;
        private final int maxFileSize;

        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
            super(Objects.requireNonNull(looper));
            this.folder = Objects.requireNonNull(folder);
            this.maxFileSize = maxFileSize;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle content = (Bundle) msg.obj;
            String message = content.getString("message");
            String tag = content.getString("tag");
            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, tag);

            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, message);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            Objects.requireNonNull(fileWriter);
            Objects.requireNonNull(content);

            fileWriter.append(content);
        }

        private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
            Objects.requireNonNull(folderName);
            Objects.requireNonNull(fileName);

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }
            //------------Add--------------------------
            File newfolder = new File(folder, fileName);
            if (!newfolder.exists()) {
                newfolder.mkdirs();
            }

            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            newFile = new File(newfolder, String.format("%s_%s.csv", fileName, newFileCount));
            while (newFile.exists()) {
                existingFile = newFile;
                newFileCount++;
                newFile = new File(newfolder, String.format("%s_%s.csv", fileName, newFileCount));
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }

            return newFile;
        }
    }
}
