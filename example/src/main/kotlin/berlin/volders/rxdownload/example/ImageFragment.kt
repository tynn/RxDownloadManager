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
import android.os.Environment
import android.view.View
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.io.File

class ImageFragment : PageFragment("rxdm-image.jpg") {

    private val filePath: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/" + fileName

    override fun getStubViewLayout(): Int {
        return R.layout.fragment_image
    }

    override fun onDownloadCompleted(uri: Uri) {
        progressBar.visibility = View.GONE
        photoView.setImageURI(uri)
        button.visibility = View.GONE
    }

    override fun onPostStubLoad() {
        if (isDownloaded()) {
            onDownloadCompleted(getUri())
        }
    }

    override fun getUri(): Uri {
        if (isDownloaded()) {
            return Uri.fromFile(File(filePath))
        } else {
            return Uri.parse(getString(R.string.image_url))
        }
    }

    private fun isDownloaded(): Boolean {
        return File(filePath).exists()
    }
}
