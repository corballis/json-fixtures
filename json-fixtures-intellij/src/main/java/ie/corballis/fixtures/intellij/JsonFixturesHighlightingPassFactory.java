package ie.corballis.fixtures.intellij;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonFixturesHighlightingPassFactory implements TextEditorHighlightingPassFactory {

    private final TextEditorHighlightingPassRegistrar passRegistrar;

    public JsonFixturesHighlightingPassFactory(TextEditorHighlightingPassRegistrar passRegistrar) {
        this.passRegistrar = passRegistrar;
    }

    @Nullable
    @Override
    public TextEditorHighlightingPass createHighlightingPass(
            @NotNull PsiFile psiFile, @NotNull Editor editor) {

        Module module = ModuleUtil.findModuleForPsiElement(psiFile);
        if (module == null) {
            return null;
        }

        return new JsonFixturesLineMarkersPass(module.getProject(), editor.getDocument(), editor.getMarkupModel());
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
        passRegistrar
                .registerTextEditorHighlightingPass(this, TextEditorHighlightingPassRegistrar.Anchor.LAST, Pass.UPDATE_ALL, true,
                                                    true);
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ie.corballis.fixtures.intellij.JsonFixturesHighlightingPassFactory";
    }
}
