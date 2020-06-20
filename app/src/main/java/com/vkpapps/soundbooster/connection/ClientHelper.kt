package com.vkpapps.soundbooster.connection

import android.os.Bundle
import com.vkpapps.soundbooster.analitics.Logger
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener
import com.vkpapps.soundbooster.interfaces.OnControlRequestListener
import com.vkpapps.soundbooster.interfaces.OnObjectReceiveListener
import com.vkpapps.soundbooster.model.User
import com.vkpapps.soundbooster.model.control.ControlFile
import com.vkpapps.soundbooster.model.control.ControlPlayer
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

/***
 * @author VIJAY PATIDAR
 */
class ClientHelper(private val socket: Socket, private val onControlRequestListener: OnControlRequestListener, var user: User, private val onClientConnectionStateListener: OnClientConnectionStateListener?) : Thread() {
    private var outputStream: ObjectOutputStream? = null
    private var onObjectReceiveListener: OnObjectReceiveListener? = null
    override fun run() {
        val bundle = Bundle()
        bundle.putString("ID", user.userId)
        try {
            outputStream = ObjectOutputStream(socket.getOutputStream())
            // send identity to connected device
            outputStream!!.writeObject(user)
            outputStream!!.flush()
            val inputStream = ObjectInputStream(socket.getInputStream())
            var obj = inputStream.readObject()
            if (obj is User) {
                user = obj

                //notify user added
                onClientConnectionStateListener?.onClientConnected(this)
                var retry = 0
                while (!socket.isClosed) {
                    try {
                        obj = inputStream.readObject()
                        if (obj is ControlPlayer) {
                            onControlRequestListener.onMusicPlayerControl(obj)
                            onObjectReceiveListener?.onObjectReceive(obj)
                        } else if (obj is ControlFile) {
                            handleFileControl(obj)
                        } else if (obj is User) {
                            // update user information
                            if (obj.userId == user.userId) {
                                user.name = obj.name
                            }
                        } else {
                            Logger.e("invalid object received $obj")
                        }
                    } catch (e: Exception) {
                        retry++
                        if (retry == 10) break
                        e.printStackTrace()
                    }
                }
            } else {
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // notify client leaved or disconnected
        onClientConnectionStateListener?.onClientDisconnected(this)
    }

    fun write(command: Any) {
        Thread(Runnable {
            try {
                outputStream?.writeObject(command)
                outputStream?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

    private fun handleFileControl(control: ControlFile) {
        try {
            when (control.action) {
                ControlFile.DOWNLOAD_REQUEST -> onControlRequestListener.onDownloadRequest(control.data, control.id, control.type)
                ControlFile.UPLOAD_REQUEST -> onControlRequestListener.onUploadRequest(control.data, control.id, control.type)
                ControlFile.DOWNLOAD_REQUEST_CONFIRM -> onControlRequestListener.onDownloadRequestAccepted(control.data, control.id, control.type)
                ControlFile.UPLOAD_REQUEST_CONFIRM -> onControlRequestListener.onUploadRequestAccepted(control.data, control.id, control.type)
                else -> Logger.d("handleFileControl: invalid req " + control.action)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun setOnObjectReceiveListener(onObjectReceiveListener: OnObjectReceiveListener) {
        this.onObjectReceiveListener = onObjectReceiveListener
    }

    fun shutDown() {
        try {
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}