package lazarini.lucas.intent

import android.content.Intent
import android.os.Bundle
import android.view.Menu
// import androidx.activity.result.ActivityResult // parl not lambda
// import androidx.activity.result.ActivityResultCallback // parl not lambda
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import lazarini.lucas.intent.Constantes.PARAMETRO_EXTRA
import lazarini.lucas.intent.Constantes.PARAMETRO_REQUEST_CODE
import lazarini.lucas.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // parametro activity result launcher (parl)
    private lateinit var parl: ActivityResultLauncher<Intent>

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

        /*
        parl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object: ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == RESULT_OK){
                        result.data?.getStringExtra(PARAMETRO_EXTRA)?.let{
                            amb.parametroTv.text = it
                        }
                    }
                }
            }
        )
        */

        parl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(PARAMETRO_EXTRA)?.let {
                    amb.parametroTv.text = it
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?):Boolean{
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}