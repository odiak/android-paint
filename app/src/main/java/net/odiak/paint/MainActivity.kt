package net.odiak.paint

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val drawView = findViewById(R.id.drawView) as DrawView
        val clearButton = findViewById(R.id.clearButton)!!

        clearButton.setOnClickListener {
            drawView.clear()
        }
    }
}
