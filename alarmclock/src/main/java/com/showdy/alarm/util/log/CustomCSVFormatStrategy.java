package com.showdy.alarm.util.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.showdy.alarm.App;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/**
 * CSV formatted file logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */
public class CustomCSVFormatStrategy implements FormatStrategy {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";

    @NonNull
    private final Date date;
    @NonNull
    private final SimpleDateFormat dateFormat;
    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;

    private CustomCSVFormatStrategy(@NonNull Builder builder) {
        Objects.requireNonNull(builder);

        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    @NonNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
        Objects.requireNonNull(message);

        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
//        builder.append(Long.toString(date.getTime()));

        // human-readable date/time
//        builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // tag
//        builder.append(SEPARATOR);
//        builder.append(tag);

        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(SEPARATOR);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!TextUtils.isEmpty(tag) && !Objects.equals(this.tag, tag)) {
          //不需要大tag
            return tag;
//            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 2 * 1024 * 1024; // 500K averages to a 4000 lines per file

        Date date;
        SimpleDateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

        @NonNull
        public Builder date(@Nullable Date val) {
            date = val;
            return this;
        }

        @NonNull
        public Builder dateFormat(@Nullable SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        @NonNull
        public Builder logStrategy(@Nullable LogStrategy val) {
            logStrategy = val;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull
        public CustomCSVFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.CHINA);
            }
            if (logStrategy == null) {
                String diskPath = App.Companion.getInstance().getFilesDir().getPath();
                String folder = diskPath + File.separatorChar + "logger";

                HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                ht.start();
                Handler handler = new CustomDiskLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
                logStrategy = new CustomDiskLogStrategy(handler);
            }
            return new CustomCSVFormatStrategy(this);
        }
    }
}
