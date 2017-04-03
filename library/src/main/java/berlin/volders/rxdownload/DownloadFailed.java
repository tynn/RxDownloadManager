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

/**
 * {@link Exception} thrown in case that when the download is finished there is no
 * id matching the enqueued download or if the download completed with status
 * other than {@link android.app.DownloadManager#STATUS_SUCCESSFUL}
 */
public class DownloadFailed extends IllegalStateException {

    DownloadFailed(String msg) {
        super(msg);
    }
}
