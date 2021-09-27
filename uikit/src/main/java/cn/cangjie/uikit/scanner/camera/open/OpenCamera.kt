package cn.cangjie.uikit.scanner.camera.open

import android.hardware.Camera
import cn.cangjie.uikit.scanner.camera.open.CameraFacing

// camera APIs
class OpenCamera(private val index: Int, val camera: Camera, val facing: CameraFacing, val orientation: Int) {

    override fun toString(): String {
        return "Camera #$index : $facing,$orientation"
    }

}