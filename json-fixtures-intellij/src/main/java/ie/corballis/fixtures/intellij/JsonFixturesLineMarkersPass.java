package ie.corballis.fixtures.intellij;import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;import java.lang.Object;import java.lang.Override;import java.lang.System;

public class JsonFixturesLineMarkersPass extends TextEditorHighlightingPass implements PsiClassVisitorAction {

    private static final Key<Object> KEY = new Key<Object>("JsonFixtures");

    private final Project project;
    private final Document document;
    private final MarkupModel model;

    protected JsonFixturesLineMarkersPass(Project project, Document document, MarkupModel model) {
        super(project, document);
        this.project = project;
        this.document = document;
        this.model = model;
    }

    @Override
    public void doCollectInformation(@NotNull ProgressIndicator progress) {
    }

    @Override
    public void doApplyInformationToEditor() {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null) {
            return;
        }
        psiFile.acceptChildren(new PsiClassVisitor(this));

    }

    @Override
    public void execute(PsiClass psiClass) {
        clearMarkersFrom(model);

        for (PsiField field : psiClass.getAllFields()) {
            for (PsiAnnotation annotation : field.getModifierList().getAnnotations()) {
                PsiAnnotationParameterList parameterList = annotation.getParameterList();
                System.out.println(annotation);
                System.out.println(parameterList);
                //                RangeHighlighter rangeHighlighter = model.addLineHighlighter(line, FIRST, null);
//                rangeHighlighter.setGutterIconRenderer(new InfinitestGutterIconRenderer(each));
//                rangeHighlighter.putUserData(KEY, each);

            }
        }
    }

    private void clearMarkersFrom(MarkupModel model) {
        for (RangeHighlighter each : model.getAllHighlighters()) {
            if (each.getUserData(KEY) != null) {
                model.removeHighlighter(each);
            }
        }
    }
}
