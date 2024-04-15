package geometry_objects.angle;

import geometry_objects.angle.comparators.AngleStructureComparator;
import utilities.eq_classes.EquivalenceClasses;

/**
 * Given the figure below:
 * 
 *    A-------B----C-----------D
 *     \
 *      \
 *       \
 *        E
 *         \
 *          \
 *           F
 * 
 * Equivalence classes structure we want:
 * 
 *   canonical = BAE
 *   rest = BAF, CAE, DAE, CAF, DAF
 */
public class AngleEquivalenceClasses extends EquivalenceClasses<Angle>
{
	// TODO
}