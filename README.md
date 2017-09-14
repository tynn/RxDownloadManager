RxDownloadManager
=================
[![Build][1]][2]
[![Release][3]][4]

*RxDownloadManager* is an implementation wrapping around the Android
`DownloadManager`, using Rx1 to expose a `Single`. This allows the user 
to handle download completion and error events.


Usage
-----

Create a new instance of the `RxDownloadManager` from the application context.

    rxDownloadManager = RxDownloadManager.from(context);

Then the manager can be directly used by supplying the `Uri`, `String` file name
and `@StringRes` description to the `download` method.
The `Single` will then asynchronously emit an `Uri`, pointing to the local file.

    rxDownloadManager.download(remoteFileUri, fileName, R.string.description)
                     .subscribe(this::useLocalFileUri, this::handleError);

A `DownloadManager.Request` is built automatically and enqueued by the `download()` 
method. 

Convinience methods returning `DownloadManager.Request` are supplied for
further customization. Downloaded files are stored under `Environment.DIRECTORY_DOWNLOADS`.

    request = RxDownloadManager.request(uri, fileName)
                               .setDescription("Downloading...")
                               .setMimeType("application/pdf");
    rxDownloadManager.download(request);


Installation
------------

Add [JitPack][4] to your repositories and *RxDownloadManager* to your dependencies

    dependencies {
        compile "com.github.v2rc:rxdownloadmanager:$rxDownloadManagerVersion"
    }


License
-------

    Copyright (C) 2017 volders GmbH with <3 in Berlin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


  [1]: https://travis-ci.org/v2rc/RxDownloadManager.svg?branch=1.x
  [2]: https://travis-ci.org/v2rc/RxDownloadManager
  [3]: https://jitpack.io/v/v2rc/rxdownloadmanager.svg
  [4]: https://jitpack.io/#v2rc/rxdownloadmanager
