package br.com.cristianodp.vocabularylist.views

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import br.com.cristianodp.vocabularylist.R
import br.com.cristianodp.vocabularylist.adapters.RecyclerCardAdapter
import br.com.cristianodp.vocabularylist.adapters.RecyclerLessonAdapter
import br.com.cristianodp.vocabularylist.ado.CardADO
import br.com.cristianodp.vocabularylist.ado.IFirebaseDatadaseADO
import br.com.cristianodp.vocabularylist.ado.LessonADO
import br.com.cristianodp.vocabularylist.global.getPathCards
import br.com.cristianodp.vocabularylist.global.getPathLesson
import br.com.cristianodp.vocabularylist.models.Card
import br.com.cristianodp.vocabularylist.models.Lesson
import kotlinx.android.synthetic.main.activity_lesson_maintenance.*

class LessonMaintenanceActivity : AppCompatActivity() {

    private lateinit var userId:String
    private lateinit var lessonId:String
    private lateinit var mLessonADO:LessonADO
    private lateinit var mLesson:Lesson
    private lateinit var mCardADO:CardADO
    private lateinit var mRecyclerCardAdapter:RecyclerCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_maintenance)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        userId = intent.getStringExtra("userId")
        lessonId = intent.getStringExtra("lessonId")
        mLesson = Lesson(lessonId)

        intListners()
        showButtonsEditDescription(false)
    }

    private fun intListners() {
        mLessonADO = LessonADO(getPathLesson(userId,lessonId),"SINGLE",object:IFirebaseDatadaseADO.IDataChange{
            override fun notifyDataChanged() {
                if (mLessonADO.getValue() != null){
                    mLesson.description = mLessonADO.getValue()!!.description
                }
                editTextDescription.setText(mLesson.description)
            }
        })

        recyclerView.setLayoutManager(LinearLayoutManager(this@LessonMaintenanceActivity))
        mCardADO = CardADO(getPathCards(userId,lessonId),"CHILD",object :IFirebaseDatadaseADO.IDataChange{
            override fun notifyDataChanged() {
                mRecyclerCardAdapter = RecyclerCardAdapter(this@LessonMaintenanceActivity,mCardADO.list,object : RecyclerCardAdapter.OnItemClickListener{
                    override fun onItemClick(item: Card) {
                        val i = Intent(this@LessonMaintenanceActivity,CardActivity::class.java)
                        i.putExtra("userId",userId)
                        i.putExtra("lessonId",lessonId)
                        i.putExtra("cardId",item.keyId)
                        startActivity(i)

                    }
                })
                recyclerView.adapter  = mRecyclerCardAdapter
                mRecyclerCardAdapter.notifyDataSetChanged()
            }

        })

        editTextDescription.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ( mLesson.description != editTextDescription.text.toString()){
                    showButtonsEditDescription(true)
                }else{
                    showButtonsEditDescription(false)
                }

            }
        })

        floatingActionButtonDone.setOnClickListener {
            mLesson.description = editTextDescription.text.toString()
            mLessonADO.push(mLesson)


        }

        floatingActionButtonCanc.setOnClickListener {
            editTextDescription.setText(mLesson.description)
        }

        floatingActionButtonAdd.setOnClickListener {
            val i = Intent(this@LessonMaintenanceActivity,CardActivity::class.java)
            i.putExtra("userId",userId)
            i.putExtra("lessonId",lessonId)
            i.putExtra("cardId",mLessonADO.genereteId())
            startActivity(i)
        }

    }

    fun showButtonsEditDescription(show: Boolean){
        if (!show){
            floatingActionButtonDone.hide()
            floatingActionButtonCanc.hide()
        }else{
            floatingActionButtonDone.show()
            floatingActionButtonCanc.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }



}