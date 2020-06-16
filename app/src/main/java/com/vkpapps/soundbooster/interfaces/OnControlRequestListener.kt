package com.vkpapps.soundbooster.interfaces

import com.vkpapps.soundbooster.model.control.ControlPlayer

/**
 * @author VIJAY PATIDAR
 */
interface OnControlRequestListener {
    fun onMusicPlayerControl(controlPlayer: ControlPlayer)
    fun onDownloadRequest(name: String, id: String)
    fun onDownloadRequestAccepted(name: String, id: String)
    fun onUploadRequest(name: String, id: String)
    fun onUploadRequestAccepted(name: String, id: String)
}