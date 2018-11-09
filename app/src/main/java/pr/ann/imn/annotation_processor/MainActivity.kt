package pr.ann.imn.annotation_processor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pr.ann.imn.generator.GenName
import pr.ann.imn.generator.Kson

@GenName
class Hello

@Kson
data class User(
    val name: String,
    val email: String
)


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = User(
            name = "Test",
            email = "test@email.com"
        )
        textView.text = user.toJson()
    }
}


