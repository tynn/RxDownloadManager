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

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

class ReceiverRegistry implements Observable.Operator<Long, Long>, Subscription {

    private static final IntentFilter DOWNLOAD_COMPLETE
            = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

    private final Context context;
    private final DownloadReceiver receiver;
    private final AtomicInteger subscriptions;

    ReceiverRegistry(Context context, DownloadReceiver receiver) {
        this.context = context;
        this.receiver = receiver;
        this.subscriptions = new AtomicInteger(0);
    }

    @Override
    public void unsubscribe() {
        if (!isUnsubscribed() && subscriptions.decrementAndGet() == 0) {
            try {
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                // broadcast receiver was unregistered before
            }
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return subscriptions.get() == 0;
    }

    @Override
    public Subscriber<? super Long> call(Subscriber<? super Long> subscriber) {
        if (subscriptions.getAndIncrement() == 0) {
            context.registerReceiver(receiver, DOWNLOAD_COMPLETE);
        }
        subscriber.add(this);
        return subscriber;
    }
}
