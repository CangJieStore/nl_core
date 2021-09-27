package cn.cangjie.timer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/6/22 15:54
 */
class TimerActivity : AppCompatActivity() {

    private var isRunning: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val typeface = ResourcesCompat.getFont(this, R.font.roboto_bold)
        timerProgramCountdown.setCustomTypeface(typeface!!)
        timerProgramCountdown.startCountDown(99999)
        timerProgramCountdown.setCountdownListener(object : CountDownClock.CountdownCallBack {
            override fun countdownAboutToFinish() {
                //TODO Add your code here
            }

            override fun countdownFinished() {
                Toast.makeText(this@TimerActivity, "Finished", Toast.LENGTH_SHORT).show()
                timerProgramCountdown.resetCountdownTimer()
                isRunning = false
                btnPause.isEnabled = false
            }
        })


        btnPause.setOnClickListener {

            if (isRunning) {
                isRunning = false
                timerProgramCountdown.pauseCountDownTimer()
                btnPause.text = "继续计时"
            } else {
                isRunning = true
                timerProgramCountdown.resumeCountDownTimer()
                btnPause.text = "暂停计时"
            }


        }
    }
}