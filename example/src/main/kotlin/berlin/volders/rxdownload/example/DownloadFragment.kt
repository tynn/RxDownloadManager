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

package berlin.volders.rxdownload.example

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import berlin.volders.rxdownload.Download
import berlin.volders.rxdownload.RxDownloadManager
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

abstract class DownloadFragment(val key: String) : Fragment() {

    init {
        arguments = Bundle()
    }

    val dm: RxDownloadManager by lazy {
        RxDownloadManager.from(context)
    }

    val subscriptions: CompositeSubscription = CompositeSubscription()

    var Bundle.download: Download?
        get() = getParcelable(key)
        set(d) = if (d == null) remove(key) else putParcelable(key, d)

    fun Download.start() {
        arguments.download = dm.bind(this)
        subscriptions.add(this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this@DownloadFragment::onDownloadCompleted))
    }

    fun download(request: DownloadManager.Request) =
            (arguments.download ?: dm.download(request)).start()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments.download?.start()
    }

    override fun onDetach() {
        subscriptions.clear()
        super.onDetach()
    }

    abstract fun onDownloadCompleted(uri: Uri)
}
