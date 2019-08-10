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
import android.net.Uri.parse
import android.view.View.GONE
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_internal_pdf.*

class InternalPdfFragment : PageFragment("rxdm-internal.pdf") {

    override val stubViewLayout = R.layout.fragment_internal_pdf
    override val uri: Uri by lazy {
        parse(getString(R.string.pdf_download_url))
    }

    override fun onDownloadCompleted(uri: Uri) {
        button.visibility = GONE
        pdfView.fromUri(uri)
            .onLoad {
                textLabel.visibility = GONE
                progressBar.visibility = GONE
            }.load()
    }
}
