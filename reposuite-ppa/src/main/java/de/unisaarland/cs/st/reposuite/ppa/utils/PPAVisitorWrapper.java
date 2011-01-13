package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;


public class PPAVisitorWrapper extends ASTVisitor {
	
	private Set<ASTVisitor> visitors = new HashSet<ASTVisitor>();
	
	public PPAVisitorWrapper(final Set<ASTVisitor> visitors) {
		this.visitors = visitors;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	@Override
	public void endVisit(final AnnotationTypeDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final AnnotationTypeMemberDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final AnonymousClassDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ArrayAccess node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ArrayCreation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ArrayInitializer node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ArrayType node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final AssertStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final Assignment node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final Block node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final BlockComment node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final BooleanLiteral node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final BreakStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final CastExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final CatchClause node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final CharacterLiteral node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ClassInstanceCreation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final CompilationUnit node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ConditionalExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ConstructorInvocation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ContinueStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final DoStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final EmptyStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final EnhancedForStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final EnumConstantDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final EnumDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ExpressionStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final FieldAccess node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final FieldDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ForStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final IfStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ImportDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final InfixExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final Initializer node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final InstanceofExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final Javadoc node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final LabeledStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final LineComment node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MarkerAnnotation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MemberRef node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MemberValuePair node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MethodDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MethodInvocation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MethodRef node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final MethodRefParameter node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final Modifier node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final NormalAnnotation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final NullLiteral node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final NumberLiteral node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final PackageDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ParameterizedType node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ParenthesizedExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final PostfixExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final PrefixExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final PrimitiveType node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final QualifiedName node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final QualifiedType node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ReturnStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SimpleName node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SimpleType node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SingleMemberAnnotation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SingleVariableDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final StringLiteral node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SuperConstructorInvocation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SuperFieldAccess node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SuperMethodInvocation node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SwitchCase node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SwitchStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final SynchronizedStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TagElement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TextElement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ThisExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final ThrowStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TryStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TypeDeclaration node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TypeDeclarationStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TypeLiteral node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final TypeParameter node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final VariableDeclarationExpression node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final VariableDeclarationFragment node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final VariableDeclarationStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final WhileStatement node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void endVisit(final WildcardType node) {
		for (ASTVisitor v : visitors) {
			v.endVisit(node);
		}
	}
	
	@Override
	public void postVisit(final ASTNode node) {
		for (ASTVisitor v : visitors) {
			v.postVisit(node);
		}
	}
	
	@Override
	public void preVisit(final ASTNode node) {
		for (ASTVisitor v : visitors) {
			v.preVisit(node);
		}
	}
}
