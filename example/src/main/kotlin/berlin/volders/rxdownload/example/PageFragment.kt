package berlin.volders.rxdownload.example

import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import berlin.volders.rxdownload.RxDownloadManager
import kotlinx.android.synthetic.main.fragment_base.*

abstract class PageFragment(val fileName: String) : Fragment() {

    val downloadManager: RxDownloadManager by lazy {
        RxDownloadManager.from(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewStub.layoutResource = getStubViewLayout()
        viewStub.inflate()
        button.setOnClickListener { buttonClicked() }
        onPostStubLoad()
    }

    private fun buttonClicked() {
        val request = downloadManager.request(getUri(), fileName, R.string.download_description)
        progressBar.visibility = View.VISIBLE
        downloadManager.download(request).subscribe { onDownloadCompleted(uri = it) }
    }

    open fun onPostStubLoad() {}

    @LayoutRes
    abstract fun getStubViewLayout(): Int

    abstract fun getUri(): Uri

    abstract fun onDownloadCompleted(uri: Uri)
}
