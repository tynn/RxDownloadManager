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

    val String.isFile get() = File(this).exists()

    private val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/" + fileName

    override fun getStubViewLayout() = R.layout.fragment_image

    override fun onDownloadCompleted(uri: Uri) {
        button.visibility = View.GONE
        progressBar.visibility = View.GONE
        photoView.setImageURI(uri)
        photoView.background = photoView.drawable.constantState.newDrawable()
    }

    override fun onPostStubLoad() {
        if (filePath.isFile) {
            onDownloadCompleted(getUri())
        }
    }

    override fun getUri() = if (filePath.isFile) {
        Uri.fromFile(File(filePath))
    } else {
        Uri.parse(getString(R.string.image_url))
    }
}
