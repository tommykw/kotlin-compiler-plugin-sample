package example

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalFileSystem
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalVirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.SingleRootFileViewProvider
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

object ConfigurationKeys {
    val OUTPUT_DIR_KEY: CompilerConfigurationKey<String> =
        CompilerConfigurationKey.create<String>("Output directory")
}

class HelloWorldCommandLineProcessor : CommandLineProcessor {
    companion object {
        val OUTPUT_DIR_OPTION: CliOption = CliOption(
            optionName = "outputDir",
            valueDescription = "",
            description = ""
        )
    }

    override val pluginId: String = "example.compilerplugin.helloworld"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(OUTPUT_DIR_OPTION)

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option) {
            OUTPUT_DIR_OPTION -> configuration.put(ConfigurationKeys.OUTPUT_DIR_KEY, value)
            else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
        }
    }
}

class HelloWorldComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        configuration.get(ConfigurationKeys.OUTPUT_DIR_KEY)?.let { outputDir ->
            AnalysisHandlerExtension.registerExtension(project, HelloWorldPluginExtension(outputDir))
        }
    }
}

class HelloWorldPluginExtension(
    private val outputDir: String
): AnalysisHandlerExtension {
    override fun doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
    ): AnalysisResult? {
        files as ArrayList

        for (i in files.indices) {
            val oldFile = files[i]
            val newFileText = HelloWorldTreeVisitor(oldFile).buildOutput()

            newFileText?.let {
                files[i] = createNewKtFile(
                    name = oldFile.name,
                    content = newFileText,
                    outputDir = outputDir,
                    fileManager = oldFile.manager
                )
            }
        }

        return null
    }
}

// @see https://github.com/acejingbo/Tracer-Kotlin-Compiler-Plugin/blob/main/TracerPlugin/src/main/kotlin/example/TracerCompilerPlugin.kt#L94
private fun createNewKtFile(
    name: String,
    content: String,
    outputDir: String,
    fileManager: PsiManager
): KtFile {
    val directory = File(outputDir).apply { mkdirs() }
    val file = File(directory, name).apply { writeText(content) }
    val virtualFile = CoreLocalVirtualFile(CoreLocalFileSystem(), file)
    return KtFile(SingleRootFileViewProvider(fileManager, virtualFile), false)
}