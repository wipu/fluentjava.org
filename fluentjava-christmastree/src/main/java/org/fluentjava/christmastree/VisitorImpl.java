package org.fluentjava.christmastree;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.GenericVisitorWithDefaults;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionFieldDeclaration;

public class VisitorImpl extends GenericVisitorWithDefaults<Void, Void> {

	private static final Logger LOG = Logger.getLogger(VisitorImpl.class);
	private final SpanLabelListener listener;
	private final JavaParserFacade solvingParser;

	VisitorImpl(SpanLabelListener listener, JavaParserFacade solvingParser) {
		this.listener = listener;
		this.solvingParser = solvingParser;
	}

	@Override
	public Void defaultAction(Node n, Void arg) {
		return recurse(n);
	}

	private Void recurse(Node n) {
		for (Node c : n.getChildNodes()) {
			c.accept(this, null);
		}
		return null;
	}

	@Override
	public Void defaultAction(@SuppressWarnings("rawtypes") NodeList n,
			Void arg) {
		LOG.trace("defaultAction, nodelist=" + n);
		for (Object child : n) {
			Node node = (Node) child;
			recurse(node);
		}
		return null;
	}

	@Override
	public Void visit(CompilationUnit n, Void arg) {
		logTree(n, 0);
		recurse(n);
		reportAdditionalTokens(n, null);
		return null;
	}

	private void logTree(Node n, int indentation) {
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < indentation; i++) {
			line.append("  ");
		}
		line.append(n.getClass().getSimpleName());
		if (n instanceof Name || n instanceof SimpleName) {
			line.append(":").append(n);
		}
		LOG.trace(line);
		for (Node c : n.getChildNodes()) {
			logTree(c, indentation + 1);
		}
	}

	private void reportAdditionalTokens(Node n, Scope scope) {
		if (!n.getTokenRange().isPresent()) {
			return;
		}
		for (JavaToken t : n.getTokenRange().get()) {
			Label label = labelOfAdditionalToken(t);
			if (label == null) {
				// not an additional token
				continue;
			}
			Location loc = Location.from(t.getRange().get().begin);
			String text = t.getText();
			listener.spanHasLabel(loc, text, label, scope);
		}
	}

	private static Label labelOfAdditionalToken(JavaToken t) {
		switch (t.getCategory()) {
		case KEYWORD:
			return Label.KEYWORD;
		case SEPARATOR:
			return Label.PUNCTUATION;
		case WHITESPACE_NO_EOL:
			return Label.WHITESPACE;
		default:
			// not to be reported as additional token
			return null;
		}
	}

	@Override
	public Void visit(LineComment n, Void arg) {
		// TODO should we keep newlines also in LabeledLines:
		// comment toString contains newline, we must remove it:
		String raw = n.toString().replaceAll("\n$", "");
		Location loc = Location.of(n);
		listener.spanHasLabel(loc, raw, Label.COMMENT, null);
		return recurse(n);
	}

	@Override
	public Void visit(PackageDeclaration n, Void arg) {
		Scope scope = Scope.PACKAGE_DECLARATION;
		reportName(n.getName(), scope);
		return recurse(n);
	}

	private static Scope scopeOfField(FieldDeclaration field) {
		if (!field.isStatic()) {
			return Scope.FIELD;
		}
		// it's static
		if (field.isFinal()) {
			return Scope.CONSTANT;
		}
		return Scope.STATIC_FIELD;
	}

	@Override
	public Void visit(ClassOrInterfaceDeclaration n, Void arg) {
		SimpleName name = n.getName();
		reportName(name, Scope.CLASS_DECLARATION);
		return recurse(n);
	}

	@Override
	public Void visit(FieldDeclaration n, Void arg) {
		Scope scope = scopeOfField(n);
		reportType(n.getCommonType(), scope);
		for (VariableDeclarator var : n.getVariables()) {
			reportName(var.getName(), scope);
		}
		return recurse(n);
	}

	private void reportType(Type type, Scope scope) {
		if (type.getChildNodes().isEmpty()) {
			// it's a primitive, handled as keyword
			return;
		}

		Scope scopeToUse = scope;
		ResolvedType usage = solvingParser.convertToUsage(type);
		if (usage.isTypeVariable()) {
			scopeToUse = Scope.GENERIC_TYPE_REF;
		}

		Node clazz = type.getChildNodes().get(0);
		listener.spanHasLabel(Location.of(clazz), clazz.toString(), Label.TYPE,
				scopeToUse);

		for (int i = 1; i < type.getChildNodes().size(); i++) {
			Node genericType = type.getChildNodes().get(i);
			listener.spanHasLabel(Location.of(genericType),
					genericType.toString(), Label.TYPE,
					Scope.GENERIC_TYPE_DIAMOND);
		}
	}

	@Override
	public Void visit(TypeParameter n, Void arg) {
		reportName(n.getName(), Scope.GENERIC_TYPE_REF);
		return recurse(n);
	}

	@Override
	public Void visit(MethodDeclaration n, Void arg) {
		reportName(n.getName(), Scope.METHOD_NAME);
		reportType(n.getType(), null);
		for (Parameter param : n.getParameters()) {
			reportName(param.getName(), Scope.PARAMETER);
			reportType(param.getType(), Scope.PARAMETER);
		}
		return recurse(n);
	}

	private void reportName(SimpleName name, Scope scope) {
		Location loc = Location.of(name);
		listener.spanHasLabel(loc, name.asString(), Label.IDENTIFIER, scope);
	}

	private void reportName(Name name, Scope scope) {
		Location loc = Location.of(name);
		listener.spanHasLabel(loc, name.asString(), Label.IDENTIFIER, scope);
	}

	@Override
	public Void visit(ConstructorDeclaration n, Void arg) {
		reportName(n.getName(), Scope.METHOD_NAME);
		for (Parameter param : n.getParameters()) {
			reportName(param.getName(), Scope.PARAMETER);
			reportType(param.getType(), Scope.PARAMETER);
		}
		return recurse(n);
	}

	@Override
	public Void visit(VariableDeclarationExpr n, Void arg) {
		Scope scope = Scope.LOCAL_VAR;
		for (VariableDeclarator var : n.getVariables()) {
			reportName(var.getName(), scope);
			reportType(var.getType(), scope);
		}
		return recurse(n);
	}

	@Override
	public Void visit(SingleMemberAnnotationExpr n, Void arg) {
		reportAnnotation(n);
		return recurse(n);
	}

	@Override
	public Void visit(MarkerAnnotationExpr n, Void arg) {
		reportAnnotation(n);
		return recurse(n);
	}

	private void reportAnnotation(AnnotationExpr n) {
		Name name = n.getName();
		Location loc = Location.of(name);
		listener.spanHasLabel(loc, name.asString(), Label.ANNOTATION, null);
	}

	@Override
	public Void visit(StringLiteralExpr n, Void arg) {
		Location loc = Location.of(n);
		listener.spanHasLabel(loc, "\"" + n.getValue() + "\"",
				Label.STRING_LITERAL, null);
		return recurse(n);
	}

	@Override
	public Void visit(CharLiteralExpr n, Void arg) {
		Location loc = Location.of(n);
		listener.spanHasLabel(loc, "\'" + n.getValue() + "\'",
				Label.CHAR_LITERAL, null);
		return recurse(n);
	}

	@Override
	public Void visit(IntegerLiteralExpr n, Void arg) {
		reportNumberLiteral(n);
		return recurse(n);
	}

	@Override
	public Void visit(LongLiteralExpr n, Void arg) {
		reportNumberLiteral(n);
		return recurse(n);
	}

	@Override
	public Void visit(DoubleLiteralExpr n, Void arg) {
		reportNumberLiteral(n);
		return recurse(n);
	}

	private void reportNumberLiteral(LiteralExpr n) {
		Location loc = Location.of(n);
		listener.spanHasLabel(loc, n.toString(), Label.NUMBER_LITERAL, null);
	}

	@Override
	public Void visit(MethodCallExpr n, Void arg) {
		Scope scope = null;
		ResolvedMethodDeclaration target = targetOf(n);
		if (target.isStatic()) {
			scope = Scope.STATIC_METHOD_CALL;
		} else if (target.isAbstract()) {
			scope = Scope.ABSTRACT_METHOD_CALL;
		}

		SimpleName name = n.getName();
		Location loc = Location.of(name);
		listener.spanHasLabel(loc, name.asString(), Label.IDENTIFIER, scope);
		return recurse(n);
	}

	/**
	 * This is an explicit field access like this.b, a.b or A.b
	 */
	@Override
	public Void visit(FieldAccessExpr n, Void arg) {
		ResolvedFieldDeclaration target = targetOf(n).asField();
		Scope scope = Scope.FIELD;
		if (target.isStatic()) {
			scope = Scope.STATIC_FIELD;
			if (isFinal(target)) {
				scope = Scope.CONSTANT;
			}
		}
		SimpleName name = n.getName();
		Location loc = Location.of(name);
		listener.spanHasLabel(loc, name.asString(), Label.IDENTIFIER, scope);
		return recurse(n);
	}

	@Override
	public Void visit(NameExpr n, Void arg) {
		ResolvedValueDeclaration target = targetOf(n);
		Scope scope = null;
		if (target != null) {
			if (target.isParameter()) {
				scope = Scope.PARAMETER;
			} else if (target.isField()) {
				scope = Scope.FIELD;
				if (target.asField().isStatic()) {
					scope = Scope.STATIC_FIELD;
					if (isFinal(target.asField())) {
						scope = Scope.CONSTANT;
					}
				}
			} else {
				scope = Scope.LOCAL_VAR;
			}
		}

		SimpleName name = n.getName();
		Location loc = Location.of(name);
		listener.spanHasLabel(loc, name.asString(), Label.IDENTIFIER, scope);
		return null;
	}

	private static boolean isFinal(ResolvedFieldDeclaration field) {
		if (field instanceof JavaParserFieldDeclaration) {
			JavaParserFieldDeclaration impl = (JavaParserFieldDeclaration) field;
			return impl.getWrappedNode().hasModifier(Modifier.Keyword.FINAL);
		}
		if (field instanceof ReflectionFieldDeclaration) {
			ReflectionFieldDeclaration impl = (ReflectionFieldDeclaration) field;
			try {
				Field delegate = impl.getClass().getDeclaredField("field");
				delegate.setAccessible(true);
				Field delegateValue = (Field) delegate.get(impl);
				return java.lang.reflect.Modifier
						.isFinal(delegateValue.getModifiers());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}

		}
		throw new UnsupportedOperationException(
				"TODO support " + field.getClass());
	}

	private ResolvedMethodDeclaration targetOf(MethodCallExpr n) {
		LOG.debug("Solving target of " + n);
		SymbolReference<ResolvedMethodDeclaration> solved = solvingParser
				.solve(n);
		return solved.getCorrespondingDeclaration();
	}

	private ResolvedValueDeclaration targetOf(FieldAccessExpr n) {
		LOG.debug("Solving target of " + n);
		SymbolReference<ResolvedValueDeclaration> solved = solvingParser
				.solve(n);
		return solved.getCorrespondingDeclaration();
	}

	private ResolvedValueDeclaration targetOf(NameExpr n) {
		if ("LocalMethodCalls".equals(n.toString())) {
			LOG.debug("here");
		}
		SymbolReference<? extends ResolvedValueDeclaration> solved = solvingParser
				.solve(n);
		if (!solved.isSolved()) {
			LOG.debug("Cannot solve target of " + n);
			return null;
		}
		return solved.getCorrespondingDeclaration();
	}

}
