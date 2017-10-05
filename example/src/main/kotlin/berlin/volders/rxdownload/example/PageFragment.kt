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

package berlin.volders.rxdownload.example

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import kotlinx.android.synthetic.main.fragment_base.*

abstract class PageFragment(protected val fileName: String) : DownloadFragment("x_" + fileName + "_downlad") {

    @get:LayoutRes
    protected abstract val stubViewLayout: Int
    protected abstract val uri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_base, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewStub.layoutResource = stubViewLayout
        viewStub.inflate()
        button.setOnClickListener { buttonClicked() }
        onPostStubLoad()
    }

    private fun buttonClicked() {
        progressBar.visibility = VISIBLE
        download(dm.request(uri, fileName, R.string.description_download))
    }

    protected open fun onPostStubLoad() {}
}
