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
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.StringRes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest(RxDownloadManager.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class RxDownloadManagerTest {

    @Mock
    Context context;
    @Mock
    DownloadManager dm;
    @Mock
    DownloadManager.Request request;
    @Mock
    Uri uri;

    RxDownloadManager rxDownloadManager;

    @Before
    public void setup() throws Exception {
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getSystemService(Context.DOWNLOAD_SERVICE)).thenReturn(dm);
        when(request.setTitle(any(CharSequence.class))).thenReturn(request);
        when(request.setDescription(any(CharSequence.class))).thenReturn(request);
        when(request.setDestinationInExternalPublicDir(anyString(), anyString())).thenReturn(request);
        when(request.setNotificationVisibility(anyInt())).thenReturn(request);
        rxDownloadManager = new RxDownloadManager(context, dm);
    }

    @Test
    public void from() throws Exception {
        RxDownloadManager rxDownloadManager = RxDownloadManager.from(context);

        assertThat(rxDownloadManager, notNullValue());
        assertThat(rxDownloadManager.dm, is(dm));
        assertThat(rxDownloadManager.context.get(), is(context));
    }

    @Test
    public void download_uri() throws Exception {
        @StringRes int descriptionRes = 123456;
        String fileName = "fileName";
        String description = "description";
        whenNew(DownloadManager.Request.class).withArguments(uri).thenReturn(request);
        when(context.getString(descriptionRes)).thenReturn(description);
        when(dm.enqueue(request)).thenReturn(1L);

        Download download = rxDownloadManager.download(uri, fileName, descriptionRes);

        assertThat(download.id, is(1L));
        verify(context, never()).registerReceiver(any(DownloadReceiver.class), any(IntentFilter.class));
        verify(dm).enqueue(request);
        verify(request).setTitle(fileName);
        verify(request).setDescription(description);
        verify(request).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        verify(request).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }

    @Test
    public void download_request() throws Exception {
        when(dm.enqueue(request)).thenReturn(1L);

        Download download = rxDownloadManager.download(request);

        assertThat(download.id, is(1L));
        verify(context, never()).registerReceiver(any(DownloadReceiver.class), any(IntentFilter.class));
        verify(dm).enqueue(request);
    }
}
