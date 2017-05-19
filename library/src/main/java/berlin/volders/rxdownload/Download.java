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
import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Single;

/**
 * {@link Parcelable} and observable {@link Single} representation of a download.
 * This {@code Download} can be bound to the {@link Context} to observe the download
 * over configuration changes.
 *
 * @see DownloadManager#enqueue(DownloadManager.Request)
 */
public class Download extends Single<Uri> implements Parcelable {

    /**
     * The id of the download returned by {@link DownloadManager#enqueue(DownloadManager.Request)}.
     */
    public final long id;

    private final AtomicReference<Single<Uri>> delegate;
    private final Single<Uri> source;

    Download(long downloadId) {
        this(downloadId, new AtomicReference<>(Single.<Uri>error(new DownloadUnbound())));
    }

    private Download(long downloadId, AtomicReference<Single<Uri>> delegate) {
        super(new DelegateOnSubscribe(delegate));
        this.delegate = delegate;
        this.id = downloadId;
        this.source = delegate.get();
    }

    Download bind(Context context, DownloadManager dm) {
        DownloadReceiver receiver = new DownloadReceiver(id);
        Single<Uri> delegate = Observable.defer(receiver)
                .lift(new ReceiverRegistry(context, receiver))
                .startWith(id)
                .concatMapEager(new IdToDownloadUri(dm))
                .first()
                .toSingle();

        this.delegate.compareAndSet(source, delegate);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }

    public static final Creator<Download> CREATOR = new Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel in) {
            return new Download(in.readLong());
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}
