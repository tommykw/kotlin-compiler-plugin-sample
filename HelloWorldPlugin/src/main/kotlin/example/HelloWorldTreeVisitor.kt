package example

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class HelloWorldTreeVisitor(
    private val oldFile: KtFile
): KtTreeVisitorVoid() {

    private val patches = mutableListOf<Pair<PsiElement, String>>()

    fun buildOutput(): String? {
        oldFile.accept(this)

        return if (patches.isEmpty()) {
            null
        } else {
            applyPatches(oldFile.text, patches)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val annotation = function.annotationEntries.map { it.text }.toList()

        if (!annotation.contains("@HelloWorld")) {
            return
        }

        val blockBody = function.bodyBlockExpression

        if (function.hasBlockBody() && blockBody != null) {
            patches.add(Pair<PsiElement, String>(blockBody,
                """|{
                |   println("Hello World")
                |   ${function.bodyExpression?.text?.removePrefix("{")?.removeSuffix("}")}
                |}
                |
            """.trimMargin()))
            return
        }
    }
}

// @see https://github.com/acejingbo/Tracer-Kotlin-Compiler-Plugin/blob/main/TracerPlugin/src/main/kotlin/example/TracerTreeVisitor.kt#L81
private fun applyPatches(oldText: String, patches: List<Pair<PsiElement, String>>): String {
    val sortedPatches: List<Pair<PsiElement, String>> = patches.sortedBy { it.first.startOffset }

    var previousPatchEndOffset = -1
    for (patch in sortedPatches) {
        if (patch.first.startOffset <= previousPatchEndOffset) {
            throw IllegalArgumentException("Cannot apply patches. Patches intersect.")
        }
        previousPatchEndOffset = patch.first.endOffset
    }

    var newText = oldText
    for ((element, replacement) in sortedPatches.reversed()) {
        newText = newText.substring(0, element.startOffset) + replacement + newText.substring(element.endOffset)
    }

    return newText
}
