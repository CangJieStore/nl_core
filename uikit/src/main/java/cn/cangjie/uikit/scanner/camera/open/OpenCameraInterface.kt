package cn.cangjie.uikit.scanner.camera.open

import android.hardware.Camera
import android.hardware.Camera.CameraInfo

// camera APIs
object OpenCameraInterface {
    /**
     * For [.open], means no preference for which camera to open.
     */
    const val NO_REQUESTED_CAMERA = -1

    /**
     * Opens the requested camera with [Camera.open], if one exists.
     *
     * @param cameraId camera ID of the camera to use. A negative value
     * or [.NO_REQUESTED_CAMERA] means "no preference", in which case a rear-facing
     * camera is returned if possible or else any camera
     * @return handle to [OpenCamera] that was opened
     */
    @JvmStatic
    fun open(cameraId: Int): OpenCamera? {
        var cameraId = cameraId
        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) {
            return null
        }
        if (cameraId >= numCameras) {
            return null
        }
        if (cameraId <= NO_REQUESTED_CAMERA) {
            cameraId = 0
            while (cameraId < numCameras) {
                val cameraInfo = CameraInfo()
                Camera.getCameraInfo(cameraId, cameraInfo)
                if (CameraFacing.values()[cameraInfo.facing] === CameraFacing.BACK) {
                    break
                }
                cameraId++
            }
            if (cameraId == numCameras) {
                cameraId = 0
            }
        }
        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        val camera = Camera.open(cameraId) ?: return null
        return OpenCamera(cameraId,
                camera,
                CameraFacing.values()[cameraInfo.facing],
                cameraInfo.orientation)
    }
}