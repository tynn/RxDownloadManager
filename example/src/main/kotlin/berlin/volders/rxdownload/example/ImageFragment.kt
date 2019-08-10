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
import android.net.Uri.fromFile
import android.net.Uri.parse
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.view.View.GONE
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.io.File

class ImageFragment : PageFragment("rxdm-image.jpg") {

    private val file by lazy {
        File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), fileName)
    }

    override val stubViewLayout = R.layout.fragment_image
    override val uri: Uri by lazy {
        if (file.exists()) {
            fromFile(file)
        } else {
            parse(getString(R.string.image_url))
        }
    }

    override fun onDownloadCompleted(uri: Uri) {
        button.visibility = GONE
        progressBar.visibility = GONE
        photoView.setImageURI(uri)
        photoView.background = photoView.drawable?.constantState?.newDrawable()
    }

    override fun onPostStubLoad() {
        if (file.exists()) {
            onDownloadCompleted(uri)
        }
    }
}
