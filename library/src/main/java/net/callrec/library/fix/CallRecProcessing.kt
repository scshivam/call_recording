package net.callrec.library.fix

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File


/**
 * Created by Viktor Degtyarev on 16.10.17
 * E-mail: viktor@degtyarev.biz
 */
class CallRecProcessing(val service: Service) : ProcessingBase(service.applicationContext)  {

    override fun makeOutputFile(): String {
        val dirStorage = Utils.getDefaultPath(context)

        val file = File(dirStorage)

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw Exception()
            }
        }
        fileNameNoFormat = Utils.makeFileName(typeCall,callId)
        filePathNoFormat = dirStorage + fileNameNoFormat
        return filePathNoFormat
    }

    override fun isServiceOn(): Boolean {
        return true
    }

    override fun getPauseBeforeRecord(): Int {
        return 0
    }

    override fun getCheckRulesRecord(): Boolean {
        return true
    }

    override fun prepareAudioPreferences() {
        formatFile = "wav"
        audioSource = MediaRecorder.AudioSource.MIC
        outputFormat = 0
        encoder = 0
        stereoChannel = false
        samplingRate = 8000
        audioEncodingBitRate = 0
        typeRecorder = TypeRecorder.WAV
    }

    override fun stopThisService() {
        service.stopService(Intent(context, service.javaClass))
    }

    override fun onRecorderError(e: Exception) {
        super.onRecorderError(e)
        service.stopForeground(true)
    }

    override fun onRecorderError(e: RecorderBase.RecorderException) {
        super.onRecorderError(e)
        service.stopForeground(true)
    }

    override fun onRecorderError(e: ProcessingException) {
        super.onRecorderError(e)
        service.stopForeground(true)
    }

    override fun onStartRecord(){
        Toast.makeText(service.applicationContext,"Recording Started",Toast.LENGTH_SHORT).show();
    }

    override fun onStopRecord(){

        val values = ContentValues(4)
        val current = System.currentTimeMillis()
        values.put(MediaStore.Audio.Media.TITLE, "audio" + fileNameNoFormat+formatFile)
        values.put(MediaStore.Audio.Media.DATE_ADDED, (current / 1000).toInt())
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
        values.put(MediaStore.Audio.Media.DATA, filePathNoFormat+formatFile)

        val contentResolver = context.getContentResolver()
        val base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val newUri = contentResolver.insert(base, values)

        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri))
        Toast.makeText(service.applicationContext,"Recording Stopped",Toast.LENGTH_SHORT).show();
    }
}