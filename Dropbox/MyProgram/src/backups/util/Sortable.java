package util;

/**
 * Interface offering a operation for comparing objects.
 *
 * @version 1.00	first version
 * @author Thomas Brinkhoff
 */
public interface Sortable {

/**
 * Vergleich, ob das Objekt kleiner als das übergebene Objekt ist.
 * @return kleiner?
 * @param obj Vergleichsobjekt
 */
public boolean less (Object obj);
}