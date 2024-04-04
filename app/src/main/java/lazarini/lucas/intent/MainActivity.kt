package lazarini.lucas.intent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lazarini.lucas.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        amb.mainTb.apply {
            title = getString(R.string.app_name)
            subtitle = this@MainActivity.javaClass.simpleName
            setSupportActionBar(this)
        }

        amb.entrarParametroBt.setOnClickListener {
            // explicita
		    Intent(this, ParametroActivity::class.java).also {
                it.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString())
                startActivityForResult(it, PARAMETRO_REQUEST_CODE)
            }

            /*
            // implicita
            Intent("ABRA_PARAMETRO_ACTIVTY").also {
                it.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString())
                startActivityForResult(it, PARAMETRO_REQUEST_CODE)
            }
            */
        }
    }
}