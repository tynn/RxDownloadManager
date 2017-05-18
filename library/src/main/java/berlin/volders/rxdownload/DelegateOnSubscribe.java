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

import android.net.Uri;

import java.util.concurrent.atomic.AtomicReference;

import rx.Single;
import rx.SingleSubscriber;

class DelegateOnSubscribe implements Single.OnSubscribe<Uri> {

    private final AtomicReference<Single<Uri>> source;

    DelegateOnSubscribe(AtomicReference<Single<Uri>> source) {
        this.source = source;
    }

    @Override
    public void call(SingleSubscriber<? super Uri> singleSubscriber) {
        source.get().subscribe(singleSubscriber);
    }
}
