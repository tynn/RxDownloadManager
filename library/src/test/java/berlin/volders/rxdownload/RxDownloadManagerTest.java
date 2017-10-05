/*
 * Copyright (C) 2019 Christian Schmitz
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
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;

import androidx.annotation.StringRes;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import berlin.volders.rxdownload.test.EnvironmentRule;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@SuppressWarnings("WeakerAccess")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class RxDownloadManagerTest {

    @Rule
    public final EnvironmentRule env = new EnvironmentRule();

    @Mock
    Context context;
    @Mock
    DownloadManager dm;
    @Mock
    Request request;
    @Mock
    Uri uri;

    RxDownloadManager rxDownloadManager;

    @Before
    public void setup() {
        rxDownloadManager = new RxDownloadManager(context, dm);
    }

    @Test
    public void from() {
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getSystemService(DOWNLOAD_SERVICE)).thenReturn(dm);

        RxDownloadManager rxDownloadManager = RxDownloadManager.from(context);

        assertThat(rxDownloadManager, notNullValue());
        assertThat(rxDownloadManager.dm, is(dm));
        assertThat(rxDownloadManager.context.get(), is(context));
    }

    @Test
    @PrepareForTest(RxDownloadManager.class)
    public void download_uri() throws Exception {
        @StringRes int descriptionRes = 123456;
        String fileName = "fileName";
        String description = "description";
        when(context.getString(descriptionRes)).thenReturn(description);
        when(dm.enqueue(request)).thenReturn(1L);
        when(request.setTitle(any(CharSequence.class))).thenReturn(request);
        when(request.setDescription(any(CharSequence.class))).thenReturn(request);
        when(request.setDestinationInExternalPublicDir(anyString(), anyString())).thenReturn(request);
        when(request.setNotificationVisibility(anyInt())).thenReturn(request);
        whenNew(Request.class).withAnyArguments().thenReturn(request);

        Download download = rxDownloadManager.download(uri, fileName, descriptionRes);

        assertThat(download.id, is(1L));
        verify(context, never()).registerReceiver(any(DownloadReceiver.class), any(IntentFilter.class));
        verify(dm).enqueue(request);
        verify(request).setTitle(fileName);
        verify(request).setDescription(description);
        verify(request).setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName);
        verify(request).setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }

    @Test
    public void download_request() {
        when(dm.enqueue(request)).thenReturn(2L);

        Download download = rxDownloadManager.download(request);

        assertThat(download.id, is(2L));
        verify(context, never()).registerReceiver(any(DownloadReceiver.class), any(IntentFilter.class));
        verify(dm).enqueue(request);
    }
}
