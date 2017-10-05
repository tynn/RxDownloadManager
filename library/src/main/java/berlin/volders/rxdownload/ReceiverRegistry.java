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

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


class ReceiverRegistry implements ObservableOperator<Long, Long>, Disposable {

    private static final IntentFilter DOWNLOAD_COMPLETE
            = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

    private final Context context;
    private final DownloadReceiver receiver;
    private final AtomicInteger subscriptions;
    private final CompositeDisposable disposables;

    ReceiverRegistry(Context context, DownloadReceiver receiver) {
        this.context = context;
        this.receiver = receiver;
        this.subscriptions = new AtomicInteger(0);
        disposables = new CompositeDisposable();
    }

    @Override
    public Observer<? super Long> apply(Observer<? super Long> observer) {
        if (subscriptions.getAndIncrement() == 0) {
            context.registerReceiver(receiver, DOWNLOAD_COMPLETE);
        }
        disposables.add(this);
        return observer;
    }

    @Override
    public void dispose() {
        if (!isDisposed() && subscriptions.decrementAndGet() == 0) {
            try {
                disposables.clear();
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                // broadcast receiver was unregistered before
            }
        }
    }

    @Override
    public boolean isDisposed() {
        return subscriptions.get() == 0;
    }
}
