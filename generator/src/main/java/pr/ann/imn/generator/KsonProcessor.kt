package pr.ann.imn.generator

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class KsonProcessor : AbstractProcessor() {

    override fun init(p0: ProcessingEnvironment) {
        super.init(p0)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("getSupportedAnnotationTypes")
        return mutableSetOf(Kson::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        println("process")
        roundEnv.getElementsAnnotatedWith(Kson::class.java)
            .forEach {
                println("Processing: ${it.simpleName}")
                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                generateClass(it, pack)
            }
        return true
    }

    private fun generateClass(klass: Element, pack: String) {
        val fileName = "${klass.simpleName}KsonUtil"
        val tripleQuote = "\"\"\""
        val body = mutableListOf<String>()
        klass.enclosedElements.forEach { element ->
            when(element.kind) {
                ElementKind.FIELD -> {
                    body.add(""""$element": ${'$'}$element""")
                }
                else -> { }
            }
        }
        val bodyString = body.joinToString(", ")
        val statement = """return $tripleQuote{$bodyString}$tripleQuote"""
        val file = FileSpec.builder(pack, fileName)
            .addFunction(FunSpec.builder("toJson")
                .returns(String::class)
                .receiver(klass.asType().asTypeName())
                .addStatement(statement)
                .build())
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}