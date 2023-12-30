package com.ozanyazici.coroutineexception

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //lifecycleScope tarafından başlatılan coroutine'lar, bağlı oldukları Activity veya Fragment'in yaşam döngüsü sona erdiğinde
        //otomatik olarak iptal edilir. Bu özelliği, coroutine'ların gereksiz kaynak tüketimini önlemek
        //ve bellek sızıntılarını engellemek için kullanışlı kılar.
        //CoroutineScope dan daha kullanışlı çünkü lifecycle ile tam uymlu ve bazı işlemleri otomatik yapıyor.

        val handler = CoroutineExceptionHandler {coroutineContext, throwable ->
            println("exception: " + throwable.localizedMessage)
        }

        /*
        lifecycleScope.launch(handler) {
            throw Exception("error")
        }

        lifecycleScope.launch(handler) {
            throw Exception("error2")
        }
         */

        //Aynı scope içerisindeki bir coroutine hata verdiğinde diğer coroutineler çalışmaz. Bu durum SupervisorScope ile halledilir.
        //supervisorScope, coroutineScope'a benzer bir kapsam (scope) oluşturan bir yapıdır.
        //Ancak, bir farkla: supervisorScope içindeki bir alt coroutine'ın başarısız olması (exception fırlatması) durumunda,
        //sadece ilgili alt coroutine iptal edilir ve diğer alt coroutine'lar ve ana coroutine devam eder.
        lifecycleScope.launch(handler) {
            supervisorScope {
                launch {
                    throw Exception("error")
                }
                launch {
                    delay(500)
                    println("this is executed")
                }
            }

        }
        lifecycleScope.launch(Dispatchers.Main + handler) {
            supervisorScope {
                launch {
                    throw Exception("Error!!")
                }
                launch {
                    delay(1000)
                    println("pikaçhu")
                }
            }
        }
    }
}