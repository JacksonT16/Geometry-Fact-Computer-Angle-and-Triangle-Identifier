package components;
import input.components.visitor.ComponentNodeVisitor;


public interface ComponentNode
{
	Object accept(ComponentNodeVisitor visitor, Object o);
}
