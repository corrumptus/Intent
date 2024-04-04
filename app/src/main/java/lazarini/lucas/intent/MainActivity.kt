package lazarini.lucas.intent

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
// import androidx.activity.result.ActivityResult // parl not lambda
// import androidx.activity.result.ActivityResultCallback // parl not lambda
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import lazarini.lucas.intent.Constantes.PARAMETRO_EXTRA
// import lazarini.lucas.intent.Constantes.PARAMETRO_REQUEST_CODE // startActivityForResult
import lazarini.lucas.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // parametro activity result launcher (parl)
    private lateinit var parl: ActivityResultLauncher<Intent>

    private lateinit var permissaoChamada: ActivityResultLauncher<String>

    private lateinit var pegarImagemArl : ActivityResultLauncher<Intent>

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
                parl.launch(it)
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

        permissaoChamada = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permissaoConcedida ->
            if (permissaoConcedida)
                chamarNumero(chamar = true)
            else
                Toast.makeText(this,"Permissao necessaria para continuar", Toast.LENGTH_LONG).show()
        }

        pegarImagemArl= registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.data?.let {
                    imagemUri -> amb.parametroTv.text = imagemUri.toString()
                    Intent(ACTION_VIEW, imagemUri).also { startActivity(it) }
                }
            }
        }
    }

    // forma deprecated de obter os resultados quando lançada uma intent por meio de
    // startActivityForResult
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PARAMETRO_REQUEST_CODE)
            return

        if (resultCode != RESULT_OK)
            return

        if (data == null)
            return

        val parametro = data.getStringExtra(PARAMETRO_EXTRA)
        amb.parametroTv.text = parametro
    }
    */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
                val navegadorIntent: Intent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }
            R.id.callMi -> {
                // checks if the phone android version is the lower than 23
                // the permission system changes in the android API 23
                // before the API 23 the permission is granted in the installation time
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    chamarNumero(chamar = true)
                    return true
                }

                // phone android version greater than 23
                // on the API 23+ the permission is granted dynamically

                // if the permission was granted it will call else will request the permission
                if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED)
                    chamarNumero(chamar = true)
                else
                    permissaoChamada.launch(CALL_PHONE)

                true
            }
            R.id.dialMi -> {
                chamarNumero(chamar = false)
                true
            }
            R.id.pickMi -> {
                val pegarImagemIntent = Intent(ACTION_PICK)
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                pegarImagemArl.launch(pegarImagemIntent)
                true
            }
            R.id.chooserMi -> {
                // para forçar o usuário a escolher um aplicativo mesmo com um padrão setado
                // é necessário 2 intents:
                // a intent mais exterior é do tipo chooser e dentro dela existe outra intent
                // para abrir o tipo de aplicativo requerido
                Uri.parse(amb.parametroTv.text.toString()).let { uri ->
                    Intent(ACTION_VIEW, uri).also { navegadorIntent ->
                        val escolherAppIntent = Intent(ACTION_CHOOSER)
                        escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador favorito")
                        escolherAppIntent.putExtra(EXTRA_INTENT, navegadorIntent)
                        startActivity(escolherAppIntent)
                    }
                }
                true
            }
            else -> { false }
        }
    }

    private fun chamarNumero(chamar: Boolean) {
        val numeroUri: Uri = Uri.parse("tel: ${amb.parametroTv.text}")
        val chamarIntent: Intent = Intent(if(chamar) ACTION_CALL else ACTION_DIAL)
        chamarIntent.data = numeroUri
        startActivity(chamarIntent)
    }
}