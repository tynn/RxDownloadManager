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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import rx.Observable;
import rx.subjects.AsyncSubject;

class DownloadBroadcastReceiver extends BroadcastReceiver {

    private final long downloadId;
    private final AsyncSubject<Long> receivedId;

    DownloadBroadcastReceiver(long downloadId) {
        this.downloadId = downloadId;
        this.receivedId = AsyncSubject.create();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

        if (downloadId == receivedId) {
            context.unregisterReceiver(this);
            this.receivedId.onNext(receivedId);
            this.receivedId.onCompleted();
        }
    }

    Observable<Long> getDownloadId() {
        return receivedId;
    }
}
