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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IdToDownloadUri.class, Uri.class})
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class IdToDownloadUriTest {

    @Mock
    DownloadManager dm;
    @Mock
    Cursor cursor;

    IdToDownloadUri func;

    @Before
    public void setup() {
        func = new IdToDownloadUri(dm);
    }

    @Test
    public void call_emptyCursor() throws Exception {
        when(cursor.moveToFirst()).thenReturn(false);
        when(dm.query((DownloadManager.Query) any())).thenReturn(cursor);

        func.call(1L).test().assertError(DownloadFailed.class);
    }

    @Test
    public void call_downloadStatus_notSuccessful() throws Exception {
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)).thenReturn(15);
        when(cursor.getInt(15)).thenReturn(DownloadManager.STATUS_FAILED);
        when(dm.query((DownloadManager.Query) any())).thenReturn(cursor);

        func.call(1L).test().assertError(DownloadFailed.class);
    }

    @Test
    public void call_downloadSuccess() throws Exception {
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)).thenReturn(15);
        when(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).thenReturn(16);
        when(cursor.getInt(15)).thenReturn(DownloadManager.STATUS_SUCCESSFUL);
        when(cursor.getString(16)).thenReturn("file");
        when(dm.query((DownloadManager.Query) any())).thenReturn(cursor);
        mockStatic(Uri.class);
        PowerMockito.when(Uri.parse("file")).thenReturn(Uri.EMPTY);

        func.call(1L).test().assertValue(Uri.EMPTY);
    }
}
