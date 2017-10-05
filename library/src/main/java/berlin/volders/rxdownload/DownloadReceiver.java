/*
 * Copyright (C) 2018 José Pablo Álvarez Lacasia
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.subjects.AsyncSubject;

import static android.os.Looper.getMainLooper;
import static android.os.Looper.myLooper;
import static io.reactivex.Observable.fromFuture;
import static io.reactivex.android.schedulers.AndroidSchedulers.from;
import static io.reactivex.subjects.AsyncSubject.create;

class DownloadReceiver extends BroadcastReceiver implements Callable<Observable<Long>> {

    private final long downloadId;
    private final AsyncSubject<Long> receivedId;

    DownloadReceiver(long downloadId) {
        this.downloadId = downloadId;
        this.receivedId = create();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (downloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)) {
            context.unregisterReceiver(this);
            receivedId.onNext(downloadId);
            receivedId.onComplete();
        }
    }

    @Override
    public Observable<Long> call() {
        Looper me = myLooper();
        if (me == null) {
            return fromFuture(receivedId.toFuture());
        }
        if (me == getMainLooper()) {
            return receivedId;
        }
        return receivedId.observeOn(from(me));
    }
}
