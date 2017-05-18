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

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pageIndicators.viewPager = viewPager

        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PERMISSION_GRANTED) {
            viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == R.id.action_downloads) {
                startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)) is Unit
            } else {
                super.onOptionsItemSelected(item)
            }

    override fun onRequestPermissionsResult(code: Int, permissions: Array<out String>, granted: IntArray) {
        if (code == 1 && granted.elementAtOrNull(0) == PERMISSION_GRANTED) {
            viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        }
    }
}
