package com.jetbrains.pyqt;

import com.jetbrains.python.PyNames;
import com.jetbrains.python.psi.Callable;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyQualifiedExpression;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import com.jetbrains.python.psi.types.PyClassTypeImpl;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.PyTypeProviderBase;
import com.jetbrains.python.psi.types.TypeEvalContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User : ktisha
 */
public class PyQtTypeProvider extends PyTypeProviderBase {
  private static final String ourQtBoundSignal = "QtCore.pyqtBoundSignal";
  private static final String ourQt4Signal = "pyqtSignal";

  @Override
  public PyType getReturnType(@NotNull PyFunction function, @Nullable PyQualifiedExpression callSite, @NotNull TypeEvalContext context) {
    if (PyNames.INIT.equals(function.getName())) {
      final PyClass containingClass = function.getContainingClass();
      if (containingClass != null && ourQt4Signal.equals(containingClass.getName())) {
        final String classQName = containingClass.getQualifiedName();
        if (classQName != null) {
          final QualifiedName name = QualifiedName.fromDottedString(classQName);
          final String qtVersion = name.getComponents().get(0);
          final PyClass aClass = PyClassNameIndex.findClass(qtVersion + "." + ourQtBoundSignal, function.getProject());
          if (aClass != null)
            return new PyClassTypeImpl(aClass, false);
        }
      }
    }
    return null;
  }

  @Nullable
  @Override
  public PyType getCallableType(@NotNull Callable callable, @NotNull TypeEvalContext context) {
    if (callable instanceof PyFunction) {
      final String qualifiedName = callable.getQualifiedName();
      if (qualifiedName != null && qualifiedName.startsWith("PyQt")){
        final QualifiedName name = QualifiedName.fromDottedString(qualifiedName);
        final String qtVersion = name.getComponents().get(0);
        final String docstring = ((PyFunction)callable).getDocStringValue();
        if (docstring != null && docstring.contains("[signal]")) {
          final PyClass aClass = PyClassNameIndex.findClass(qtVersion + "." + ourQtBoundSignal, callable.getProject());
          if (aClass != null)
            return new PyClassTypeImpl(aClass, false);
        }
      }
    }
    return null;
  }
}
