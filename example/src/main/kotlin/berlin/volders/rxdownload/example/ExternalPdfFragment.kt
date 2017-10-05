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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.net.Uri.parse
import android.util.Log
import android.view.View.GONE
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.core.content.FileProvider.getUriForFile
import kotlinx.android.synthetic.main.fragment_base.*
import java.io.File

class ExternalPdfFragment : PageFragment("rxdm-external.pdf") {

    override val stubViewLayout = R.layout.fragment_external_pdf
    override val uri: Uri by lazy {
        parse(getString(R.string.url_pdf))
    }

    override fun onDownloadCompleted(uri: Uri) = with(context!!) {
        progressBar.visibility = GONE
        val authority = "$packageName.files"
        val file = File(uri.path!!)
        val content = getUriForFile(this, authority, file)
        val intent = Intent(ACTION_VIEW)
        intent.setDataAndType(content, "application/pdf")
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.w("ExternalPdfFragment", e)
            makeText(this, R.string.label_no_app, LENGTH_LONG).show()
        }
    }
}
