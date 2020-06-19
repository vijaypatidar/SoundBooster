package com.vkpapps.soundbooster.interfaces

import com.vkpapps.soundbooster.model.control.ControlPlayer

/**
 * @author VIJAY PATIDAR
 */
interface OnControlRequestListener {
    fun onMusicPlayerControl(controlPlayer: ControlPlayer)
    fun onDownloadRequest(name: String, id: String, type: Int)
    fun onDownloadRequestAccepted(name: String, id: String, type: Int)
    fun onUploadRequest(name: String, id: String, type: Int)
    fun onUploadRequestAccepted(name: String, id: String, type: Int)
}