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

import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_base.*

abstract class PageFragment(val fileName: String) : DownloadFragment("x_" + fileName + "_downlad") {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, state: Bundle?)
            = inflater?.inflate(R.layout.fragment_base, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewStub.layoutResource = getStubViewLayout()
        viewStub.inflate()
        button.setOnClickListener { buttonClicked() }
        onPostStubLoad()
    }

    private fun buttonClicked() {
        progressBar.visibility = View.VISIBLE
        download(dm.request(getUri(), fileName, R.string.download_description))
    }

    open fun onPostStubLoad() {}

    @LayoutRes
    abstract fun getStubViewLayout(): Int

    abstract fun getUri(): Uri
}
