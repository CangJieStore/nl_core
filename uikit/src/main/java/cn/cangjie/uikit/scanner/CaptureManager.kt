package cn.cangjie.uikit.scanner

import cn.cangjie.uikit.scanner.camera.CameraManager


interface CaptureManager {
    /**
     * Get [CameraManager]
     * @return [CameraManager]
     */
    val cameraManager: CameraManager?

    /**
     * Get [BeepManager]
     * @return [BeepManager]
     */
    val beepManager: BeepManager?

    /**
     * Get [AmbientLightManager]
     * @return [AmbientLightManager]
     */
    val ambientLightManager: AmbientLightManager?

    /**
     * Get [InactivityTimer]
     * @return [InactivityTimer]
     */
    val inactivityTimer: InactivityTimer?
}