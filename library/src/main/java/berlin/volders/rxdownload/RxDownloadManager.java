/*
 * Copyright (C) 2017 volders GmbH with <3 in Berlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package berlin.volders.rxdownload;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;

import rx.Single;

/**
 * A reference to the {@code RxDownloadManager} has to be initialised by passing {@link Context}.
 * Then the manager can be directly used by supplying a {@link Uri}, {@link String} file name
 * and a string resource with a description passed to the {@code download} method.
 * <p>
 * <pre>
 *     rxDownloadManager.download(remoteFileUri, fileName, R.string.description)
 *                      .subscribe(this::useLocalFileUri, this::handleError);
 * </pre>
 */
public class RxDownloadManager {

    final WeakReference<Context> context;
    final DownloadManager dm;

    @VisibleForTesting
    RxDownloadManager(Context context, DownloadManager dm) {
        this.context = new WeakReference<>(context);
        this.dm = dm;
    }

    /**
     * @param context Any available {@link Context}. Required to get the application context
     * @return A new instance of this manager
     */
    public static RxDownloadManager from(@NonNull Context context) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return new RxDownloadManager(context.getApplicationContext(), dm);
    }

    /**
     * @param download {@link Download} to bind to a {@link Context}
     * @return {@code download} bound to a {@link Context}
     */
    public Download bind(Download download) {
        return download.bind(context.get(), dm);
    }

    /**
     * @param uri         {@link Uri} for downloading the file
     * @param fileName    Destination file name
     * @param description String resource for the text to be shown in the notification associated with the download
     * @return {@link Single} that, upon download completion, will emit the {@link Uri} pointing to the local file
     */
    public Download download(Uri uri, String fileName, @StringRes int description) {
        return download(request(uri, fileName, description));
    }

    /**
     * @param request {@link DownloadManager.Request} that contains the file destination and URL information for downloading
     * @return {@link Single} that, upon download completion, will emit the {@link Uri} pointing to the local file
     */
    public Download download(DownloadManager.Request request) {
        return bind(new Download(dm.enqueue(request)));
    }

    /**
     * @param uri      {@link Uri} for downloading the file
     * @param fileName Destination file name
     * @return Built {@link DownloadManager.Request} with destination folder {@link Environment#DIRECTORY_DOWNLOADS} and file name
     */
    public static DownloadManager.Request request(Uri uri, String fileName) {
        return new DownloadManager.Request(uri)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setTitle(fileName);
    }

    /**
     * @param uri         {@link Uri} for downloading the file
     * @param fileName    Destination file name
     * @param description {@link String} to be shown in the notification associated with the download
     * @return {@link DownloadManager.Request} with destination folder {@link Environment#DIRECTORY_DOWNLOADS} and file name
     */
    public static DownloadManager.Request request(Uri uri, String fileName, String description) {
        return request(uri, fileName).setDescription(description);
    }

    /**
     * @param uri         {@link Uri} for downloading the file
     * @param fileName    Destination file name
     * @param description String resource for the text to be shown in the notification associated with the download
     * @return {@link DownloadManager.Request} with destination folder {@link Environment#DIRECTORY_DOWNLOADS} and file name
     */
    public DownloadManager.Request request(Uri uri, String fileName, @StringRes int description) {
        return request(uri, fileName).setDescription(context.get().getString(description));
    }
}
