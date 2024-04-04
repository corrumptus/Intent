package lazarini.lucas.intent

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    private lateinit var permissaoChamada: ActivityResultLauncher<String>

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
                val navegadorIntent: Intent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }
            R.id.callMi -> {
                // checks if the phone android version is the lower than 23
                // the permission system changes in the android API 23
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    val numeroUri: Uri = Uri.parse("tel:${amb.parametroTv.text}")
                    val chamarIntent: Intent = Intent(ACTION_CALL)
                    chamarIntent.data = numeroUri
                    startActivity(chamarIntent)
                    return true
                }

                // phone android version greater than 23

                // if the permission was granted it will call else will request the permission
                if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                    val numeroUri: Uri = Uri.parse("tel:${amb.parametroTv.text}")
                    val chamarIntent: Intent = Intent(ACTION_CALL)
                    chamarIntent.data = numeroUri
                    startActivity(chamarIntent)
                } else {
                    permissaoChamada.launch(CALL_PHONE)
                }

                true
            }
            R.id.dialMi -> { true }
            R.id.pickMi -> { true }
            R.id.chooserMi -> { true }
            else -> { false }
        }
    }
}