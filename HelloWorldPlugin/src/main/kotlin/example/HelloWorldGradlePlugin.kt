package example

import org.gradle.api.Plugin
import org.jetbrains.kotlin.gradle.plugin.*
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions

class HelloWorldGradlePlugin : Plugin<Project>, KotlinGradleSubplugin<AbstractCompile> {

    override fun apply(p0: Project) = Unit

    override fun getCompilerPluginId(): String = "example.compilerplugin.helloworld"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact("example", "HelloWorldPlugin", "1.0.0")

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean = true

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        return listOf(
            SubpluginOption(
                key = "outputDir",
                value = "${project.buildDir.absolutePath}/generated/ktPlugin"
            )
        )
    }
}

