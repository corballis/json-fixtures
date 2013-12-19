package ie.corballis.fixtures.intellij;import com.intellij.psi.PsiClass;

public interface PsiClassVisitorAction {
    void execute(PsiClass psiClass);
}