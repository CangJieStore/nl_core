package cn.cangjie.uikit.scanner.camera

import android.graphics.Point
import android.graphics.Rect
import android.hardware.Camera
import android.os.Build
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.roundToInt

object CameraConfigurationUtils {
    private val SEMICOLON = Pattern.compile(";")
    private const val MIN_PREVIEW_PIXELS = 480 * 320 // normal screen
    private const val MAX_EXPOSURE_COMPENSATION = 1.5f
    private const val MIN_EXPOSURE_COMPENSATION = 0.0f
    private const val MAX_ASPECT_DISTORTION = 0.05
    private const val MIN_FPS = 10
    private const val MAX_FPS = 20
    private const val AREA_PER_1000 = 400
    fun setFocus(parameters: Camera.Parameters,
                 autoFocus: Boolean,
                 disableContinuous: Boolean,
                 safeMode: Boolean) {
        val supportedFocusModes = parameters.supportedFocusModes
        var focusMode: String? = null
        if (autoFocus) {
            focusMode = if (safeMode || disableContinuous) {
                findSettableValue("focus mode",
                        supportedFocusModes,
                        Camera.Parameters.FOCUS_MODE_AUTO)
            } else {
                findSettableValue("focus mode",
                        supportedFocusModes,
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
                        Camera.Parameters.FOCUS_MODE_AUTO)
            }
        }
        // Maybe selected auto-focus but not available, so fall through here:
        if (!safeMode && focusMode == null) {
            focusMode = findSettableValue("focus mode",
                    supportedFocusModes,
                    Camera.Parameters.FOCUS_MODE_MACRO,
                    Camera.Parameters.FOCUS_MODE_EDOF)
        }
        if (focusMode != null) {
            if (focusMode == parameters.focusMode) {
            } else {
                parameters.focusMode = focusMode
            }
        }
    }

    fun setTorch(parameters: Camera.Parameters, on: Boolean) {
        val supportedFlashModes = parameters.supportedFlashModes
        val flashMode: String?
        flashMode = if (on) {
            findSettableValue("flash mode",
                    supportedFlashModes,
                    Camera.Parameters.FLASH_MODE_TORCH,
                    Camera.Parameters.FLASH_MODE_ON)
        } else {
            findSettableValue("flash mode",
                    supportedFlashModes,
                    Camera.Parameters.FLASH_MODE_OFF)
        }
        if (flashMode != null) {
            if (flashMode == parameters.flashMode) {
            } else {
                parameters.flashMode = flashMode
            }
        }
    }

    fun setBestExposure(parameters: Camera.Parameters, lightOn: Boolean) {
        val minExposure = parameters.minExposureCompensation
        val maxExposure = parameters.maxExposureCompensation
        val step = parameters.exposureCompensationStep
        if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
            val targetCompensation = if (lightOn) MIN_EXPOSURE_COMPENSATION else MAX_EXPOSURE_COMPENSATION
            var compensationSteps = (targetCompensation / step).roundToInt()
            val actualCompensation = step * compensationSteps
            compensationSteps = Math.max(Math.min(compensationSteps, maxExposure), minExposure)
            if (parameters.exposureCompensation == compensationSteps) {
            } else {
                parameters.exposureCompensation = compensationSteps
            }
        } else {
        }
    }

    fun setBestPreviewFPS(parameters: Camera.Parameters) {
        setBestPreviewFPS(parameters, MIN_FPS, MAX_FPS)
    }

    fun setBestPreviewFPS(parameters: Camera.Parameters, minFPS: Int, maxFPS: Int) {
        val supportedPreviewFpsRanges = parameters.supportedPreviewFpsRange
        if (supportedPreviewFpsRanges != null && supportedPreviewFpsRanges.isNotEmpty()) {
            var suitableFPSRange: IntArray? = null
            for (fpsRange in supportedPreviewFpsRanges) {
                val thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
                    suitableFPSRange = fpsRange
                    break
                }
            }
            if (suitableFPSRange == null) {
            } else {
                val currentFpsRange = IntArray(2)
                parameters.getPreviewFpsRange(currentFpsRange)
                if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
                } else {
                    parameters.setPreviewFpsRange(suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                            suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX])
                }
            }
        }
    }

    fun setFocusArea(parameters: Camera.Parameters) {
        if (parameters.maxNumFocusAreas > 0) {
            val middleArea = buildMiddleArea(AREA_PER_1000)
            parameters.focusAreas = middleArea
        } else {
        }
    }

    fun setMetering(parameters: Camera.Parameters) {
        if (parameters.maxNumMeteringAreas > 0) {
            val middleArea = buildMiddleArea(AREA_PER_1000)
            parameters.meteringAreas = middleArea
        } else {
        }
    }

    private fun buildMiddleArea(areaPer1000: Int): List<Camera.Area> {
        return listOf(
                Camera.Area(Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1))
    }

    fun setVideoStabilization(parameters: Camera.Parameters) {
        if (parameters.isVideoStabilizationSupported) {
            if (parameters.videoStabilization) {
            } else {
                parameters.videoStabilization = true
            }
        } else {
        }
    }

    fun setBarcodeSceneMode(parameters: Camera.Parameters) {
        if (Camera.Parameters.SCENE_MODE_BARCODE == parameters.sceneMode) {
            return
        }
        val sceneMode = findSettableValue("scene mode",
                parameters.supportedSceneModes,
                Camera.Parameters.SCENE_MODE_BARCODE)
        if (sceneMode != null) {
            parameters.sceneMode = sceneMode
        }
    }

    fun setZoom(parameters: Camera.Parameters, targetZoomRatio: Double) {
        if (parameters.isZoomSupported) {
            val zoom = indexOfClosestZoom(parameters, targetZoomRatio) ?: return
            if (parameters.zoom == zoom) {
            } else {
                parameters.zoom = zoom
            }
        } else {
        }
    }

    private fun indexOfClosestZoom(parameters: Camera.Parameters, targetZoomRatio: Double): Int? {
        val ratios = parameters.zoomRatios
        val maxZoom = parameters.maxZoom
        if (ratios == null || ratios.isEmpty() || ratios.size != maxZoom + 1) {
            return null
        }
        val target100 = 100.0 * targetZoomRatio
        var smallestDiff = Double.POSITIVE_INFINITY
        var closestIndex = 0
        for (i in ratios.indices) {
            val diff = abs(ratios[i] - target100)
            if (diff < smallestDiff) {
                smallestDiff = diff
                closestIndex = i
            }
        }
        return closestIndex
    }

    fun setInvertColor(parameters: Camera.Parameters) {
        if (Camera.Parameters.EFFECT_NEGATIVE == parameters.colorEffect) {
            return
        }
        val colorMode = findSettableValue("color effect",
                parameters.supportedColorEffects,
                Camera.Parameters.EFFECT_NEGATIVE)
        if (colorMode != null) {
            parameters.colorEffect = colorMode
        }
    }

    fun findBestPreviewSizeValue(parameters: Camera.Parameters, screenResolution: Point): Point {
        val rawSupportedSizes = parameters.supportedPreviewSizes
        if (rawSupportedSizes == null) {
            val defaultSize = parameters.previewSize
                    ?: throw IllegalStateException("Parameters contained no preview size!")
            return Point(defaultSize.width, defaultSize.height)
        }
        val screenAspectRatio: Double = if (screenResolution.x < screenResolution.y) {
            screenResolution.x / screenResolution.y.toDouble()
        } else {
            screenResolution.y / screenResolution.x.toDouble()
        }
        // Find a suitable size, with max resolution
        var maxResolution = 0
        var maxResPreviewSize: Camera.Size? = null
        for (size in rawSupportedSizes) {
            val realWidth = size.width
            val realHeight = size.height
            val resolution = realWidth * realHeight
            if (resolution < MIN_PREVIEW_PIXELS) {
                continue
            }
            val isCandidatePortrait = realWidth < realHeight
            val maybeFlippedWidth = if (isCandidatePortrait) realWidth else realHeight
            val maybeFlippedHeight = if (isCandidatePortrait) realHeight else realWidth
            val aspectRatio = maybeFlippedWidth / maybeFlippedHeight.toDouble()
            val distortion = abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                continue
            }
            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                val exactPoint = Point(realWidth, realHeight)
                return exactPoint
            }

            // Resolution is suitable; record the one with max resolution
            if (resolution > maxResolution) {
                maxResolution = resolution
                maxResPreviewSize = size
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (maxResPreviewSize != null) {
            return Point(maxResPreviewSize.width, maxResPreviewSize.height)
        }

        // If there is nothing at all suitable, return current preview size
        val defaultPreview = parameters.previewSize
                ?: throw IllegalStateException("Parameters contained no preview size!")
        return Point(defaultPreview.width, defaultPreview.height)
    }

    private fun findSettableValue(name: String,
                                  supportedValues: Collection<String>?,
                                  vararg desiredValues: String): String? {
        if (supportedValues != null) {
            for (desiredValue in desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    return desiredValue
                }
            }
        }
        return null
    }

    private fun toString(arrays: Collection<IntArray>?): String {
        if (arrays == null || arrays.isEmpty()) {
            return "[]"
        }
        val buffer = StringBuilder()
        buffer.append('[')
        val it = arrays.iterator()
        while (it.hasNext()) {
            buffer.append(it.next().contentToString())
            if (it.hasNext()) {
                buffer.append(", ")
            }
        }
        buffer.append(']')
        return buffer.toString()
    }

    private fun toString(areas: Iterable<Camera.Area>?): String? {
        if (areas == null) {
            return null
        }
        val result = StringBuilder()
        for (area in areas) {
            result.append(area.rect).append(':').append(area.weight).append(' ')
        }
        return result.toString()
    }

    fun collectStats(parameters: Camera.Parameters): String {
        return collectStats(parameters.flatten())
    }

    fun collectStats(flattenedParams: CharSequence?): String {
        val result = StringBuilder(1000)
        result.append("BOARD=").append(Build.BOARD).append('\n')
        result.append("BRAND=").append(Build.BRAND).append('\n')
        result.append("CPU_ABI=").append(Build.CPU_ABI).append('\n')
        result.append("DEVICE=").append(Build.DEVICE).append('\n')
        result.append("DISPLAY=").append(Build.DISPLAY).append('\n')
        result.append("FINGERPRINT=").append(Build.FINGERPRINT).append('\n')
        result.append("HOST=").append(Build.HOST).append('\n')
        result.append("ID=").append(Build.ID).append('\n')
        result.append("MANUFACTURER=").append(Build.MANUFACTURER).append('\n')
        result.append("MODEL=").append(Build.MODEL).append('\n')
        result.append("PRODUCT=").append(Build.PRODUCT).append('\n')
        result.append("TAGS=").append(Build.TAGS).append('\n')
        result.append("TIME=").append(Build.TIME).append('\n')
        result.append("TYPE=").append(Build.TYPE).append('\n')
        result.append("USER=").append(Build.USER).append('\n')
        result.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append('\n')
        result.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append('\n')
        result.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append('\n')
        result.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append('\n')
        if (flattenedParams != null) {
            val params = SEMICOLON.split(flattenedParams)
            Arrays.sort(params)
            for (param in params) {
                result.append(param).append('\n')
            }
        }
        return result.toString()
    }
}