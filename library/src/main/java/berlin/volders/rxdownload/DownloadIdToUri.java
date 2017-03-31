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

import rx.functions.Func1;

class DownloadIdToUri implements Func1<Long, Uri> {

    private final DownloadManager dm;

    DownloadIdToUri(DownloadManager dm) {
        this.dm = dm;
    }

    @Override
    public Uri call(Long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);

        Cursor cur = dm.query(query);
        try {
            if (!cur.moveToFirst()) {
                throw new DownloadFailed(id.toString());
            }

            int statusColumn = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (cur.getInt(statusColumn) != DownloadManager.STATUS_SUCCESSFUL) {
                int titleColumn = cur.getColumnIndex(DownloadManager.COLUMN_TITLE);
                throw new DownloadFailed(cur.getString(titleColumn));
            }

            int localUriColumn = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            return Uri.parse(cur.getString(localUriColumn));
        } finally {
            cur.close();
        }
    }
}
