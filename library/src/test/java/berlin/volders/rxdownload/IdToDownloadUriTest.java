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
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static android.app.DownloadManager.COLUMN_LOCAL_URI;
import static android.app.DownloadManager.COLUMN_STATUS;
import static android.app.DownloadManager.Query;
import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@SuppressWarnings("WeakerAccess")
@RunWith(PowerMockRunner.class)
@PrepareForTest(IdToDownloadUri.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class IdToDownloadUriTest {

    @Mock
    DownloadManager dm;
    @Mock
    Cursor cursor;
    @Mock
    Query query;

    IdToDownloadUri func;

    @Before
    public void setup() throws Exception {
        whenNew(Query.class).withNoArguments().thenReturn(query);
        func = new IdToDownloadUri(dm);
    }

    @Test
    public void call_emptyCursor() {
        when(cursor.moveToFirst()).thenReturn(false);
        when(dm.query((Query) any())).thenReturn(cursor);

        func.call(1L).test().assertError(DownloadFailed.class);

        verify(query).setFilterById(1L);
    }

    @Test
    public void call_downloadStatus_notSuccessful() {
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getColumnIndex(COLUMN_STATUS)).thenReturn(15);
        when(cursor.getInt(15)).thenReturn(STATUS_FAILED);
        when(dm.query((Query) any())).thenReturn(cursor);

        func.call(2L).test().assertError(DownloadFailed.class);

        verify(query).setFilterById(2L);
    }

    @Test
    @PrepareForTest({IdToDownloadUri.class, Uri.class})
    public void call_downloadSuccess() {
        Uri uri = mock(Uri.class);
        PowerMockito.mockStatic(Uri.class);
        PowerMockito.when(Uri.parse("file")).thenReturn(uri);
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getColumnIndex(COLUMN_STATUS)).thenReturn(15);
        when(cursor.getColumnIndex(COLUMN_LOCAL_URI)).thenReturn(16);
        when(cursor.getInt(15)).thenReturn(STATUS_SUCCESSFUL);
        when(cursor.getString(16)).thenReturn("file");
        when(dm.query((Query) any())).thenReturn(cursor);

        func.call(3L).test().assertValue(uri);

        verify(query).setFilterById(3L);
    }
}
