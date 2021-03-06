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
import android.database.Cursor;
import android.net.Uri;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static android.app.DownloadManager.COLUMN_LOCAL_URI;
import static android.app.DownloadManager.COLUMN_STATUS;
import static android.app.DownloadManager.COLUMN_TITLE;
import static android.app.DownloadManager.Query;
import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;
import static android.net.Uri.parse;
import static io.reactivex.Observable.empty;
import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;
import static java.lang.String.valueOf;

class IdToDownloadUri implements Function<Long, Observable<Uri>> {

    private final DownloadManager dm;

    IdToDownloadUri(DownloadManager dm) {
        this.dm = dm;
    }

    @Override
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public Observable<Uri> apply(Long id) {
        Cursor cur = dm.query(new Query().setFilterById(id));
        try {
            if (!cur.moveToFirst()) {
                return error(new DownloadFailed(valueOf(id)));
            }

            switch (cur.getInt(cur.getColumnIndex(COLUMN_STATUS))) {
                case STATUS_FAILED:
                    int titleColumn = cur.getColumnIndex(COLUMN_TITLE);
                    return error(new DownloadFailed(cur.getString(titleColumn)));
                case STATUS_SUCCESSFUL:
                    int localUriColumn = cur.getColumnIndex(COLUMN_LOCAL_URI);
                    return just(parse(cur.getString(localUriColumn)));
                default:
                    return empty();
            }
        } finally {
            cur.close();
        }
    }
}
