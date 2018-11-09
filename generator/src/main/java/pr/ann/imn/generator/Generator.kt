package pr.ann.imn.generator

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(Generator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class Generator: AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GenName::class.java.canonicalName)
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        println("imn process")
        roundEnv.getElementsAnnotatedWith(GenName::class.java)
            .forEach {
                val className = it.simpleName.toString()
                println("Processing: $className")
                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "IMN $className $pack")
                generateClass(className, pack)
            }

        return true
    }

    private fun generateClass(className: String, pack: String) {
        val fileName = "Generated_$className"
        val file = FileSpec.builder(pack, fileName)
            .addType(TypeSpec.classBuilder(fileName)
                .addFunction(FunSpec.builder("getName")
                    .addStatement("return \"World\"")
                    .build())
                .build())
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}