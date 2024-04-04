package lazarini.lucas.intent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lazarini.lucas.intent.databinding.ActivityParametroBinding

class ParametroActivity : AppCompatActivity() {
    private val apb: ActivityParametroBinding by lazy {
        ActivityParametroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)
    }
}