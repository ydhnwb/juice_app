package com.plugin.justiceapp.utils

import android.content.Context
import com.beautycoder.pflockscreen.security.IPFPinCodeHelper
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback


class PFPinCodeHelperImpl : IPFPinCodeHelper {
    override fun encodePin(context: Context?, pin: String, callBack: PFPinCodeHelperCallback<String>?) {
        if (callBack == null) { return }
        callBack.onResult(PFResult(pin + "1111"))
    }

    override fun checkPin(context: Context?, encodedPin: String, pin: String, callback: PFPinCodeHelperCallback<Boolean>?) {
        if (callback == null) {
            return
        }
        callback.onResult(PFResult(encodedPin == pin + "1111"))
    }

    override fun delete(callback: PFPinCodeHelperCallback<Boolean>) {
        //Delete all stuff related to pincode encryption
        //Like any additional keys etc;
        //Or anything on the server side
    }

    override fun isPinCodeEncryptionKeyExist(callback: PFPinCodeHelperCallback<Boolean>) {
        //If you use anyadditional keys. For encryprion or anything else. Here should be check that
        //all that keys or whatever exist and you actually can perform decryption.
        //This is necessary for default PFPinCodeHelper with fingerprint and keystore.
        //Maybe your won't need it.
        callback.onResult(PFResult(true))
    }
}